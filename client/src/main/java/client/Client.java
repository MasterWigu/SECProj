package client;

import commonClasses.Announcement;
import commonClasses.User;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.UserNotFoundException;
import library.ICommLib;

import java.rmi.*;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/** This is the client of the Tic Tac Toe game. */
public class Client {
	private ICommLib DPASService;
	private PublicKey publicKey;
	private Scanner keyboardSc;

	private Client() throws Exception {
		keyboardSc = new Scanner(System.in);

		//publicKey = PublicKeyReader.get("pk.pem");
		publicKey = null;
		DPASService = (ICommLib) Naming.lookup("//localhost:8000/DPASService");
		System.out.println("Found server");
	}


	private void login() throws RemoteException {
		DPASService.register(publicKey, "test");
	}


	private void work() throws RemoteException {
		int choice = 0;
		while (choice != 9) {
			printMenu();
			choice = readChoice();
			switch (choice) {
				case 1:
					post(0);
					break;
				case 2:
					post(1);
					break;
				case 3:
					printPrivateBoard();
					break;
				case 4:
					printPublicBoard();
					break;
				default:
					return;
			}
		}
	}


	private void printMenu() {
		System.out.println("--------------------------- MENU ---------------------------");
		System.out.println("1 - Post to personal board");
		System.out.println("2 - Post to general board");
		System.out.println("3 - Read from personal board of some user");
		System.out.println("4 - Read from general board");
		System.out.println("9 - Exit");
		System.out.println("------------------------------------------------------------");
	}

	private int readChoice() {
		int choice = keyboardSc.nextInt();
		while (!(choice == 1 || choice == 2 || choice == 3  || choice == 4 || choice == 9)) {
			System.out.println("Invalid choice, please enter a number between 1 and 4, or 9 to exit.");
			choice = keyboardSc.nextInt();
		}
		return choice;
	}

	private void post(int board) throws RemoteException {
		boolean accept = false;
		String message = "";
		String line = "";
		if (board == 1) {
			System.out.println("----------------- Posting to general board -----------------");
		} else {
			System.out.println("---------------- Posting to personal board -----------------");
		}
		while (!accept) {
			System.out.println("Write message (max 255 characters): [Enter to submit]");
			message = keyboardSc.nextLine();
			if (message.length() < 255 && message.length()>0)
				accept = true;
			else {
				System.out.println("Message is too long! Please try again.");
			}
		}

		System.out.println("Refer to any previous announcements? [One announcement id per line, 0 to finish]");
		boolean finish = false;
		List<Announcement> announcements = new ArrayList<>();
		while (!finish) {
			System.out.print("Announcement id: ");
			line = keyboardSc.nextLine();
			int ann = Integer.valueOf(line);

			if (ann == 0) {
				System.out.println("ASDASD");
				finish = true;
			} else {
				try {
					System.out.println("ASdasllala");
					announcements.add(DPASService.getAnnouncementById(ann));
				} catch (AnnouncementNotFoundException e) {
					System.out.println("Invalid announcement id, please try again.");
				}
				System.out.println("Announcement added to the list of referrals");
			}
		}

		try {
			if (board == 1) {
				DPASService.postGeneral(publicKey, message.toCharArray(), announcements.toArray(new Announcement[0]));
				System.out.println("Announcement successfully posted to general board.");
			} else {
				DPASService.post(publicKey, line.toCharArray(), announcements.toArray(new Announcement[0]));
				System.out.println("---------------- Posting to personal board -----------------");
			}
		} catch (UserNotFoundException e) {
			//Unpossible
		}
	}

	private void printPrivateBoard() throws RemoteException{
		boolean finish = false;
		String line;
		User user = null;

		System.out.println("------------------- Read Personal Board --------------------");
		while (!finish) {
			System.out.print("Enter User Id: ");
			line = keyboardSc.nextLine();
			int uid = Integer.getInteger(line);
			try {
				user = DPASService.getUserById(uid);
				finish = true;
			} catch (UserNotFoundException e) {
				System.out.println("Invalid user id, please try again.");
				finish = false;
			}
		}

		System.out.print("Enter number of announcements to print: [0 to print all]");
		int numAnn = keyboardSc.nextInt();
		while (numAnn < 0) {
			System.out.println("Invalid choice, please enter a number greater or equal to 0");
			numAnn = keyboardSc.nextInt();
		}


		System.out.println("------------ Printing Personal Board of user "+ user.getId() + " -------------");
		try {
			printAnnouncements(DPASService.read(user.getPk(), numAnn));
		} catch (UserNotFoundException e) {
			// Impossible
			System.out.println(Arrays.toString(e.getStackTrace()));
		}
	}

	private void printPublicBoard() throws RemoteException{
		System.out.println("-------------------- Read General Board --------------------");
		System.out.print("Enter number of announcements to print: [0 to print all]");
		int numAnn = keyboardSc.nextInt();
		while (numAnn < 0) {
			System.out.println("Invalid choice, please enter a number greater or equal to 0");
			numAnn = keyboardSc.nextInt();
		}


		System.out.println("------------------ Printing General Board ------------------");

		printAnnouncements(DPASService.readGeneral(numAnn));

	}



	private void printAnnouncements(Announcement[] announcements) {
		for (Announcement a : announcements) {
			System.out.println("----------- Announcement -----------");
			System.out.println("From: " + a.getCreator().getId());
			System.out.println("Message:");
			System.out.println(a.getMessage());
			System.out.println("--------- End Announcement ---------");
		}
	}

	public static void main(String[] args) {
		try {
			Client c = new Client();
			c.login();
			c.work();
		} catch (RemoteException e) {
            System.out.println("DPASService: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Lookup: " + e.getMessage());
        }
	}



}
