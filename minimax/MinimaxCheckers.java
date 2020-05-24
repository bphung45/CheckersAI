package minimax;
import mcts.*;
import static mcts.TestMCTS.*;
import static mcts.State.*;

import java.util.*;

/**
 * Class that implements a minimax checkers player with alpha-beta pruning
 * and cut-off evaluation. Code adapted from
 * Stuart Russell and Peter Norvig. Artificial Intelligence:
 * A Modern Approach, Third Edition. Prentice Hall, 2010. ISBN: 978-0-13-604259-4.
 */
public class MinimaxCheckers {
	
	// The depth limit of the search
	private int limit = 10;
	
	private State startingState;
	
	public MinimaxCheckers() {
		startingState = null;
	}
	
	public MinimaxCheckers(State state) {
		startingState = state;
	}
	
	public Move alphaBetaMove() {
		if (startingState.isPlayersTurn()) {
			return maxValue(startingState, Integer.MIN_VALUE, Integer.MAX_VALUE, 0).move;			
		}
		return minValue(startingState, Integer.MIN_VALUE, Integer.MAX_VALUE, 0).move;
	}
	
	public MoveValue maxValue(State state, int alpha, int beta, int depth) {
		if (depth == limit || state.isTerminalState()) {
			return new MoveValue(new Move(), evaluation(state));
		}
		depth++;
		MoveValue moveValue = new MoveValue(new Move(), Integer.MIN_VALUE);
		for (Move move : state.getLegalMoves()) {
			moveValue.move = move;
			moveValue.value = Math.max(moveValue.value, minValue(state.result(move), alpha, beta, depth).value);
			if (moveValue.value >= beta) {
				return moveValue;
			}
			alpha = Math.max(alpha, moveValue.value);
		}
		return moveValue;
	}
	
	public MoveValue minValue(State state, int alpha, int beta, int depth) {
		if (depth == limit || state.isTerminalState()) {
			return new MoveValue(new Move(), evaluation(state));
		}
		depth++;
		MoveValue moveValue = new MoveValue(new Move(), Integer.MAX_VALUE);
		for (Move move : state.getLegalMoves()) {
			moveValue.move = move;
			moveValue.value = Math.min(moveValue.value, maxValue(state.result(move), alpha, beta, depth).value);
			if (moveValue.value <= alpha) {
				return moveValue;
			}
			beta = Math.min(beta, moveValue.value);
		}
		return moveValue;
	}
	
	public int diffEvaluation(State state) {
		HashMap<String, Integer> differences = state.getDifferences();
		return differences.get("opponent") - differences.get("player") + 4 * (differences.get("playerKing") - differences.get("opponentKing"));
	}
	
	public int numEvaluation(State state) {
		HashMap<String, Integer> numPieces = state.getNumPieces();
		return 2 * (numPieces.get("player") - numPieces.get("opponent")) + 4 * (numPieces.get("playerKing") - numPieces.get("opponentKing"));
	}
	
	public int evaluation(State state) {
		if (state.isTerminalState()) {
			if (state.playerWins()) {
				return 1000;
			}
			else if (state.opponentWins()) {
				return -1000;
			} else {
				return 0;
			}
		}
		
		return diffEvaluation(state) + 3 * numEvaluation(state); 
	}
	
	public void setDepthLimit(int limit) {
		this.limit = limit;
	}
	
	public void setStartingState(State state) {
		startingState = state;
	}
	
	// A class that stores a Move and its corresponding value
	private class MoveValue {
		public Move move;
		public int value;
		
		public MoveValue(Move move, int value) {
			this.move = move;
			this.value = value;
		}
	}
	/*
	public static void main(String[] args) {
		int[][] board = new int[8][8];
		initialize(board);
		State startState = new State(board, true);
		long startTime = System.currentTimeMillis();
		System.out.println((new MinimaxCheckers(startState)).alphaBetaMove());
		long endTime = System.currentTimeMillis();
		System.out.println((endTime - startTime) + " milliseconds");
	}
	*/
	
}
