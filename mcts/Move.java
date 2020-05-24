package mcts;

import java.util.ArrayList;
import static mcts.State.*;
import java.io.Serializable;

/**
 * Class that represents a move in the game of checkers
 */
public class Move implements Serializable {
	
	/*
	 * Board positions are numbered as such
	 * 
	 * +---+---+---+---+---+---+---+---+
	 * |   | 1 |   | 2 |   | 3 |   | 4 |
	 * +---+---+---+---+---+---+---+---+
	 * | 5 |   | 6 |   | 7 |   | 8 |   |
	 * +---+---+---+---+---+---+---+---+
	 * |   | 9 |   | 10|   | 11|   | 12|
	 * +---+---+---+---+---+---+---+---+
	 * | 13|   | 14|   | 15|   | 16|   |
	 * +---+---+---+---+---+---+---+---+
	 * |   | 17|   | 18|   | 19|   | 20|
	 * +---+---+---+---+---+---+---+---+
	 * | 21|   | 22|   | 23|   | 24|   |
	 * +---+---+---+---+---+---+---+---+
	 * |   | 25|   | 26|   | 27|   | 28|
	 * +---+---+---+---+---+---+---+---+
	 * | 29|   | 30|   | 31|   | 32|   |
	 * +---+---+---+---+---+---+---+---+
	 * 
	 */

	private static final long serialVersionUID = 2L;

	// Board position from where the piece moves
	private int from;
	
	// Board position to where the piece moves
	private int to;
	
	// Positions that the piece jumps over when
	// performing this move
	private ArrayList<Integer> jumpList;
	
	/**
	 * "Dummy" move for terminal state
	 */
	public Move() {
		from = 0;
		to = 0;
		jumpList = new ArrayList<Integer>();
	}
	
	/**
	 * Constructor
	 * @param from
	 * @param to
	 **/
	public Move(int from, int to) {
		this.from = from;
		this.to = to;
		this.jumpList = new ArrayList<Integer>();
	}
	
	public int getNumJumps() {
		return jumpList.size();
	}
	
	public void addJump(int pos) {
		jumpList.add(pos);
	}
	
	public void addJumps(ArrayList<Integer> list) {
		jumpList.addAll(list);
	}
	
	/**
	 * Getter for from
	 * @return from
	 **/
	public int getFrom() {
		return from;
	}
	
	/**
	 * Getter for to
	 * @return to
	 **/
	public int getTo() {
		return to;
	}
	
	/**
	 * Getter for jumpList
	 * @return jumpList
	 **/
	public ArrayList<Integer> getJumps() {
		return jumpList;
	}
	
	public int hashCode() {
		return to * to - from * from;
	}
	
	public boolean equals(Object other) {
		return this.to == ((Move) (other)).to && this.from == ((Move) other).from;
	}
	
	
	public String toString() {
		//return String.format("%-17s" + "Jumps: " + (jumpList.toString()), "(" + posToRow(from) + ", " + posToCol(from) + ") - (" + posToRow(to) + ", " + posToCol(to) + "),");
		return String.format("%-7s" + "Jumps: " + (jumpList.toString()), from + "-" + to + ","); 
	}
	
}
