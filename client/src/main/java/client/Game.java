package client;

import library.ICommLib;

import java.rmi.*;
import java.rmi.server.*;

import java.util.Scanner;

/** This is the client of the Tic Tac Toe game. */
public class Game {
	ICommLib ttt = null;
	Scanner keyboardSc;
	int winner = 0;
	int player = 1;

	public Game() throws RemoteException, Exception {
		keyboardSc = new Scanner(System.in);
		ttt = (ICommLib) Naming.lookup("//localhost:8000/ICommlib");
		System.out.println("Found server");
	}

	public int readPlay() {
		int play;
		do {
			System.out.printf(
					"\nPlayer %d, please enter the number of the square "
							+ "where you want to place your %c (or 0 to refresh the board): \n",
					player, (player == 1) ? 'X' : 'O');
			play = keyboardSc.nextInt();
		} while ((play > 9 || play < 0) && play != 201);
		return play;
	}
/*
	public void playGame() throws RemoteException {
		int play;
		boolean playAccepted;

		do {
			player = ++player % 2;
			do {
				System.out.println(ttt.currentBoard());
				play = readPlay();
				if (play == 201) {
					playAccepted = ttt.jogaCantoAleatorio();
					if (!playAccepted)
						System.out.println("Invalid play! Try again.");
				}
				else if (play != 0) {
					playAccepted = ttt.play(--play / 3, play % 3, player);
					if (!playAccepted)
						System.out.println("Invalid play! Try again.");
				} else
					playAccepted = false;
			} while (!playAccepted);
			winner = ttt.checkWinner();
		} while (winner == -1);
		System.out.println(ttt.currentBoard());
	}
*/
	public void congratulate() {
		if (winner == 2)
			System.out.printf("\nHow boring, it is a draw\n");
		else
			System.out.printf("\nCongratulations, player %d, YOU ARE THE WINNER!\n", winner);
	}

	public static void main(String[] args) {
		try {
			Game g = new Game();
			//g.playGame();
			//g.congratulate();
		} catch (RemoteException e) {
            System.out.println("TTTService: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Lookup: " + e.getMessage());
        }
	}



}
