package server;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import commonClasses.User;
import commonClasses.exceptions.*;
import library.Interfaces.ICommLib;
import library.Packet;
import org.apache.commons.lang3.SerializationUtils;
import server.DataSerializables.GeneralBoardData;
import server.DataSerializables.PersonalBoardsData;
import server.DataSerializables.UsersData;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class DPASService implements ICommLib {

	private ArrayList<User> users;
	private Integer registerWts;
	private final Object usersListLock;

	private HashMap<PublicKey, ArrayList<Announcement>> personalBoards;
	private HashMap<PublicKey, ArrayList<Announcement>> personalBoardBuffers;
	private HashMap<PublicKey, Integer> personalWtss;
	private final HashMap<PublicKey, Object> personalBoardLocks;

	private HashMap<Integer, ArrayList<Announcement>> generalBoard;
	private Integer generalWts;
	private final Object generalBoardLock;

	private FileSaver fileSaver;
	private boolean useFilesRead;
	private boolean useFilesWrite;


	DPASService(boolean filesRead, boolean filesWrite) {
		useFilesRead = filesRead;
		useFilesWrite = filesWrite;

		users = new ArrayList<>();
		usersListLock = new Object();
		registerWts = 0;

		personalBoards = new HashMap<>();
		personalBoardBuffers = new HashMap<>();
		personalBoardLocks = new HashMap<>();
		personalWtss = new HashMap<>();


		generalBoard = new HashMap<>();
		generalBoardLock = new Object();
		generalWts = 0;


		fileSaver = FileSaver.getInstance("src\\test\\resources\\", 1);
		loadFiles();
	}


	DPASService(int id) {
		users = new ArrayList<>();
		usersListLock = new Object();
		registerWts = 0;

		personalBoards = new HashMap<>();
		personalBoardBuffers = new HashMap<>();
		personalBoardLocks = new HashMap<>();
		personalWtss = new HashMap<>();


		generalBoard = new HashMap<>();
		generalBoardLock = new Object();
		generalWts = 0;

		fileSaver = FileSaver.getInstance("src\\main\\resources\\", id);


		loadFiles();

		useFilesRead = false;
		useFilesWrite = false;
	}

	private void loadFiles() {
		if (!useFilesRead)
			return;
		UsersData usersData = fileSaver.readUsers();
		users = usersData.getUsers();
		registerWts = usersData.getRegisterWts();


		GeneralBoardData generalBoardData = fileSaver.readGeneralBoard();
		generalBoard = generalBoardData.getGeneralBoard();
		generalWts = generalBoardData.getGeneralWts();


		PersonalBoardsData personalBoardsData = fileSaver.readPersonalBoards();
		personalBoards = personalBoardsData.getPersonalBoards();
		personalBoardBuffers = personalBoardsData.getPersonalBoardBuffers();
		personalWtss = personalBoardsData.getPersonalWtss();
	}

	private void savePersonalBoards() {
		if (!useFilesWrite)
			return;
		PersonalBoardsData personalBoardsData = new PersonalBoardsData(personalBoards, personalBoardBuffers, personalWtss);
		fileSaver.writePersonalBoards(personalBoardsData);
	}

	private void saveGeneralBoard() {
		if (!useFilesWrite)
			return;
		GeneralBoardData generalBoardData = new GeneralBoardData(generalBoard, generalWts);
		fileSaver.writeGeneralBoard(generalBoardData);
	}

	private void saveUsers() {
		if (!useFilesWrite)
			return;
		UsersData usersData = new UsersData(users, registerWts);
		fileSaver.writeUsers(usersData);
	}



	@Override
	public String register(PublicKey pk, int wts) throws KeyException, InvalidWtsException {
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
				personalBoards.put(pk, new ArrayList<Announcement>());
				personalBoardBuffers.put(pk, new ArrayList<Announcement>());
				personalWtss.put(pk, 0);
				registerWts++;
				saveUsers();
			}
		}
		return "Successfully logged in, your id is "+ user.getId();
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
		if (board == 1) {
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
		try {
			getAnnouncementWithId(a.getId());
		} catch (AnnouncementNotFoundException e) {
			return false;
		}
		return true;
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

			if (ann.getRefs() != null)
				for (Announcement a : ann.getRefs()) {
					if (!MessageSigner.verify(ann)) {
						throw new InvalidAnnouncementException();
					}
					try {
						getAnnouncementWithId(a.getId());
					} catch (AnnouncementNotFoundException e) {
						throw new InvalidAnnouncementException();
					}
				}

			String id = "P" + creator.getId() + "_" + wts;
			ann.setId(id.toCharArray());
			if (checkRepeatedAnn(ann)) {
				return "Duplicate Announcement";
			}

			if (wts != ann.getWts())
				throw new InvalidAnnouncementException();

			if (wts <= personalWtss.get(pk))
				throw new InvalidAnnouncementException();

			if (wts == personalWtss.get(pk)+1) {
				personalBoards.get(creator.getPk()).add(ann);
				personalWtss.put(creator.getPk(), personalWtss.get(creator.getPk()) + 1);

				//check if any packet in buffer can be posted
				boolean again = true;
				while (again) {
					again = false;
					for (Announcement a : personalBoardBuffers.get(pk)) {
						if (a.getWts() == personalWtss.get(pk)+1) {
							personalBoards.get(creator.getPk()).add(a);
							personalWtss.put(creator.getPk(), personalWtss.get(creator.getPk()) + 1);
							again = true;
						}
					}
				}

			}
			else { // if we still need packet before this one, put in buffer
				personalBoardBuffers.get(pk).add(ann);
			}
			savePersonalBoards();

			return "Announcement successfully posted with id " + id + " to personal board.";
		}
	}

	@Override
	public String postGeneral(PublicKey key, Announcement ann, int wts) throws UserNotFoundException, InvalidAnnouncementException {
		if (!personalBoards.containsKey(ann.getCreator().getPk())) { // check if user exists
			throw new UserNotFoundException();
		}

		User creator = getUserWithPk(ann.getCreator().getPk());
		synchronized (generalBoardLock) {
			ann.setCreator(creator);
			if (ann.getBoard() != 1 || !key.equals(ann.getCreator().getPk()))
				throw new InvalidAnnouncementException();

			if (!MessageSigner.verify(ann, key) || !MessageSigner.verify(ann)) {
				throw new InvalidAnnouncementException();
			}


			String id = "G" + creator.getId() + "_" + wts;
			ann.setId(id.toCharArray());
			if (checkRepeatedAnn(ann)) {
				return "Duplicate Announcement";
			}

			if (wts > generalWts+1)
				throw new InvalidAnnouncementException();

			if (!generalBoard.containsKey(wts)) {
				generalBoard.put(wts, new ArrayList<Announcement>());
			}

			generalBoard.get(wts).add(ann);

			if (generalWts < wts) // if this ann is late, and the generalWts is now greater, we dont update
				generalWts = wts;

			saveGeneralBoard();

			return "Announcement successfully posted with id " + id + " to general board.";
		}
	}


	@Override
	public HashMap<Integer, ArrayList<Announcement>> read(PublicKey key, Packet packet) throws UserNotFoundException {
		if (!personalBoards.containsKey(key)) { // check if user exists
			throw new UserNotFoundException();
		}
		HashMap<Integer, ArrayList<Announcement>> outMap = new HashMap<>();

		synchronized (personalBoardLocks.get(key)) {
			packet.setWts(personalWtss.get(key));
			outMap.put(-1, SerializationUtils.clone(personalBoards.get(key)));
		}
		return outMap;
	}

	@Override
	public HashMap<Integer, ArrayList<Announcement>> readGeneral(Packet packet) {
		HashMap<Integer, ArrayList<Announcement>> outMap;

		synchronized (generalBoardLock) {
			packet.setWts(generalWts);
			outMap = SerializationUtils.clone(generalBoard);
		}
		return outMap;
	}

	@Override
	public void readWb(PublicKey pk, Map<Integer, ArrayList<Announcement>> announcements) throws UserNotFoundException, InvalidAnnouncementException {
		if (!personalBoards.containsKey(pk)) { // check if user exists
			throw new UserNotFoundException();
		}
		User boardOwner = getUserWithPk(announcements.get(-1).get(0).getCreator().getPk());
		if (!announcements.keySet().contains(-1) || announcements.keySet().size() != 1) {
			throw new InvalidAnnouncementException();
		}

		int maxWts = 0;

		synchronized (personalBoardLocks.get(boardOwner.getPk())) {
			for (Announcement ann : announcements.get(-1)) {
				if (MessageSigner.verify(ann, boardOwner.getPk())) {
					if (!personalBoards.get(boardOwner.getPk()).contains(ann)) {
						personalBoards.get(boardOwner.getPk()).add(ann);
						if (ann.getWts() > maxWts) {
							maxWts = ann.getWts();
						}
					}
				}
			}
			if (personalWtss.get(boardOwner.getPk()) < maxWts)
				personalWtss.put(boardOwner.getPk(), maxWts);
		}
	}

	@Override
	public void readGeneralWb(PublicKey pk, Map<Integer, ArrayList<Announcement>> announcements) throws UserNotFoundException {
		if (!personalBoards.containsKey(pk)) { // check if user exists
			throw new UserNotFoundException();
		}

		int maxWts = 0;
		synchronized (generalBoardLock) {
			for (int wts : announcements.keySet()) {
				for (Announcement ann : announcements.get(wts)) {
					if (MessageSigner.verify(ann, null) && ann.getWts() == wts) {
						if (!generalBoard.get(wts).contains(ann)) {
							generalBoard.get(wts).add(ann);
							if (ann.getWts() > maxWts) {
								maxWts = ann.getWts();
							}
						}
					}
				}
			}
			if (generalWts < maxWts)
				generalWts = maxWts;
		}
	}

	@Override
	public Announcement getAnnouncementById(char[] id, Packet packet) throws AnnouncementNotFoundException {
		Announcement ann = getAnnouncementWithId(id);
		if (ann.getBoard() == 1) { // if board general
			packet.setWts(generalWts);
		} else {
			packet.setWts(personalWtss.get(ann.getCreator().getPk()));
		}
		return getAnnouncementWithId(id);
	}

	@Override
	public User getUserById(int id, Packet packet) throws UserNotFoundException {
		User user;
		synchronized (usersListLock) {
			user = getUserWithId(id);
			packet.setWts(registerWts);
		}
		return user;
	}

	@Override
	public void announcementByIdWb(PublicKey pk, Announcement ann) throws UserNotFoundException {
		if (!personalBoards.containsKey(pk) || !personalBoards.containsKey(ann.getCreator().getPk())) { // check if user exists
			return;
		}
		if (MessageSigner.verify(ann)) {
			int board = ann.getBoard();
			if (board == 1) { //General board
				int wts = ann.getWts();
				synchronized (generalBoardLock) {
					if (!generalBoard.get(wts).contains(ann)) {
						generalBoard.get(wts).add(ann);
					}
					if (generalWts < wts) {
						generalWts = wts;
					}
				}
			}
			else if (board == 0) { //personal board
				User owner = getUserWithPk(ann.getCreator().getPk());
				synchronized (personalBoardLocks.get(owner.getPk())) {
					if (!personalBoards.get(owner.getPk()).contains(ann)) {
						personalBoards.get(owner.getPk()).add(ann);
						if (personalWtss.get(owner.getPk()) < ann.getWts()) {
							personalWtss.put(owner.getPk(), ann.getWts());
						}
					}
				}

			}
		}
	}

	@Override
	public void userByIdWb(PublicKey pk, User user) {
		// Don't WB users (may allow attacks and duplication of user ids
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

	private Announcement getAnnouncementWithId(char[] id) throws AnnouncementNotFoundException {

		String annId = new String(id);
		String board = annId.substring(0,1); // get first letter

		String[] user_ann_ids = annId.substring(1).split("_");
		if (board.equals("G")) {
			// since id = GXX_YY where YY is the wts, we can search directly in the correct wts
			int annWts = Integer.parseInt(user_ann_ids[1]);
			if (!generalBoard.containsKey(annWts)) {
				throw new AnnouncementNotFoundException();
			}
			for (Announcement ann : generalBoard.get(annWts)) {
				if (Arrays.equals(ann.getId(), id)) {
					return ann;
				}
			}
		} else if (board.equals("P")) {
			int userId = Integer.parseInt(user_ann_ids[0]);
			User user;
			try {
				user = getUserWithId(userId);
			} catch (UserNotFoundException e) {
				throw new AnnouncementNotFoundException();
			}
			for (Announcement ann : personalBoards.get(user.getPk())) {
				if (Arrays.equals(ann.getId(), id)) {
					return ann;
				}
			}
		}
		throw new AnnouncementNotFoundException();
	}
}
