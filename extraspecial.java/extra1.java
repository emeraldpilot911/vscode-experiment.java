import java.util.Random;
import java.util.Scanner;

public class extra1 {
	private static final Scanner input = new Scanner(System.in);
	private static final Random random = new Random();

	public static void main(String[] args) {
		char[] board = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
		System.out.println("=== TIC-TAC-TOE ===");
		System.out.println("You are X. Enter a number from 1 to 9.");

		char winner = ' ';
		for (int turn = 0; turn < 9 && winner == ' '; turn++) {
			printBoard(board);
			if (turn % 2 == 0) {
				int move = playerMove(board);
				board[move] = 'X';
			} else {
				int move = computerMove(board);
				board[move] = 'O';
				System.out.println("Computer chose " + (move + 1) + ".");
			}
			winner = checkWinner(board);
		}

		printBoard(board);
		switch (winner) {
			case 'X' -> System.out.println("You win!");
			case 'O' -> System.out.println("Computer wins!");
			default -> System.out.println("It's a draw!");
		}
	}

	private static void printBoard(char[] board) {
		System.out.println();
		System.out.println(" " + board[0] + " | " + board[1] + " | " + board[2]);
		System.out.println("---+---+---");
		System.out.println(" " + board[3] + " | " + board[4] + " | " + board[5]);
		System.out.println("---+---+---");
		System.out.println(" " + board[6] + " | " + board[7] + " | " + board[8]);
		System.out.println();
	}

	private static int playerMove(char[] board) {
		while (true) {
			System.out.print("Your move: ");
			if (input.hasNextInt()) {
				int move = input.nextInt() - 1;
				if (move >= 0 && move < 9 && board[move] != 'X' && board[move] != 'O') {
					return move;
				}
			} else {
				input.next();
			}
			System.out.println("Invalid move. Choose an empty square from 1 to 9.");
		}
	}

	private static int computerMove(char[] board) {
		int move;
		do {
			move = random.nextInt(9);
		} while (board[move] == 'X' || board[move] == 'O');
		return move;
	}

	private static char checkWinner(char[] board) {
		int[][] lines = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8},
				{0, 3, 6}, {1, 4, 7}, {2, 5, 8},
				{0, 4, 8}, {2, 4, 6}};
		for (int[] line : lines) {
			if (board[line[0]] == board[line[1]] && board[line[1]] == board[line[2]]) {
				return board[line[0]];
			}
		}
		return ' ';
	}
}
