package server;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import commonClasses.User;
import commonClasses.exceptions.*;
import javafx.beans.InvalidationListener;
import library.Interfaces.ICommLib;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class DPASService implements ICommLib {

	private List<Announcement> announcements;


	private List<User> users;
	private Integer registerWts;
	private final Object usersListLock;

	private HashMap<PublicKey, HashMap<Integer, ArrayList<Announcement>>> personalBoards;
	private HashMap<PublicKey, Integer> personalWtss;
	private final HashMap<PublicKey, Object> personalBoardLocks;

	private HashMap<Integer, ArrayList<Announcement>> generalBoard;
	private Integer generalWts;
	private final Object generalBoardLock;




	private FileSaver fileSaver;
	private boolean useFilesRead;
	private boolean useFilesWrite;

/*
	DPASService(boolean filesRead, boolean filesWrite) {
		useFilesRead = filesRead;
		useFilesWrite = filesWrite;

		announcements = new ArrayList<>();
		users = new ArrayList<>();

		usersListLock = new Object();
		announcementsListLock = new Object();

		fileSaver = FileSaver.getInstance("src\\test\\resources\\", 1);
		if (useFilesRead) {
			users = fileSaver.readUsers();
			announcements = fileSaver.readAnnouncements();
		}
	}*/


	DPASService(int id) {
		announcements = new ArrayList<>();
		users = new ArrayList<>();

		usersListLock = new Object();
		personalBoardLocks = new HashMap<>();
		personalWtss = new HashMap<>();

		generalBoardLock = new Object();

		fileSaver = FileSaver.getInstance("src\\main\\resources\\", id);

		users = fileSaver.readUsers();
		announcements = fileSaver.readAnnouncements();

		useFilesRead = true;
		useFilesWrite = true;
	}


	@Override
	public String register(PublicKey pk, int wts) throws KeyException, InvalidWtsException {
		//TODO remove this shit/add wts verification
		if (wts <= 0) {
			throw new InvalidWtsException();
		}

		if (pk == null)
			throw new KeyException();
		User user;
		synchronized (usersListLock) {
			try {
				user = getUserWithPk(pk);
			} catch (UserNotFoundException e) {
				user = new User(users.size()+1, pk);
				users.add(user);
				personalBoardLocks.put(pk, new Object());
				personalBoards.put(pk, new HashMap<Integer, ArrayList<Announcement>>());
				personalWtss.put(pk, 0);
				registerWts++;
				if (useFilesWrite)
					fileSaver.writeUsers(users);
			}
		}
		return "Successful, your id is "+ user.getId();
	}

	@Override
	public int getRegisterWts() {
		int tempWts;
		synchronized (usersListLock) {
			tempWts = registerWts;
		}
		return tempWts;
	}

	@Override
	public int getChannelWts(int board, PublicKey ownerPk) throws UserNotFoundException {
		int tempWts;
		if (board == 0) {
			synchronized (generalBoardLock) {
				tempWts = generalWts;
			}
		}
		else {
			if (!personalBoards.containsKey(ownerPk)) {
				throw new UserNotFoundException();
			}
			synchronized (personalBoardLocks.get(ownerPk)) {
				tempWts = personalWtss.get(ownerPk);
			}
		}
		return tempWts;
	}

	private boolean checkRepeatedAnn(Announcement a) {
		//TODO refactor
		for (Announcement a1 : announcements) {
			if (a1.equals(a)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String post(PublicKey pk, Announcement ann, int wts) throws UserNotFoundException, InvalidAnnouncementException {
		if (!personalBoards.containsKey(ann.getCreator().getPk())) {
			throw new UserNotFoundException();
		}
		User creator = getUserWithPk(ann.getCreator().getPk());
		synchronized (personalBoardLocks.get(ann.getCreator().getPk())) {
			ann.setCreator(creator);
			if (ann.getBoard() != 0 || !pk.equals(ann.getCreator().getPk()))
				throw new InvalidAnnouncementException();

			if (!MessageSigner.verify(ann, pk) || !MessageSigner.verify(ann)) {
				throw new InvalidAnnouncementException();
			}

			if (!checkRepeatedAnn(ann)) {
				String id = "P" + creator.getId() + "_" + (personalBoards.get(creator.getPk()).size() + 1);
				ann.setId(id.toCharArray());
				personalBoards.get(creator.getPk()).put(wts, new ArrayList<Announcement>());
				personalBoards.get(creator.getPk()).get(wts).add(ann);
				personalWtss.put(creator.getPk(), personalWtss.get(creator.getPk()) + 1);
				if (useFilesWrite)
					fileSaver.writeAnnouncements(announcements);
				return "Announcement successfully posted with id " + announcements.size() + " to personal board.";
			}

		}
		return "Duplicate Announcement";
	}

	@Override
	public String postGeneral(PublicKey key, char[] message, Announcement[] a, long time, byte[] sign) throws UserNotFoundException, InvalidAnnouncementException {
		synchronized (announcementsListLock) {
			User user;
			try {
				user = getUserWithPk(key);
			} catch (UserNotFoundException e) {
				throw new UserNotFoundException();
			}
			Announcement tempAnn = new Announcement(message, user, a, 1, time, sign);
			if (!MessageSigner.verify(tempAnn)) {
				throw new InvalidAnnouncementException();
			}

			if (!checkRepeatedAnn(tempAnn)) {
				tempAnn.setId(announcements.size()+1);
				announcements.add(tempAnn);
				if (useFilesWrite)
					fileSaver.writeAnnouncements(announcements);
				return "Announcement successfully posted with id " + announcements.size() + " to general board";
			}

		}
		return "Duplicate Announcement";
	}

	@Override
	public Announcement[] read(PublicKey key, int number) throws UserNotFoundException {
		List<Announcement> tempAnnouncements = new ArrayList<>();
		boolean sendAll = number==0;
		for (Announcement a : announcements) {
			if (a.getBoard()==0 && a.getCreator() == getUserWithPk(key)) {
				tempAnnouncements.add(a);
			}
			if (--number == 0 && !sendAll) {
				break;
			}
		}
		return tempAnnouncements.toArray(new Announcement[0]);
	}

	@Override
	public Announcement[] readGeneral(int number) {
		List<Announcement> tempAnnouncements = new ArrayList<>();
		boolean sendAll = number==0;
		for (Announcement a : announcements) {
			if (a.getBoard()==1) {
				tempAnnouncements.add(a);
			}
			if ((--number == 0) && !sendAll) {
				break;
			}
		}
		return tempAnnouncements.toArray(new Announcement[0]);
	}

	@Override
	public Announcement getAnnouncementById(int id) throws AnnouncementNotFoundException {
		return getAnnouncementWithId(id);
	}

	@Override
	public User getUserById(int id) throws UserNotFoundException {
		return getUserWithId(id);
	}


	private User getUserWithPk(PublicKey pk) throws UserNotFoundException {
		for (User u : users) {
			if (u.getPk().equals(pk)) {
				return u;
			}
		}
		throw new UserNotFoundException();
	}

	private User getUserWithId(int id) throws UserNotFoundException {
		for (User u : users) {
			if (u.getId() == id) {
				return u;
			}
		}
		throw new UserNotFoundException();
	}

	private Announcement getAnnouncementWithId(int id) throws AnnouncementNotFoundException {
		for (Announcement a : announcements) {
			if (a.getId() == id) {
				return a;
			}
		}
		throw new AnnouncementNotFoundException();
	}
}
