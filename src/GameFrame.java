import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class GameFrame extends JFrame implements KeyListener {
	public static final String TITLE = "Fleet Command";
	public static final String INSTRUCTIONS = "Left and right arrow keys control your ship's thrusters.<br/>Ram into other ships to add them to your fleet.<br/>Expand your fleet and destroy enemy ships to earn points.";
	
	
	public static final int FRAME_HEIGHT = 600;
	public static final int FRAME_WIDTH = 800;
	
	private GamePanel gamePanel;
	private JPanel splashPanel;
	private JPanel playAgainPanel;
	private JLabel highScores;
	private JLabel playAgainMessage;
	
	private JButton fuckThisStupidShit;
	
	private Font titleFont;
	private Font textFont;
	
	private boolean gameActive;
	
	private int resize;
	
	private ArrayList<String> highScoreList;
	
	
	public GameFrame()
	{
		highScoreList = new ArrayList<String>();
		
		readHighScores();
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        System.exit(0);
		    }
		});
		
		titleFont = new Font("Helvetica", Font.BOLD, 50);
		textFont = new Font("Helvetica", Font.BOLD, 20);
		
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        
        makeSplashPanel();
        makePlayAgainPanel();
        gamePanel = new GamePanel();
        this.addKeyListener(this);
        splashPanel.addKeyListener(this);
        this.add(splashPanel, BorderLayout.CENTER);
        
        //this is a dirty hack to get key input. fuck swing.
        fuckThisStupidShit = new JButton();
        fuckThisStupidShit.setVisible(false);
        fuckThisStupidShit.setSize(0, 0);
        this.add(fuckThisStupidShit, BorderLayout.NORTH);
        fuckThisStupidShit.addKeyListener(this);
        fuckThisStupidShit.setFocusable(true);
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
    	this.setTitle("Fleet Command");
    	
    	repaint();
    	playAgainPanel.update(getGraphics());
    	playAgainPanel.repaint();
    	resize=1;
    	
    	
    	new Thread(){
    		public void run(){
    			while(true)
    			{
    				update(); 
    				try {
    					Thread.sleep(5);
        		   	} catch (InterruptedException e) {
        		   		e.printStackTrace();
        		   	}
    			}
    		   
    		}
    	}.start();
	}
    
	public void update() {
    	//this is a dirty hack to force shit to reload. fuck swing.
		if(resize==1) {
			resize=2;
			this.setSize(this.getWidth()+1, this.getHeight()+1);
		} else if(resize==2) {
			resize=0;
			this.setSize(this.getWidth()+1, this.getHeight()+1);
		}
		
    	
    	if(gamePanel!=null && gamePanel.gameOver) {
			endGame();
		}
    		
    	if(gamePanel!=null && gameActive)
    	{    		
    		gamePanel.update();
    		
    	}
	}
    
    private void startGame() {
    	this.remove(splashPanel);
    	this.remove(playAgainPanel);
        this.add(gamePanel, BorderLayout.CENTER);
        
        KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(fuckThisStupidShit);
        gameActive=true;
        repaint();
        gamePanel.update(getGraphics());
        resize=1;
    }
    
    private void endGame() {
    	gameActive=false;
    	//get the score from the game
    	int score = gamePanel.getScore();
    	playAgainMessage.setText(getColoredText("You scored " + score + " points. Play again?", "white"));
    	updateHighScores(score);
    	
    	//dispose of the game panel
    	removeKeyListener(gamePanel);
    	this.remove(gamePanel);
    	gamePanel.reset();
    	
    	this.add(playAgainPanel, BorderLayout.CENTER);
    	repaint();
    	playAgainPanel.update(getGraphics());
    	playAgainPanel.repaint();
    	resize=1;
    	
    }
    
    private void makeSplashPanel() {
    	splashPanel = new JPanel();
    	splashPanel.setLayout(new BoxLayout(splashPanel, BoxLayout.X_AXIS));
    	splashPanel.setBackground(Color.BLACK);
    	
    	ArrayList<Component> components = new ArrayList<Component>();
    	
    	JLabel title = new JLabel(getColoredText(TITLE, "white"));
    	title.setFont(titleFont);
    	title.setPreferredSize(new Dimension(400, 50));
    	components.add(title);
    	JLabel instructions = new JLabel(getColoredText(INSTRUCTIONS, "white"));
    	instructions.setPreferredSize(new Dimension(400, 150));
    	instructions.setFont(textFont);
    	components.add(instructions);
    	JButton playButton = new JButton();
    	playButton.setText("Play");
    	playButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
    	});
    	playButton.setFocusable(false);
    	playButton.addKeyListener(this);
    	playButton.setPreferredSize(new Dimension(400, 50));
    	components.add(playButton);
    	
    	boxFill(splashPanel, components);
    }
    
    private void makePlayAgainPanel() {
    	playAgainPanel = new JPanel();
    	playAgainPanel.setBackground(Color.BLACK);
    	playAgainPanel.setLayout(new BoxLayout(playAgainPanel, BoxLayout.X_AXIS));
    	
    	ArrayList<Component> components = new ArrayList<Component>();
    	
    	highScores = new JLabel();
    	highScores.setFont(textFont);
    	
    	components.add(highScores);
    	playAgainMessage = new JLabel();
    	playAgainMessage.setFont(textFont);
    	components.add(playAgainMessage);
    	JButton playButton = new JButton();
    	playButton.setText("Play Again");
    	playButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
    	});
    	playButton.setFocusable(false);
    	playButton.setPreferredSize(new Dimension(400, 50));
    	components.add(playButton);
    	
    	boxFill(playAgainPanel, components);
    }

    private void boxFill(JPanel panel, ArrayList<Component> components) {
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	
    	panel.add(Box.createVerticalGlue());
    	for(Component c: components) {
    		JPanel p = new JPanel();
    		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    		JLabel fuckThisShitToo = new JLabel();
        	fuckThisShitToo.setPreferredSize(new Dimension(200, 50));
        	p.add(fuckThisShitToo);
    		//p.add(Box.createHorizontalGlue());
    		p.add(c);
    		p.add(Box.createHorizontalGlue());
    		p.setBackground(Color.BLACK);
    		panel.add(p);
    	}
    	panel.add(Box.createVerticalGlue());
    }
    
    private String getColoredText(String text, String color) {
    	return "<html><font color='" + color + "'>" + text + "</font></html>";
    }
    
    private void readHighScores() {
    	try {
			BufferedReader br = new BufferedReader(new FileReader("highscores.txt"));
			highScoreList = new ArrayList<String>();
			String line;
			while((line=br.readLine()) != null && highScoreList.size()<10) {
				highScoreList.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    private void writeHighScores() {
    	try {
			PrintWriter pw = new PrintWriter("highscores.txt");
			for(String score: highScoreList) {
				pw.println(score);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    private void updateHighScores(int score) {
    	DateFormat df = new SimpleDateFormat("MM/dd/yy-HH:mm");
    	
    	for(int i=0; i<highScoreList.size(); i++) {
    		if(value(highScoreList.get(i))<score) {
    			highScoreList.add(i, df.format(new Date()) + ": " + score);
    			break;
    		} else if(i==highScoreList.size()-1) {
    			highScoreList.add(df.format(new Date()) + ": " + score);
    			break;
    		}
    	}
    	if(highScoreList.size()==0) {
    		highScoreList.add(df.format(new Date()) + ": " + score);
    	}
    	
    	writeHighScores();
    	
    	String highScoreText = "High Scores:";
    	for(int i=0; i<highScoreList.size() && i<10; i++) {
    		highScoreText+= "<br/>" + highScoreList.get(i);
    	}
    	
    	highScores.setText(getColoredText(highScoreText, "yellow"));
    }
    
    private int value(String score) {
    	return Integer.valueOf(score.split(" ")[1]);
    }
    
	@Override
	public void keyPressed(KeyEvent e) {
		if(gameActive) {
			gamePanel.keyPressed(e);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(gameActive) {
			gamePanel.keyReleased(e);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(gameActive) {
			gamePanel.keyTyped(e);
		}
	}
}
