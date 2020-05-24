package mcts;

import java.io.Serializable;
import java.util.*;

/**
 * Class that represents a state in checkers
 * @author Jung Won Lee 
 */
public class State implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String[] KEYS = {"player", "opponent", "playerKing", "opponentKing"};
	
	/* State in a checker board is represented by
	 * a 2D-array of integers where
	 * 0 = vacant
	 * 1 = player piece
	 * 2 = opponent piece
	 * 3 = player king piece
	 * 4 = opponent king piece
	 **/
	private int[][] board;
	
	// true if player's turn at this state, false if opponent's turn
	// at this state
	private boolean turn;
	
	private ArrayList<Move> legalMoves;
	
	// player's score at this state
	private int pScore;
	
	// opponent's score at this state
	private int oScore;
	
	// utility of the terminal state. 0 by default
	private int utility;
	
	private HashMap<String, Integer> numPieces;
	
	private HashMap<String, Integer> differences;
	
	
	/**
	 * Constructor for State
	 * @param board
	 * @param turn
	 * @param pScore
	 * @param oScore
	 */
	public State(int[][] board, boolean turn, int pScore, int oScore) {
		this.board = copyBoard(board);
		this.turn = turn;
		this.pScore = pScore;
		this.oScore = oScore;
		this.utility = 0;
		numPieces = new HashMap<String, Integer>();		
		differences = new HashMap<String, Integer>();
		for (String piece : KEYS) {
			numPieces.put(piece, 0);
		}
		legalMoves = allLegalMoves();
		for (String piece : KEYS) {
			differences.put(piece, 0);
		}
	}
	
	/**
	 * Constructor for State
	 * @param board
	 * @param turn
	 */
	public State(int[][] board, boolean turn) {
		this.board = copyBoard(board);
		this.turn = turn;
		this.pScore = 0;
		this.oScore = 0;
		this.utility = 0;
		numPieces = new HashMap<String, Integer>();
		differences = new HashMap<String, Integer>();
		for (String piece : KEYS) {
			numPieces.put(piece, 0);
		}
		legalMoves = allLegalMoves();
		for (String piece : KEYS) {
			differences.put(piece, 0);
		}
	}
	
	/***
	 * Returns an ArrayList of legal Moves in the state
	 * @return an ArrayList of Move objects that represents the legal moves
	 *         in the state
	 */
	public ArrayList<Move> allLegalMoves() {
		int relativePlayer = 0;
		int relativeOpponent = 0;
		int kingPiece = 0;
		int opponentKingPiece = 0;
		if (turn) {
			relativePlayer = 1;
			relativeOpponent = 2;
			kingPiece = 3;
			opponentKingPiece = 4;
		}
		else {
			relativePlayer = 2;
			relativeOpponent = 1;
			kingPiece = 4;
			opponentKingPiece = 3;
		}
		
		ArrayList<Move> moves = new ArrayList<Move>();
		ArrayList<Move> jumps = new ArrayList<Move>();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == relativePlayer || board[i][j] == kingPiece) {
					if (board[i][j] != 2) {
						if (i + 1 < board.length && j - 1 >= 0 && board[i + 1][j - 1] == 0) {
							moves.add(new Move(RCToPos(i, j), RCToPos(i + 1, j - 1)));
						}
						if (i + 1 < board.length && j + 1 < board[0].length && board[i + 1][j + 1] == 0) {
							moves.add(new Move(RCToPos(i, j), RCToPos(i + 1, j + 1)));
						}
					}
					if (board[i][j] != 1) {
						if (i - 1 >= 0 && j - 1 >= 0 && board[i - 1][j - 1] == 0) {
							moves.add(new Move(RCToPos(i, j), RCToPos(i - 1, j - 1)));
						}
						if (i - 1 >= 0 && j + 1 < board[0].length && board[i - 1][j + 1] == 0) {
							moves.add(new Move(RCToPos(i, j), RCToPos(i - 1, j + 1)));
						}
					}
					addJumpMoves(jumps, board[i][j], relativeOpponent, opponentKingPiece, i, j);
					String key = KEYS[board[i][j] - 1];
					numPieces.put(key, numPieces.get(key) + 1);
				}
			}
		}
		
		if (jumps.isEmpty()) {
			return moves;
		}
		else {
			return jumps;
		}
	}
	
	/**
	 * Adds jump moves that the player/opponent can make with the piece at
	 * row i and column j by breadth-first search
	 * @param moves ArrayList that stores possible moves
	 * @param piece    the piece type (either a man or a king piece)
	 * @param opponent the opponent piece
	 * @param initR    the row of the piece
	 * @param initC    the column column of the piece
	 */
	public void addJumpMoves(ArrayList<Move> moves, int piece, int opponent, int opponentKingPiece, int initR, int initC) {
		
		// Copy of the board
		int[][] copy = copyBoard(board);
		int init = RCToPos(initR, initC);
		PositionNode root = new PositionNode(init);
		
		// Queue for implementing a breadth-first search
		Queue<PositionNode> queue = new LinkedList<PositionNode>();
		
		// The leaves of the search tree
		Queue<PositionNode> leaves = new LinkedList<PositionNode>();
		queue.offer(root);
		
		while(!queue.isEmpty()) {
			PositionNode parent = queue.poll();
			int parentPos = parent.value;
			int r = posToRow(parentPos);
			int c = posToCol(parentPos);
			boolean noMoves = true;
			
			if (piece != 1 && canJumpUpLeft(r, c, opponent, opponentKingPiece, copy)) {
				copy[r-1][c-1] = 0;
				addNode(queue, parent, r - 1, c - 1, r - 2, c - 2);
				noMoves = false;
			}
			if (piece != 1 && canJumpUpRight(r, c, opponent, opponentKingPiece, copy)) {
				copy[r-1][c+1] = 0;
				addNode(queue, parent, r - 1, c + 1, r - 2, c + 2);
				noMoves = false;
			}
			if (piece != 2 && canJumpDownLeft(r, c, opponent, opponentKingPiece, copy)) {
				copy[r+1][c-1] = 0;
				addNode(queue, parent, r + 1, c - 1, r + 2, c - 2);
				noMoves = false;
			}
			if (piece != 2 && canJumpDownRight(r, c, opponent, opponentKingPiece, copy)) {
				copy[r+1][c+1] = 0;
				addNode(queue, parent, r + 1, c + 1, r + 2, c + 2);
				noMoves = false;
			}
			if (noMoves && (r != initR || c != initC)) {
				leaves.offer(parent);
			}

		}
		
		if (!leaves.isEmpty()) {
			for (PositionNode node : leaves) {
				Move move = new Move(init , node.value);
				move.addJumps(node.jumpList);
				moves.add(move);
			}			
		}
	}
	
	public void addNode(Queue<PositionNode> queue, PositionNode parent, int jumpedR, int jumpedC, int newR, int newC) {
		PositionNode newNode = new PositionNode(RCToPos(newR, newC));
		newNode.jumpList.addAll(parent.jumpList);
		newNode.jumpList.add(RCToPos(jumpedR, jumpedC));
		queue.offer(newNode);
	}
	
	public boolean canJumpDownLeft(int r, int c, int opponent, int opponentKingPiece, int[][] copy) {
		return r + 2 < copy.length && c - 2 >= 0 && copy[r + 2][c - 2] == 0 && (copy[r + 1][c - 1] == opponent || copy[r + 1][c - 1] == opponentKingPiece);
	}
	
	public boolean canJumpDownRight(int r, int c, int opponent, int opponentKingPiece, int[][] copy) {
		return r + 2 < copy.length && c + 2 < copy[0].length && copy[r + 2][c + 2] == 0 && (copy[r + 1][c + 1] == opponent || copy[r + 1][c + 1] == opponentKingPiece);
	}
	
	public boolean canJumpUpLeft(int r, int c, int opponent, int opponentKingPiece, int[][] copy) {
		return r - 2 >= 0 && c - 2 >= 0 && copy[r - 2][c - 2] == 0 && (copy[r - 1][c - 1] == opponent || copy[r - 1][c - 1] == opponentKingPiece);
	}
	
	public boolean canJumpUpRight(int r, int c, int opponent, int opponentKingPiece, int[][] copy) {
		return r - 2 >= 0 && c + 2 < copy[0].length && copy[r - 2][c + 2] == 0 && (copy[r - 1][c + 1] == opponent || copy[r - 1][c + 1] == opponentKingPiece);
	}
	
	public int[][] copyBoard(int[][] b) {
		int[][] copy = new int[8][8];
		for (int i = 0; i < copy.length; i++) {
			for (int j = 0; j < copy[0].length; j++) {
				copy[i][j] = b[i][j];
			}
		}
		return copy;
	}
	
	/***
	 * Returns the resulting state when move is performed
	 * @param move
	 * @return a State
	 */
	public State result(Move move) {
		boolean newTurn = !turn;
		int[][] newBoard = copyBoard(board);
		int fromR = posToRow(move.getFrom());
		int fromC = posToCol(move.getFrom());
		int toR = posToRow(move.getTo());
		int toC = posToCol(move.getTo());
		int pieceToMove = board[fromR][fromC];
		if (turn) {
			if (toR == 7) {
				newBoard[toR][toC] = 3;
			}
			else {
				if (pieceToMove == 1) {
					newBoard[toR][toC] = 1;					
				} else {
					newBoard[toR][toC] = 3;					
				}
			}
		}
		else {
			if (toR == 0) {
				newBoard[toR][toC] = 4;
			}
			else {
				if (pieceToMove == 2) {
					newBoard[toR][toC] = 2;					
				} else {
					newBoard[toR][toC] = 4;					
				}
			}
			
		}
		
		for (int pos : move.getJumps()) {
			int r = posToRow(pos);
			int c = posToCol(pos);
			newBoard[r][c] = 0;
		}
		newBoard[fromR][fromC] = 0;
		
		State newState = new State(newBoard, newTurn);
		
		if (turn) {
			newState.setOScore(oScore + move.getNumJumps());
		} else {
			newState.setPScore(pScore + move.getNumJumps());			
		}
		
		HashMap<String, Integer> newDiff = newState.getDifferences();
		HashMap<String, Integer> newNumPieces = newState.getNumPieces();
		
		for (String piece : KEYS) {
			newDiff.put(piece, numPieces.get(piece) - newNumPieces.get(piece));
		}
		
		return newState;
	}
	
	/**
	 * Determines of the state is a terminal state
	 * @return true if state is a terminal state, false otherwise
	 */
	public boolean isTerminalState() {
		return playerWins() || opponentWins() || isDraw();
	}
	
	/**
	 * Determines if the player won in this state
	 * @return true if the player won, false otherwise
	 */
	public boolean playerWins() {
		if (playerWinsByCapture() || (!turn && legalMoves.isEmpty())) {
			utility = 1;
			return true;
		}
		return false;
	}
	
	/**
	 * Determines if the opponent won in this state
	 * @return true if the opponent won, false otherwise
	 */
	public boolean opponentWins() {
		if (opponentWinsByCapture() || (turn && legalMoves.isEmpty())) {
			utility = 0;
			return true;
		}
		return false;
	}
	
	/**
	 * Determines if there is a draw on the state
	 * @return true if the state is a draw, false otherwise
	 */
	public boolean isDraw() {
		
		if (numPieces.get("playerKing") == 1 && numPieces.get("opponentKing") == 1 && numPieces.get("player") == 0 && numPieces.get("opponent") == 0) {
			return true;
		}
		
		ArrayList<Move> playerMoves = legalMoves;
		turn = !turn;
		ArrayList<Move> opponentMoves = allLegalMoves();
		turn = !turn;
		if (playerMoves.isEmpty() && opponentMoves.isEmpty()) {
			utility = 0;
			return true;
		}
		return false;
		
	}
	
	/**
	 * Determines if the player won in this state by capturing
	 * all of the opponent's pieces
	 * @return true if the player won, false otherwise
	 */
	public boolean playerWinsByCapture() {
		for (int[] row : board) {
			for (int i : row) {
				if (i != 1 && i != 3 && i != 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Determines if the opponent won in this state by capturing
	 * all of the player's pieces
	 * @return true if the opponent won, false otherwise
	 */
	public boolean opponentWinsByCapture() {
		for (int[] row : board) {
			for (int i : row) {
				if (i != 2 && i != 4 && i != 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void setBoard(int[][] board) {
		this.board = board;
	}
	
	public int getPScore() {
		return pScore;
	}

	public void setPScore(int pScore) {
		this.pScore = pScore;
	}

	public int getOScore() {
		return oScore;
	}

	public void setOScore(int oScore) {
		this.oScore = oScore;
	}
	
	public int[][] getBoard() {
		return board;
	}
	
	public boolean isPlayersTurn() {
		return turn;
	}
	
	public int getUtility() {
		return utility;
	}
	
	public HashMap<String, Integer> getDifferences() {
		return differences;
	}
	
	public HashMap<String, Integer> getNumPieces() {
		return numPieces;
	}
	
	public static int RCToPos(int i, int j) {
		return 4 * i + j / 2 + 1;
	}
	
	public static int posToRow(int pos) {
		return (int) (Math.ceil(((double) pos) / 4) - 1);
	}
	
	public static int posToCol(int pos) {
		if (posToRow(pos) % 2 == 0) {
			return 2 * ((pos - 1) % 4) + 1;
		}
		return 2 * ((pos - 1) % 4);
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("State: \n");
		for (int[] row : board) {
			for (int i : row) {
				builder.append(i);
				builder.append(" ");
			}
			builder.append("\n");
		}
		int count = 1;
		builder.append("\nLegal Moves: \n");
		for (Move m : legalMoves) {
			builder.append(count + ". ");
			builder.append(m.toString());
			builder.append("\n");
			count++;
		}
		builder.append("\nTurn: ");
		builder.append(turn ? "Player" : "Opponent");
		builder.append("\n");

		return new String(builder);
	}
	
	public ArrayList<Move> getLegalMoves() {
		return legalMoves;
	}
	
	public boolean getTurn() {
		return this.turn;
	}
	/**
	 * Node that stores a board position. Used for a breadth-first
	 * tree search in addJumpMoves
	 */
	private class PositionNode {
		public int value;
		public ArrayList<Integer> jumpList;
		
		PositionNode(int val) {
			value = val;
			jumpList = new ArrayList<Integer>();
		}
		
	}
	
	public int hashCode() {
		int count = 0;
		int hash = 0;
		for (int[] row : board) {
			for (int i : row) {
				if (count % 2 == 0) {
					hash += (i * i);
				}
				else {
					hash += (-1 * i * i);
				}
				count++;
			}
		}
		return hash;
	}

}
