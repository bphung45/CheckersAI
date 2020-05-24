package mcts;
import java.awt.*;
import javax.swing.*;


/**
 * CheckersGUI
 * A class that creates the checkers application and 
 * implements all necessary panels and buttons.
 * @author Benjamin Phung
 * Last Updated: 05/08/2020
 *
 */
public class CheckersGUI extends JPanel{
 // Label for displaying messages to the user.
	private Board board;
	
    public CheckersGUI() {
    	Font currentFont = new Font("TimesRoman", Font.PLAIN, 20); 
    	Font descriptFont = currentFont.deriveFont(currentFont.getSize() * 1F);
    	Font authorFont = currentFont.deriveFont(currentFont.getSize() * 0.8F);
    	Font titleFont = currentFont.deriveFont(currentFont.getSize() * 2F);
    	
    	JLabel title = new JLabel("Checkers");
    	title.setFont(titleFont);
    	JLabel description = new JLabel("Play with an AI with 3 different levels of difficulty!");
    	JLabel description2 = new JLabel("Or play with another person locally!");
    	description.setFont(descriptFont);
    	description2.setFont(descriptFont);
    	JLabel authors = new JLabel("Program built by Ben Phung and Jung Won Lee");
    	authors.setFont(authorFont);
    	
        setLayout(null); 
        setPreferredSize( new Dimension(1300, 900) );
        Board board = new Board();
        board.setBounds(20,20,804,804);
        
        title.setBounds(988, 120, 200, 120);
        description.setBounds(868, 190, 500, 50);
        description2.setBounds(918, 217, 500, 50);
        authors.setBounds(990, 0, 500, 50);
        add(title);
        add(description);
        add(description2);
        add(authors);
        add(board.getNewGameButton());
        add(board.getResignButton());
        add(board.getMessage());
        add(board.getTwoPlayer());
        add(board.getAI());
        add(board.getEasy());
        add(board.getMedium());
        add(board.getHard());
        this.board = board;
        add(board);
    }
    
    
   
	public static void main(String[] args) {
	      JFrame window = new JFrame("Checkers");
	      CheckersGUI content = new CheckersGUI();
	      window.setContentPane(content);
	      window.pack();
	      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
	      window.setLocation( (screensize.width - window.getWidth())/2,
	            (screensize.height - window.getHeight())/2 );
	      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
	      window.setResizable(false);  
	      window.setVisible(true);
	}

}
