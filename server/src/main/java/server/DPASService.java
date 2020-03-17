package server;

import commonClasses.Announcement;
import commonClasses.User;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.UserNotFoundException;
import library.ICommLib;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;


public class DPASService extends UnicastRemoteObject implements ICommLib {

	private List<Announcement> announcements;
	private List<User> users;

	private final Object usersFileLock;
	private final Object announcementsFileLock;
	private final Object usersListLock;
	private final Object announcementsListLock;



	DPASService() throws RemoteException {
		announcements = new ArrayList<>();
		users = new ArrayList<>();
		usersFileLock = new Object();
		announcementsFileLock = new Object();
		usersListLock = new Object();
		announcementsListLock = new Object();

	}


	private void writeAnnouncements() {
		// Serialization

		synchronized (announcementsFileLock) {
			boolean tryAgain = true;

			while (tryAgain) {
				try {
					//Saving of object in a file
					FileOutputStream file = new FileOutputStream("AnnouncementList1");
					ObjectOutputStream out = new ObjectOutputStream(file);

					// Method for serialization of object
					out.writeObject(announcements);
					out.close();
					file.close();

					tryAgain = false;
				} catch (IOException ex) {
					System.out.println("IOException is caught");
					tryAgain = true;
				}
			}

			tryAgain = true;
			while (tryAgain) {
				try {
					//Saving of object in a file
					FileOutputStream file = new FileOutputStream("AnnouncementList2");
					ObjectOutputStream out = new ObjectOutputStream(file);

					// Method for serialization of object
					out.writeObject(announcements);
					out.close();
					file.close();

					System.out.println("Object has been serialized");

					tryAgain = false;
				} catch (IOException ex) {
					System.out.println("IOException is caught");
					tryAgain = true; //when you try your best but you don't succeed...
				}
			}
		}
	}

	private void writeUsers() {
		// Serialization

		synchronized (usersFileLock) {
			boolean tryAgain = true;

			while (tryAgain) {
				try {
					//Saving of object in a file
					FileOutputStream file = new FileOutputStream("UsersList1");
					ObjectOutputStream out = new ObjectOutputStream(file);

					// Method for serialization of object
					out.writeObject(users);
					out.close();
					file.close();

					tryAgain = false;
				} catch (IOException ex) {
					System.out.println("IOException is caught");
					tryAgain = true;
				}
			}

			tryAgain = true;
			while (tryAgain) {
				try {
					//Saving of object in a file
					FileOutputStream file = new FileOutputStream("UsersList2");
					ObjectOutputStream out = new ObjectOutputStream(file);

					// Method for serialization of object
					out.writeObject(users);
					out.close();
					file.close();

					System.out.println("Object has been serialized");

					tryAgain = false;
				} catch (IOException ex) {
					System.out.println("IOException is caught");
					tryAgain = true; //when you try your best but you don't succeed...
				}
			}
		}
	}


	@Override
	public String register(PublicKey pk, String username) {
		synchronized (usersListLock) {
			try {
				getUserWithPk(pk);
			} catch (UserNotFoundException e) {
				users.add(new User(users.size()+1, pk, username));
				writeUsers();
			}
		}
		return "Successful";
	}

	@Override
	public String post(PublicKey key, char[] message, Announcement[] a) throws UserNotFoundException {
		synchronized (announcementsListLock) {
			announcements.add(new Announcement(announcements.size() + 1, message, getUserWithPk(key), 0));
		}
		writeAnnouncements();
		return "Announcement successfully posted";
	}

	@Override
	public String postGeneral(PublicKey key, char[] message, Announcement[] a) throws UserNotFoundException {
		synchronized (announcementsListLock) {
			announcements.add(new Announcement(announcements.size() + 1, message, getUserWithPk(key), 1));
			System.out.println(announcements);
			System.out.println(announcements.get(0).toString());
		}
		writeAnnouncements();
		return "General announcement successfully posted";
	}

	@Override
	public Announcement[] read(PublicKey key, int number) throws UserNotFoundException {
		List<Announcement> tempAnnouncements = new ArrayList<>();
		boolean sendAll = number==0;
		for (Announcement a : announcements) {
			if (a.getBoard()==0 && a.getCreator() == getUserWithPk(key)) {
				tempAnnouncements.add(a);
			}
			if (number-- == 0 && !sendAll) {
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
			if ((number-- == 0) && !sendAll) {
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
			if (u.getPk() == pk) {
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
