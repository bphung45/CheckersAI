package mcts;

import java.util.*;

/**
 * Class that represents a node in the Monte Carlo
 * search tree
 * @author Jung Won Lee
 */
public class MCTSNode {
	// The game state of the node
	private State state;
	
	// The parent of this node
	private MCTSNode parent;
	
	// N(s) : number of total visits to this.state
	private int numVisits; 
	
	// Q(s, a) : estimated expected utility of doing a move in this.state
	private HashMap<Move, Double> Q; 
	
	// N(s, a) : total number of times a move was performed in this.state
	private HashMap<Move, Integer> countsByMove; 
	
	// ArrayLists that store the children of this node and the
	// corresponding actions that produced them
	private ArrayList<Move> moveForChild;
	private ArrayList<MCTSNode> children;
	
	// true if player's turn, false otherwise
	private boolean turn;
	
	// The legal moves from this node
	private ArrayList<Move> possibleMoves;
	
	// The Move that is performed during a simulation
	private Move moveTaken;
	
	private int nthState;
	
	/**
	 * Constructor
	 * @param state
	 * @param parent
	 */
	public MCTSNode(State state, MCTSNode parent) {
		this.state = new State(state.getBoard(), state.isPlayersTurn());
		this.possibleMoves = this.state.getLegalMoves();
		this.parent = parent;
		this.turn = state.isPlayersTurn();
		this.moveTaken = null;
		numVisits = 0;
		children = new ArrayList<MCTSNode>();
		moveForChild = new ArrayList<Move>();
		countsByMove = new HashMap<Move, Integer>();
		Q = new HashMap<Move, Double>();
		for (Move move : possibleMoves) {
			countsByMove.put(move, 0);
			Q.put(move, 0.0);
		}
		nthState = 0;
	}
	
	/***
	 * Expands the node by adding a child
	 * @return a new child of the node
	 */
	public MCTSNode expand() {
		Move move = possibleMoves.get(nthState);
		nthState++;
		MCTSNode child = null;
		moveTaken = move;
		child = new MCTSNode(state.result(move), this);

		children.add(child);
		moveForChild.add(move);
		return child;
	}
	
	/***
	 * Simulates a game from the node by randomly selecting
	 * a move until a terminal state is reached.
	 * @return the utility value of the terminal state 
	 */
	public int defaultSim() {
		State currentState = new State(state.getBoard(), turn);
		int count = 0;
		while (!currentState.isTerminalState()) {
			Move currentMove = defaultPolicy(currentState);
			if (count == 0) {
				moveTaken = currentMove;
			}
			currentState = currentState.result(currentMove);
			count++;
		} 
		if (count == 0) { // If this.state is a terminal state, add the dummy Move
			moveTaken = new Move();
		}
		return currentState.getUtility();
	}
	
	/***
	 * Uses the UCT value to select the index of the best action 
	 * (the action with the highest expected utility) and the corresponding
	 * child node that results from the best action. This is the tree
	 * policy in a simulated game.
	 * @param c the exploration factor (theoretically sqrt(2))
	 * @return the index of the best action and corresponding child
	 */
	public int bestArgs(double c) {
		int arg = 0;
		if (turn) { // Find the action that maximizes UCT value
			for (int i = 0; i < moveForChild.size(); i++) {
				double currentQ = Q.get(moveForChild.get(i)) + c * Math.sqrt(Math.log(numVisits) / countsByMove.get(moveForChild.get(i)));
				double bestQ = Q.get(moveForChild.get(arg)) + c * Math.sqrt(Math.log(numVisits) / countsByMove.get(moveForChild.get(arg)));
				if (currentQ > bestQ) {
					arg = i;
				}
			}
		}
		else { // Find the action that minimizes UCT value
			for (int i = 0; i < moveForChild.size(); i++) {
				double currentQ = Q.get(moveForChild.get(i)) - c * Math.sqrt(Math.log(numVisits) / countsByMove.get(moveForChild.get(i)));
				double bestQ = Q.get(moveForChild.get(arg)) - c * Math.sqrt(Math.log(numVisits) / countsByMove.get(moveForChild.get(arg)));
				if (currentQ < bestQ) {
					arg = i;
				}
			}
		}
		moveTaken = moveForChild.get(arg);
		return arg;
	}
	
	/***
	 * The default policy in a simulated game, which is randomly
	 * selecting a legal move
	 * @return a random legal move
	 */
	public Move defaultPolicy(State s) {
		ArrayList<Move> moves = s.getLegalMoves(); 
		int randI = (int) (Math.random() * moves.size());
		return moves.get(randI);
	}
	
	/**
	 * Backs the utility of a reached terminal state and other
	 * values up through the tree after a simulation.
	 * @param the utility of reached terminal state in a simulation
	 */
	public void backPropagate(int utility) {
		if (!state.isTerminalState()) {
			this.numVisits += 1;
			int newCount = countsByMove.get(moveTaken) + 1;
			double newQ = Q.get(moveTaken) + (utility - Q.get(moveTaken)) / (1.0 * this.countsByMove.get(moveTaken));
			this.countsByMove.put(moveTaken, newCount);
			this.Q.put(moveTaken, newQ);
		}
		
		if (this.parent != null) {
			this.parent.backPropagate(utility);
		}
	}
	
	public void setMoveTaken(Move m) {
		moveTaken = m;
	}
	
	public boolean fullyExpanded() {
		return  nthState >= possibleMoves.size();
	}
	
	public State getState() {
		return state;
	}
	
	public ArrayList<MCTSNode> getChildren() {
		return children;
	}
	
	public ArrayList<Move> getMoveForChild() {
		return moveForChild;
	}
	
	public HashMap<Move, Integer> getCountsByMoves() {
		return countsByMove;
	}
	
	public HashMap<Move, Double> getQ() {
		return Q;
	}
	
}
