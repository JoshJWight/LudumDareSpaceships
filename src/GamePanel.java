import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends Component implements KeyListener{
	public boolean gameOver = false;
	private GameModel model;
	private Point offset;
	
	private Image image;
	
	public GamePanel() {
		model = new GameModel();
		//addKeyListener(this);
	}
	
	public void reset() {
		gameOver=false;
		model.reset();
	}
	
	public int getScore() {
		return model.score;
	}
	
	public void update() {
		image = createImage(getParent().getWidth(), getParent().getHeight());
    	if(image==null){
    		return;
    	}
    	Graphics g = image.getGraphics();
		
		
		model.tick();
		Point modelCenter = model.getCenterPoint();
		offset = new Point((this.getParent().getWidth()/2 - modelCenter.x), (this.getParent().getHeight()/2 - modelCenter.y));
		for(Ship s: model.getShips()) {
			g.setColor(s.color);
			g.fillPolygon(makePolygon(s.hullCorners));
			if(!s.pThrusterDisabled) {
				g.setColor(s.pThrusterOn ? Color.ORANGE : Color.LIGHT_GRAY);
				g.fillPolygon(makePolygon(s.pThrusterCorners));
			}
			if(!s.sThrusterDisabled) {
				g.setColor(s.sThrusterOn ? Color.ORANGE : Color.LIGHT_GRAY);
				g.fillPolygon(makePolygon(s.sThrusterCorners));
			}
		}
		
		g.setColor(Color.CYAN);
		for(Laser l: model.getLasers()){
			Point p = normalize(l.position);
			g.fillOval(p.x-5, p.y-5, 10, 10);
		}
		
		g.setColor(Color.ORANGE);
		for(Spark s: model.sparks){
			Point p = normalize(s.position);
			g.fillOval(p.x-1, p.y-1, 2, 2);
		}
		
		g.setColor(Color.WHITE);
		for(Fleet f: model.getFleets()) {
			Point p = normalize(f.center);
			g.fillOval(p.x-3, p.y-3, 6, 6);
		}
		
		g.setFont(g.getFont().deriveFont(Font.PLAIN, 25));
		g.drawString("SCORE: "+model.score, 10, 30);
		
		if(model.gameOver) {
			g.setColor(Color.RED);
			g.setFont(g.getFont().deriveFont(Font.BOLD, 100));
			g.drawString("GAME OVER", this.getParent().getWidth()/2 - 300, this.getParent().getHeight()/2 - 25);
		}
		
		
		
		//draw all things before this point
		
		getGraphics().drawImage(image, 0, 0, this); 
		
		if(model.gameOverCountdown<=0) {
			gameOver=true;
		}
	}
	
	private Polygon makePolygon(RealPoint points[]) {
		int xpoints[] = new int[points.length];
		int ypoints[] = new int[points.length];
		
		for(int i=0; i<points.length; i++) {
			Point p = normalize(points[i]);
			xpoints[i] = p.x;
			ypoints[i] = p.y;
		}
		
		return new Polygon(xpoints, ypoints, points.length);
	}
	
	private Point normalize(RealPoint p) {
		return new Point((int)p.x + offset.x, (int)p.y + offset.y);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_LEFT) {
			model.toggleHeroPThruster(true);
		} else if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
			model.toggleHeroSThruster(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_LEFT) {
			model.toggleHeroPThruster(false);
		} else if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
			model.toggleHeroSThruster(false);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
}
