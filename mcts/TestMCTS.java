package mcts;

import static mcts.State.*;
import minimax.*;
import static mcts.MCTSCheckers.*;

import java.util.*;

/**
 * Class for test MCTS player
 * @author Jung Won Lee
 */
public class TestMCTS {
	public static <K, V> void printMap(HashMap<K, V> map) {
		for (K key : map.keySet()) {
			System.out.println(key + " : " + map.get(key));
		}
	}
	
	public static Move rando(State state) {
		ArrayList<Move> moves = state.getLegalMoves();
		return moves.get( (int) (Math.random() * moves.size()));
	}
	
	public static void playAgainstRandomPlayer(int numOfGames) {
		double numOfWins = 0.0;
		MCTSCheckers search = new MCTSCheckers();
		
		for (int i = 0; i < numOfGames; i++) {
			int[][] board = new int[8][8];
			initialize(board);
			State state = new State(board, false, 0, 0);
			System.out.println("-------------------------- Game " + (i + 1) + " start ------------------------------------");
			
			while (!state.isTerminalState()) {
				System.out.println(state);

				Move yourMove = rando(state);

				state = state.result(yourMove);

				System.out.println("Move selected by the Doofus: " + yourMove);
				System.out.println();
				System.out.println(state);

				search.setRootState(state);

				Move hisMove = search.getBestMove();


				state = state.result(hisMove);

				System.out.println("Move selected by MCTS: " + hisMove);
				System.out.println();

			}
			if (state.playerWins()) {
				System.out.println("MCTS player wins.");
				numOfWins++;
			}
			else if(state.opponentWins()) {
				System.out.println("Random player wins.");
			}
			else {
				System.out.println("Draw.");
			}
			System.out.println("-------------------------- Game " + (i + 1) + " ends ------------------------------------");
			System.out.println();
			try {
				Thread.sleep(2000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Percentage wins against random player: " + (numOfWins / numOfGames));
	}
	
	public static void playAgainstAlphaBeta(int numOfGames) {
		double numOfWins = 0.0;
		MCTSCheckers search = new MCTSCheckers();
		
		MinimaxCheckers alphabeta = new MinimaxCheckers();
		
		for (int i = 0; i < numOfGames; i++) {
			int[][] board = new int[8][8];
			initialize(board);
			State state = new State(board, false, 0, 0);
			System.out.println("-------------------------- Game " + (i + 1) + " start ------------------------------------");
			
			while (!state.isTerminalState()) {
				
				System.out.println(state);
				alphabeta.setStartingState(state);
				Move yourMove = alphabeta.alphaBetaMove();
				System.out.println("Move selected by alpha-beta: " + yourMove);
				System.out.println();
				state = state.result(yourMove);
				
				if (state.isTerminalState()) {
					break;
				}
				
				System.out.println(state);
				search.setRootState(state);
				Move hisMove = search.getBestMove();
				System.out.println("Move selected by MCTS: " + hisMove);
				System.out.println();
				state = state.result(hisMove);
			}
			if (state.playerWins()) {
				System.out.println("MCTS player wins.");
				numOfWins++;
			}
			else if(state.opponentWins()) {
				System.out.println("Alpha-beta player wins.");
			}
			else {
				System.out.println("Draw.");
			}
			System.out.println("-------------------------- Game " + (i + 1) + " ends ------------------------------------");
			System.out.println();
			try {
				Thread.sleep(2000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Percentage wins against alpha-beta player: " + (numOfWins / numOfGames));
	}
	
	public static void playAgainstHuman() {
		Scanner input = new Scanner(System.in);
		int[][] board = new int[8][8];
		initialize(board);
		State state = new State(board, false, 0, 0);
		
		MCTSCheckers search = new MCTSCheckers();
		
		while (!state.isTerminalState()) {
			try {
				System.out.println(state);
				
				System.out.println("Select move: ");
				int selection = input.nextInt();
				System.out.println();
				
				Move yourMove = state.getLegalMoves().get(selection - 1);
				
				state = state.result(yourMove);
				
				if (state.isTerminalState()) {
					break;
				}
				
				System.out.println("Move selected: " + yourMove);
				System.out.println();
				System.out.println(state);
				
				search.setRootState(state);
				
				Move hisMove = search.getBestMove();
				
				System.out.println("Q function at this state: ");
				printMap(search.getRoot().getQ());
				System.out.println();
				
				
				state = state.result(hisMove);
				
				System.out.println("Move selected by MCTS: " + hisMove);
				System.out.println();
				
				
			} catch (InputMismatchException e) {
				input.close();
				break;
			}
		}
		
		if (state.playerWins()) {
			System.out.println("The end of the human race is nigh.");
		}
		else if (state.opponentWins()) {
			System.out.println("Eh. Could have been better.");
		}
		else {
			System.out.println("Seriously? A draw?");
		}
		
		input.close();
	}
	
	public static void initialize(int[][] board) {
		for (int i = 1; i < 13; i++) {
			board[posToRow(i)][posToCol(i)] = 1;
		}
		for (int i = 21; i < 33; i++) {
			board[posToRow(i)][posToCol(i)] = 2;
		}
	}
	
	
	
	public static void main(String[] args) {
		playAgainstAlphaBeta(1);

		/*
		Move m1 = new Move(21, 17);
		Move m2 = new Move(12, 16);
		Move m3 = new Move(17, 14);
		
		State s1 = state.result(m1);
		State s2 = state.result(m1).result(m2).result(m3);

		
		
		int[][] b2 = new int[8][8];
		b2[0][5] = 3;
		b2[1][4] = 2;
		b2[1][2] = 2;
		b2[3][2] = 2;
		b2[1][6] = 2;
		b2[3][4] = 2;
		
		State endGame = new State(b2, false, 0, 0);
		
		MCTSCheckers search = new MCTSCheckers(s2, 0, 0);
		
		System.out.println(s2);
		MCTSNode root = search.getRoot();
		
		long startTime = System.nanoTime();
		System.out.println(search.getBestMove());
		System.out.println();
		long endTime = System.nanoTime();
		System.out.println(((endTime-startTime) / 1000000) + " milliseconds");
		printMap(search.getRoot().getQ());
		*/
	}
}
