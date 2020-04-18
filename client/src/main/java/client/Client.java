package client;

import commonClasses.Announcement;
import commonClasses.KeyLoader;
import commonClasses.MessageSigner;
import commonClasses.User;
import commonClasses.exceptions.AnnouncementNotFoundException;
import library.Exceptions.CommunicationErrorException;
import commonClasses.exceptions.InvalidAnnouncementException;
import commonClasses.exceptions.UserNotFoundException;
import library.ClientEndpoint;

import java.lang.reflect.Array;
import java.security.KeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Client {
	private PublicKey clientPublicKey;
	private Scanner keyboardSc;
	private ClientEndpoint clientEndpoint;
	private PrivateKey clientPrivateKey;

	Client(int id) {
		int[] serverPorts = new int[]{10251,10252,10253};
		List<PublicKey> serverPublicKey = new ArrayList<>();
	    String keyStorePass = "DPASsecClient"+id;
	    String resourcesPath = "src\\main\\resources\\";
		keyboardSc = new Scanner(System.in);


		try {
		    serverPublicKey.add(KeyLoader.getServerPublicKey(resourcesPath+"KeysUser" + id, id, keyStorePass));
            clientPrivateKey = KeyLoader.getPrivateKey(resourcesPath+"KeysUser" + id, keyStorePass);
            clientPublicKey = KeyLoader.getPublicKey(resourcesPath+"KeysUser" + id, keyStorePass);
        } catch (KeyException e) {
		    e.printStackTrace();
        }

		clientEndpoint = new ClientEndpoint("localhost", serverPorts, clientPrivateKey, serverPublicKey);
		System.out.println("Found server");
	}


	void login() throws CommunicationErrorException {
		int numTries = 0;
		while (++numTries<=3) {
			try {
				clientEndpoint.register(clientPublicKey, "test");
				return;
			} catch (CommunicationErrorException e) {
				System.out.println("Communication with server failed, trying again.");
			}
		}
		throw new CommunicationErrorException();
	}


	void work() {
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

	private byte[] getMsgSign(char[] msg, int b, Announcement[] anns) {
		return MessageSigner.sign(msg, clientPublicKey, b, anns, clientPrivateKey);
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

	private void post(int board) {
		boolean accept = false;
		String message = "";
		message = keyboardSc.nextLine();
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
				finish = true;
			} else {
				try {
					announcements.add(clientEndpoint.getAnnouncementById(clientPublicKey, ann));
					System.out.println("Announcement added to the list of referrals");
				} catch (AnnouncementNotFoundException e) {
					System.out.println("Invalid announcement id, please try again.");
				} catch (CommunicationErrorException e) {
					System.out.println("Communication error, please try again.");
				}
			}
		}

		byte[] sign = getMsgSign(message.toCharArray(), board, announcements.toArray(new Announcement[0]));
		char[] response;
		try {
			if (board == 1) {
				response = clientEndpoint.postGeneral(clientPublicKey, message.toCharArray(), announcements.toArray(new Announcement[0]), sign);
				System.out.println(String.valueOf(response));
			} else {

				response = clientEndpoint.post(clientPublicKey, message.toCharArray(), announcements.toArray(new Announcement[0]), sign);
				System.out.println(String.valueOf(response));
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			//Unpossible
		} catch (CommunicationErrorException | InvalidAnnouncementException e) {
			System.out.println("Communication error, please try posting again.");
			e.printStackTrace();
		}
	}

	private void printPrivateBoard() {
		boolean finish = false;
		String line;
		User user = null;

		System.out.println("------------------- Read Personal Board --------------------");
		while (!finish) {
			System.out.print("Enter User Id: ");
			keyboardSc.nextLine();
			line = keyboardSc.nextLine();
			int uid = Integer.parseInt(line);
			try {
				user = clientEndpoint.getUserById(clientPublicKey, uid);
				finish = true;
			} catch (UserNotFoundException e) {
				System.out.println("Invalid user id, please try again.");
				finish = false;
			} catch (CommunicationErrorException e) {
				e.printStackTrace();
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
			printAnnouncements(clientEndpoint.read(user.getPk(), numAnn));
		} catch (UserNotFoundException e) {
			// Impossible
			System.out.println(Arrays.toString(e.getStackTrace()));
		} catch (CommunicationErrorException e) {
			System.out.println("Communication error, please try again.");
		}
	}

	private void printPublicBoard() {
		System.out.println("-------------------- Read General Board --------------------");
		System.out.print("Enter number of announcements to print: [0 to print all]");
		int numAnn = keyboardSc.nextInt();
		while (numAnn < 0) {
			System.out.println("Invalid choice, please enter a number greater or equal to 0");
			numAnn = keyboardSc.nextInt();
		}


		System.out.println("------------------ Printing General Board ------------------");
		try {
			printAnnouncements(clientEndpoint.readGeneral(clientPublicKey, numAnn));
		} catch (CommunicationErrorException e) {
			System.out.println("Communication error, please try again.");
		}

	}



	private void printAnnouncements(Announcement[] announcements) {
		for (Announcement a : announcements) {
			if (!MessageSigner.verify(a)) {
				System.out.println("Invalid Announcement");
			} else {
				System.out.println(a);
			}
		}
	}





}
