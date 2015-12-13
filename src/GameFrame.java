import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;


public class GameFrame extends JFrame {
	public static final int FRAME_HEIGHT = 600;
	public static final int FRAME_WIDTH = 800;
	
	private Image image;
	private Graphics secondGraphics;
	
	private GamePanel gamePanel;
	
	public GameFrame()
	{
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        System.exit(0);
		    }
		});
		
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        
        gamePanel = new GamePanel(this.getWidth(), this.getHeight());
        this.add(gamePanel);
        gamePanel.running=true;
        addKeyListener(gamePanel);
        
        
    	this.setVisible(true);
    	this.setTitle("Fleet Command");
    	
    	new Thread(){
    		public void run(){
    			while(true)
    			{
    				update(getGraphics()); 
    				try {
    					Thread.sleep(5);
        		   	} catch (InterruptedException e) {
        		   		e.printStackTrace();
        		   	}
    			}
    		   
    		}
    	}.start();
	}
    
    @Override
	public void update(Graphics g) {
    	
    	
    	if(image==null)
    	{
    		image = createImage(FRAME_WIDTH, FRAME_HEIGHT);
    		if(image==null){
    			return;
    		}
    		secondGraphics = image.getGraphics();
    	}
    	
    	if(gamePanel!=null && gamePanel.running)
    	{
    		gamePanel.update(secondGraphics);
    	}
    	
    	g.drawImage(image, 0, 0, this);   	
    	secondGraphics.clearRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
    	
	}
}
