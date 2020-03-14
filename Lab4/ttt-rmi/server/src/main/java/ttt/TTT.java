package ttt;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
/**
 * TTT - Tic Tac Toe.
 */
public class TTT extends UnicastRemoteObject implements TTTService {

	/** The Game Board */
	private char board[][] = {
			{ '1', '2', '3' }, /* Initial values are reference numbers */
			{ '4', '5', '6' }, /* used to select a vacant square for */
			{ '7', '8', '9' } /* a turn. */
			};
	/** Next player */
	private int nextPlayer;
	/** Number of plays */
	private int numPlays;

	public TTT() throws RemoteException {
		nextPlayer = 0;
		numPlays = 0;
	}

	/** Return a textual representation of the current game board. */
	public String currentBoard() throws RemoteException {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n ");

		// acquire lock for current object
		synchronized (this) {
			sb.append(board[0][0]).append(" | ");
			sb.append(board[0][1]).append(" | ");
			sb.append(board[0][2]).append(" ");
			sb.append("\n---+---+---\n ");
			sb.append(board[1][0]).append(" | ");
			sb.append(board[1][1]).append(" | ");
			sb.append(board[1][2]).append(" ");
			sb.append("\n---+---+---\n ");
			sb.append(board[2][0]).append(" | ");
			sb.append(board[2][1]).append(" | ");
			sb.append(board[2][2]).append(" \n");
		}
		// release lock

		return sb.toString();
	}

	public boolean jogaCantoAleatorio() throws RemoteException {
		boolean res = false;
		ArrayList<Integer> posicoes = new ArrayList<Integer>(Arrays.asList(0,2,6,8));
		res = aleatorio(posicoes);
		if (res != true) {
			posicoes = new ArrayList<Integer>(Arrays.asList(1, 3, 4, 5, 7));
			res = aleatorio(posicoes);
		}
		return res;
	}

	private boolean aleatorio(ArrayList<Integer> posicoes) {
		Random random = new Random();
		Integer pos = 0;
		ArrayList<Integer> livres = new ArrayList<Integer>();
		for (Integer i : posicoes) {
			if (board[i / 3][i % 3]  != 'X' && board[i / 3][i % 3]  != 'O') {
				livres.add(i);
			}
		}
		if (livres.size() > 0) {
			if (livres.size() != 1)
				pos = livres.get(random.nextInt(livres.size()-1));
			else
				pos = livres.get(0);
			board[pos / 3][pos % 3] = (nextPlayer == 1) ? 'X' : 'O';
			nextPlayer = (nextPlayer + 1) % 2;
			numPlays++;
			return true;
		}
		return false;
	}


	/** Make a game play on behalf of provided player. */
	public boolean play(int row, int column, int player) throws RemoteException {
		// outside board ?
		if (!(row >= 0 && row < 3 && column >= 0 && column < 3))
			return false;

		// lock
		synchronized (this) {
			// invalid square ?
			if (board[row][column] > '9')
				return false;
			// not player's turn ?
			if (player != nextPlayer)
				return false;
			// no more plays left ?
			if (numPlays == 9)
				return false;

			/* insert player symbol */
			board[row][column] = (player == 1) ? 'X' : 'O';
			nextPlayer = (nextPlayer + 1) % 2;
			numPlays++;
			return true;
		}
		// unlock on return

	}

	/**
	 * Check if there is a game winner. Synchronized keyword means that the lock
	 * of the object is acquired when the method is called and released on
	 * return.
	 */
	public synchronized int checkWinner() throws RemoteException {
		int i;

		/* Check for a winning line - diagonals first */
		if ((board[0][0] == board[1][1] && board[0][0] == board[2][2])
				|| (board[0][2] == board[1][1] && board[0][2] == board[2][0])) {
			if (board[1][1] == 'X')
				return 1;
			else
				return 0;
		} else {
			/* Check rows and columns for a winning line */
			for (i = 0; i <= 2; i++) {
				if ((board[i][0] == board[i][1] && board[i][0] == board[i][2])) {
					if (board[i][0] == 'X')
						return 1;
					else
						return 0;
				}

				if ((board[0][i] == board[1][i] && board[0][i] == board[2][i])) {
					if (board[0][i] == 'X')
						return 1;
					else
						return 0;
				}
			}
		}

		if (numPlays == 9)
			/* A draw! */
			return 2;
		else
			/* Game is not over yet */
			return -1;
	}

}
