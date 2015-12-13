import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends Component implements KeyListener{
	public boolean running;
	private GameModel model;
	private Point offset;
	
	public GamePanel(int width, int height) {
		this.setSize(width, height);
		model = new GameModel();
		//addKeyListener(this);
	}
	
	public void update(Graphics g) {
		model.tick();
		Point modelCenter = model.getCenterPoint();
		offset = new Point((this.getWidth()/2 - modelCenter.x), (this.getHeight()/2 - modelCenter.y));
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
			g.fillOval(p.x, p.y, 10, 10);
		}
		
		g.setColor(Color.WHITE);
		for(Fleet f: model.getFleets()) {
			Point p = normalize(f.center);
			g.fillOval(p.x, p.y, 5, 5);
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
