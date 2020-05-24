package mcts;

import static mcts.State.posToCol;
import static mcts.State.posToRow;
import static mcts.State.RCToPos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import minimax.MinimaxCheckers;

/**
 * A class that builds the board graphics, updates it, and implements the bots' moves
 * @author Benjamin Phung
 * Last Updated: 05/08/2020
 *
 */
public class Board extends JPanel implements ActionListener, MouseListener{
	
	private JButton newGameButton;  
	private JButton resignButton;  
	private boolean easyDiff;
	private boolean medDiff;
	private boolean hardDiff;
	private boolean twoPlayerMode;
	private JButton easy;
	private JButton medium;
	private JButton hard;
	private JButton twoPlayer;
	private JButton AI;
	private JLabel message;
	private State state;
	
	private boolean start;
	private boolean moved;
	private int selectedRow;
	private int selectedCol;

	Board() {
        addMouseListener(this);
        resignButton = new JButton("Resign");
        newGameButton = new JButton("New Game");
        twoPlayer = new JButton("2-Player");
        AI = new JButton("AI");
        easy = new JButton("Easy");
        medium = new JButton("Medium");
        hard = new JButton("Insane");
        
        resignButton.addActionListener(this);
        newGameButton.addActionListener(this);
        twoPlayer.addActionListener(this);
        AI.addActionListener(this);
        easy.addActionListener(this);
        medium.addActionListener(this);
        hard.addActionListener(this);
        
        message = new JLabel("",JLabel.CENTER);
        this.state = new State(new int[8][8], false, 0, 0);
        
        newGameButton.setBounds(1000, 300, 120, 30);
        resignButton.setBounds(1000, 300, 120, 30);
        message.setBounds(808, 200, 500, 500);
        twoPlayer.setBounds(900, 300, 120, 30);
        AI.setBounds(1100, 300, 120, 30);
        easy.setBounds(880, 300, 120, 30);
        medium.setBounds(1000, 300, 120, 30);
        hard.setBounds(1120, 300, 120, 30);
        
        resignButton.setVisible(false);
        easy.setVisible(false);
        medium.setVisible(false);
        hard.setVisible(false);
        AI.setVisible(false);
    	twoPlayer.setVisible(false);
        
        Font currentFont = message.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
        message.setFont(newFont);
        
        message.setText("Start New Game");
        
        doNewGame();
	}
	

	public void checkWin() {
	      if (state.isTerminalState()) {
	        	newGameButton.setEnabled(true);
	        	
	        	if (twoPlayerMode) {
	        		if (state.playerWins()) 
	        			message.setText("<html><center>BLACK WINS!</center> <br> <center>CLICK NEW GAME TO START AGAIN!</center><html>");
	        		else if (state.opponentWins())
	        			message.setText("<html><center>RED WINS!</center> <br> <center>CLICK NEW GAME TO START AGAIN!</center><html>");
	        		else 
	        			message.setText("<html><center>SERIOUSLY? A DRAW?</center> <br> <center>CLICK NEW GAME TO START AGAIN!</center><html>");
	        	}
	        	else {
	        		if (state.playerWins()) 
	        			message.setText("<html><center>THE END OF THE HUMAN RACE IS NIGH.</center> <br> <center>CLICK NEW GAME TO TRY AGAIN!</center><html>");
	        		else if(state.opponentWins())
	        			message.setText("<html><center>THE HUMAN RACE SURVIVES ONE MORE GAME.</center> <br> <center>CLICK NEW GAME TO START AGAIN!</center><html>");
	        		else
	        			message.setText("<html><center>SERIOUSLY? A DRAW?</center> <br> <center>CLICK NEW GAME TO START AGAIN!</center><html>");
	        	} 
	        }
	}
	
	
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
        int col = (e.getX() - 3) / 100;
        int row = (e.getY() - 3) / 100;
        if (col >= 0 && col < 8 && row >= 0 && row < 8) {
        	clickSquare(row,col);
        }
        	
        checkWin();
        
	}

	
	public void clickSquare(int row, int col) {
		/*
	  	System.out.println("EasyDiff: " + easyDiff);
    	System.out.println("MedDiff: " + medDiff);
    	System.out.println("HardDiff: " + hardDiff);
    	*/
		
		
		
        if (state.getTurn() && twoPlayerMode && (state.getBoard()[row][col] == 1 || state.getBoard()[row][col] == 3)) {
            selectedRow = row;
            selectedCol = col;
        	message.setText("BLACK PIECE SELECTED");
        	moved = false;
        	repaint();
            return;
        }
        if (!state.getTurn() && (state.getBoard()[row][col] == 2 || state.getBoard()[row][col] == 4)) {
            selectedRow = row;
            selectedCol = col;
        	message.setText("RED PIECE SELECTED");
        	moved = false;
        	repaint();
            return;
        }
        
        
        if (selectedRow < 0) {
           message.setText("Click the piece you want to move.");
           return;
        }
        
        int to = RCToPos(row, col);
        int from = RCToPos(selectedRow, selectedCol);
        Move m = new Move(from, to);
    	

    	ArrayList<Move> moves = state.getLegalMoves();
        
    	if (moves.contains(m)) {
    		moved = true;
    		selectedRow = -1;
    		selectedCol = -1;
    		int index = moves.indexOf(m);
    		
        	m.addJumps(state.getLegalMoves().get(index).getJumps());
    	
        	state = state.result(m);
            
            if (!state.isPlayersTurn() && twoPlayerMode) {
            	message.setText("RED TURN");
            } else if (state.isPlayersTurn() && twoPlayerMode){
            	message.setText("BLACK TURN");
            } else {
            	message.setText("COMPUTING...");
            }
            repaint();
            return;
        }
  
    	message.setText("CLICK A VALID PIECE OR MOVE");
  
    	
    }  
	
	public void mouseReleased(MouseEvent evt) {
		
		if (state.isPlayersTurn() && !twoPlayerMode) {
    		if (easyDiff) {
    			MinimaxCheckers easyAlgo = new MinimaxCheckers(state);
    			easyAlgo.setDepthLimit(5);
				Move yourMove = easyAlgo.alphaBetaMove();
				state = state.result(yourMove);
				message.setText("RED TURN");
            }
    		if (medDiff) {
	           	MinimaxCheckers medAlgo = new MinimaxCheckers(state);
	           	medAlgo.setDepthLimit(10);
				Move yourMove = medAlgo.alphaBetaMove();
				state = state.result(yourMove);
				message.setText("RED TURN");
			}
    		if (hardDiff) {
    			MCTSCheckers search = new MCTSCheckers();
    			search.setRootState(state);
    			Move hisMove = search.getBestMove();
				state = state.result(hisMove);
				message.setText("RED TURN");
            }
    		checkWin();
    		moved=false;
    		repaint();
    		return;
		}	
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
        Object src = e.getSource();
        if (src == newGameButton) {
        	twoPlayer.setVisible(true);
        	AI.setVisible(true);
        	newGameButton.setVisible(false);
        	start = false;
        	message.setText("Select Opponent");
        	doNewGame();
        } else if (src == twoPlayer) {
        	twoPlayer.setVisible(false);
        	AI.setVisible(false);
        	newGameButton.setVisible(true);
        	twoPlayerMode = true;
        	start = true;
        	message.setText("GAME START! RED MOVE FIRST");
        	cleanBoard();
        } else if (src == AI) {
        	twoPlayer.setVisible(false);
        	AI.setVisible(false);
        	easy.setVisible(true);
        	medium.setVisible(true);
        	hard.setVisible(true);
        	message.setText("<html>Easy = alpha-beta agent with depth limit of 5 <br>"
        			+ "Medium = alpha-beta agent with depth limit of 10 <br>"
        			+ " Insane = MCTS (every move is optimal)<html>");
        } else if (src == easy) {
        	easy.setVisible(false);
        	medium.setVisible(false);
        	hard.setVisible(false);
        	newGameButton.setVisible(true);
        	easyDiff = true;
        	start = true;
        	message.setText("<html> GAME START! RED MOVE FIRST <br> <center>DIFFICULTY: EASY</center><html>");
        	cleanBoard();
        } else if (src == medium) {
        	easy.setVisible(false);
        	medium.setVisible(false);
        	hard.setVisible(false);
        	newGameButton.setVisible(true);
        	medDiff = true;
        	start = true;
        	message.setText("<html> GAME START! RED MOVE FIRST <br> <center>DIFFICULTY: MEDIUM</center><html>");
        	cleanBoard();
        } else if (src == hard) {
        	easy.setVisible(false);
        	medium.setVisible(false);
        	hard.setVisible(false);
        	newGameButton.setVisible(true);
        	hardDiff = true;
        	start = true;
        	message.setText("<html> GAME START! RED MOVE FIRST <br> <center>DIFFICULTY: INSANE</center><html>");
        	cleanBoard();
        }
	}

	public static void initialize(int[][] board) {
		for (int i = 1; i < 13; i++) {
			board[posToRow(i)][posToCol(i)] = 1;
		}
		for (int i = 21; i < 33; i++) {
			board[posToRow(i)][posToCol(i)] = 2;
		}
	}
	
	
    public void doNewGame() {
    	int[][] board = new int[8][8];
    	initialize(board);
        state = new State(board, false, 0, 0);
        selectedRow = -1;
        
    	easyDiff = false;
    	medDiff = false;
    	hardDiff = false;
    	twoPlayerMode = false;
    	
        repaint();
    }
    
    public void cleanBoard() {
    	int[][] board = new int[8][8];
    	initialize(board);
        state = new State(board, false, 0, 0);
        selectedRow = -1;
        repaint();
    }

    public void paintComponent(Graphics g) {
       
       //black border
       g.setColor(Color.black);
       g.drawRect(0,0,getSize().width - 1,getSize().height-1);
       g.drawRect(1,1,getSize().width - 3,getSize().height-3);
       
       
       Font currentFont = g.getFont();
       Font newFont = currentFont.deriveFont(currentFont.getSize() * 5F);
  
       
       for (int row = 0; row < 8; row++) {
          for (int col = 0; col < 8; col++) {
             if ( row % 2 == col % 2 )
                g.setColor(Color.LIGHT_GRAY);
             else
                g.setColor(Color.GRAY);
             g.fillRect(2 + col*100, 2 + row*100, 100, 100);
             
             
             switch (state.getBoard()[row][col]) {
             
             case 1:
            	g.setFont(currentFont);
                g.setColor(Color.BLACK);
                g.fillOval(12 + col*100, 12 + row*100, 80, 80);
                break;
             case 2:
            	g.setFont(currentFont);
                g.setColor(Color.RED);
                g.fillOval(12 + col*100, 12 + row*100, 80, 80);
                break;
             case 3:
            	g.setFont(newFont);
                g.setColor(Color.BLACK);
                g.fillOval(12 + col*100, 12 + row*100, 80, 80);
                g.setColor(Color.WHITE);
                g.drawString("K", 30 + col*100, 75 + row*100);
                break;
             case 4:
            	g.setFont(newFont);
                g.setColor(Color.RED);
                g.fillOval(12 + col*100, 12 + row*100, 80, 80);
                g.setColor(Color.WHITE);
                g.drawString("K", 30 + col*100, 75 + row*100);
                break;
             }
          }
       }
       
       //CYAN BORDER FOR PIECES THAT CAN BE MOVED
       if (!state.isTerminalState() && start || (!state.isTerminalState() && start && !twoPlayerMode && !state.isPlayersTurn())) {
    	   g.setColor(Color.MAGENTA);
    	   ArrayList<Move> legalMoves = state.allLegalMoves();
    	   for (int i = 0; i < legalMoves.size(); i++) {
    		   Move move = legalMoves.get(i);
    		   int row = posToRow(move.getFrom());
    		   int col = posToCol(move.getFrom());
    		   g.drawRect(2 + col*100, 2 + row*100, 99, 99);
    		   g.drawRect(1 + col*100, 1 + row*100, 101, 101);
    	   }
          
    	   if (selectedRow >= 0 && !moved) {
        	
    		   //draw white border around that piece
    		   g.setColor(Color.white);
    		   g.drawRect(2 + selectedCol*100, 2 + selectedRow*100, 99, 99);
    		   g.drawRect(1 + selectedCol*100, 1 + selectedRow*100, 101, 101);
    		   
        	   //draw green border for places where the piece can can be moved
    		   g.setColor(Color.green);
    		   for (int i = 0; i < legalMoves.size(); i++) {
    			   Move move = legalMoves.get(i);
    			   int row = posToRow(move.getTo());
    			   int col = posToCol(move.getTo());
    			   g.drawRect(2 + col*100, 2 + row*100, 99, 99);
    			   g.drawRect(1 + col*100, 1 + row*100, 101, 101);
           }
        }
        
     }
      

    }  

	
    public JButton getNewGameButton() {
    	return this.newGameButton;
    }
    
    public JButton getResignButton() {
    	return this.newGameButton;
    }
    
    public JButton getTwoPlayer() {
    	return this.twoPlayer;
    }
    
    public JLabel getMessage() {
    	return this.message;
    }
    
    public JButton getAI() {
    	return this.AI;
    }
    
    public JButton getEasy() {
    	return this.easy;
    }
    
    public JButton getMedium() {
    	return this.medium;
    }
    
    public JButton getHard() {
    	return this.hard;
    }
    public State getState() {
    	return this.state;
    }
    
    public void mouseClicked(MouseEvent evt) { }
    public void mouseEntered(MouseEvent evt) { }
    public void mouseExited(MouseEvent evt) { }

}
