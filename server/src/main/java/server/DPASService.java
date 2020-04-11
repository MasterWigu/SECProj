package server;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import commonClasses.User;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.InvalidAnnouncementException;
import commonClasses.exceptions.KeyException;
import commonClasses.exceptions.UserNotFoundException;
import library.Interfaces.ICommLib;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;


public class DPASService implements ICommLib {

	private List<Announcement> announcements;
	private List<User> users;

	private final Object usersListLock;
	private final Object announcementsListLock;

	private FileSaver fileSaver;
	private boolean useFilesRead;
	private boolean useFilesWrite;


	DPASService(boolean filesRead, boolean filesWrite) {
		useFilesRead = filesRead;
		useFilesWrite = filesWrite;

		announcements = new ArrayList<>();
		users = new ArrayList<>();

		usersListLock = new Object();
		announcementsListLock = new Object();

		fileSaver = FileSaver.getInstance("src\\test\\resources\\");
		if (useFilesRead) {
			users = fileSaver.readUsers();
			announcements = fileSaver.readAnnouncements();
		}
	}


	DPASService() {
		announcements = new ArrayList<>();
		users = new ArrayList<>();

		usersListLock = new Object();
		announcementsListLock = new Object();

		fileSaver = FileSaver.getInstance("src\\main\\resources\\");

		users = fileSaver.readUsers();
		announcements = fileSaver.readAnnouncements();
	}


	@Override
	public String register(PublicKey pk, String username) throws KeyException {
		if (pk == null)
			throw new KeyException();
		User user;
		synchronized (usersListLock) {
			try {
				user = getUserWithPk(pk);
			} catch (UserNotFoundException e) {
				user = new User(users.size()+1, pk, username);
				users.add(user);
				if (useFilesWrite)
					fileSaver.writeUsers(users);
			}
		}
		return "Successful, your id is "+ user.getId();
	}

	private boolean checkRepeatedAnn(Announcement a) {
		for (Announcement a1 : announcements) {
			if (a1.equals(a)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String post(PublicKey key, char[] message, Announcement[] a, long time, byte[] sign) throws UserNotFoundException, InvalidAnnouncementException {
		synchronized (announcementsListLock) {
			User user;
			try {
				user = getUserWithPk(key);
			} catch (UserNotFoundException e) {
				throw new UserNotFoundException();
			}
			Announcement tempAnn = new Announcement(message, user, a, 0, time, sign);
			if (!MessageSigner.verify(tempAnn)) {
				throw new InvalidAnnouncementException();
			}
			if (!checkRepeatedAnn(tempAnn)) {
				tempAnn.setId(announcements.size()+1);
				announcements.add(tempAnn);
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
