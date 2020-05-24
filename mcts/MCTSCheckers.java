package mcts;

/**
 * Class that implements Monte Carlo tree search
 * to play checkers
 */
public class MCTSCheckers {
	
	// The root of the search tree
	private MCTSNode root;
	private State rootState;
	
	// Number of iterations
	private final static int MAXITER = 4000;
	
	// Exploration factor
	private final static double C = 1.41;
	
	/**
	 * Constructor
	 */
	public MCTSCheckers() {
		rootState = null;
		root = null;
	}
	
	/**
	 * Constructor
	 * @param state
	 * @param visitedStates
	 */
	public MCTSCheckers(State state) {
		rootState = state;
		root = new MCTSNode(rootState, null);
	}
	
	/**
	 * Calculates and returns the best move from the root state using
	 * the Monte Carlo tree search algorithm
	 */
	public Move getBestMove() {
		for (int i = 0; i < MAXITER; i++) {
			MCTSNode current = treePolicy();
			int utility = current.defaultSim();
			current.backPropagate(utility);
		}
		Move bestMove = root.getMoveForChild().get(root.bestArgs(0));
		return bestMove;
	}
	
	public MCTSNode treePolicy() {
		MCTSNode currentNode = root;
		while(!currentNode.getState().isTerminalState()) {
			if(!currentNode.fullyExpanded()) {
				return currentNode.expand();
			}
			else {
				int arg = currentNode.bestArgs(C);
				currentNode = currentNode.getChildren().get(arg);
			}
		}
		return currentNode;
	}
	
	public void setRootState(State state) {
		rootState = state;
		root = new MCTSNode(rootState, null);
	}
	
	public MCTSNode getRoot() {
		return root;
	}
}
