package server;

import commonClasses.Announcement;
import commonClasses.User;
import library.ICommLib;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
/**
 * TTT - Tic Tac Toe.
 */
public class DPASService extends UnicastRemoteObject implements ICommLib {

		private List<Announcement> announcements = null;
		private List<User> users = null;




	public DPASService() throws RemoteException {
		announcements = new ArrayList<Announcement>();
		users = new ArrayList<User>();
	}


	private int writeAnnouncements() {
		// Serialization

		synchronized (this) {
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
		return 0;
	}

	private int writeUsers() {
		// Serialization

		synchronized (this) {
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
		return 0;
	}


	@Override
	public String register(User u) throws RemoteException {
		users.add(u);
		return "Successful";
	}

	@Override
	public String post(PublicKey key, char[] message, Announcement[] a) throws RemoteException {
		return null;
	}

	@Override
	public String postGeneral(PublicKey key, char[] message, Announcement[] a) throws RemoteException {
		return null;
	}

	@Override
	public Announcement[] read(PublicKey key, int number) {
		return new Announcement[0];
	}

	@Override
	public Announcement[] readGeneral(int number) {
		return new Announcement[0];
	}
}
