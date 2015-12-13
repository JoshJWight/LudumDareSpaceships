import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class GameModel {	
	private ArrayList<Fleet> fleets;
	private ArrayList<Ship> ships;
	private ArrayList<Laser> lasers;
	private Ship heroShip;
	private Fleet heroFleet;
	
	private Random rand;
	
	public boolean gameOver = false;
	public int gameOverCountdown = 1000;
	
	public GameModel() {
		ships = new ArrayList<Ship>();
		fleets = new ArrayList<Fleet>();
		lasers = new ArrayList<Laser>();
		rand = new Random();
		
		heroShip = new Ship(new RealPoint(0, 0), 0, Color.RED);
		heroShip.lasersDisabled = true;
		ships.add(heroShip);
		heroFleet = new Fleet();
		heroFleet.addShip(heroShip);
		fleets.add(heroFleet);
		
		spawnShip(new RealPoint(100, 30), Math.PI/4, Color.BLUE);
		spawnShip(new RealPoint(200, 200), Math.PI/2, Color.GREEN);
		spawnShip(new RealPoint(-200, -100), Math.PI/2, Color.GREEN);
	}
	
	private void spawnShip(RealPoint position, double orientation, Color color) {
		Ship s = new Ship(position, orientation, color);
		Fleet f = new Fleet();
		f.addShip(s);
		fleets.add(f);
		ships.add(s);
	}
	
	public void tick() {
		if(gameOver) {
			gameOverCountdown--;
		}
		
		//update positions
		for(Fleet f: fleets) {
			f.update();
		}
		
		for(int i=0; i<lasers.size(); i++) {
			Laser l = lasers.get(i);
			l.update();
			if(l.age>Laser.MAX_AGE) {
				lasers.remove(l);
				i--;
			}
		}
		
		//update ship corner positions for drawing
		for(Ship s: ships) {
			s.updateBounds();
		}
		
		//fire missiles from ships that are ready to fire
		for(Ship s: ships) {
			if(!s.lasersDisabled) {
				s.recharge--;
				if(s.recharge<=0) {
					lasers.add(new Laser(s));
					s.recharge = Ship.MAX_RECHARGE/2 + rand.nextInt(Ship.MAX_RECHARGE/2);
				}
			}
		}
		
		//check whether ships get hit by lasers
		for(int i=0; i<lasers.size(); i++) {
			Laser l = lasers.get(i);
			for(Ship s: ships) {
				if(GameMath.rectContainsPoint(s.hullCorners, l.position)) {
					if(s.pThrusterDisabled && s.sThrusterDisabled) {
						s.dead = true;
					} else if(s.pThrusterDisabled || s.sThrusterDisabled) {
						s.sThrusterDisabled = true;
						s.pThrusterDisabled = true;
					} else {
						if(rand.nextBoolean()) {
							s.pThrusterDisabled = true;
						} else {
							s.sThrusterDisabled = true;
						}
					}
					lasers.remove(l);
					i--;
					break;
				}
			}
		}
		
		//clean up dead ships
		for(int i=0; i<ships.size(); i++) {
			Ship s = ships.get(i);
			if(s.dead) {
				ArrayList<Ship> linkedShips = new ArrayList<Ship>();
				while(s.linkedShips.size()>0){
					Ship s2 = s.linkedShips.get(0);
					s2.linkedShips.remove(s);
					s.linkedShips.remove(s2);
					linkedShips.add(s2);
				}
				
				if(s==heroShip) {
					if(linkedShips.size()>0) {
						heroShip = linkedShips.get(0);
						heroShip.color = Color.RED;
					}
					else{
						gameOver=true;
					}
				}
												
				if(linkedShips.size()>0) {
					for(Ship s2: linkedShips) {
						Fleet f = new Fleet();
						f.recursiveAdd(s2);
						fleets.add(f);
						if(f.ships.contains(heroShip)) {
							heroFleet = f;
						}
					}
				}
				Fleet f = s.fleet;
				f.removeShip(s);
				if(f.ships.size()==0) {
					fleets.remove(f);
				}
				ships.remove(s);
				i--;
			}
		}
		
		//check whether any ships should join a new fleet
		for(Ship s: ships) {
			for(Ship t: ships) {
				Fleet f = s.fleet;
				Fleet g = t.fleet;
				if(f != g && GameMath.rectsIntersect(s.hullCorners, t.hullCorners)) {
					s.linkedShips.add(t);
					t.linkedShips.add(s);
					if(g.ships.contains(heroShip)){
						g.absorbFleet(f);
						fleets.remove(f);
					} else {
						f.absorbFleet(g);
						fleets.remove(g);
					}
				}
			}
		}
	}
	
	public ArrayList<Ship> getShips() {
		return ships;
	}
	
	public ArrayList<Fleet> getFleets() {
		return fleets;
	}
	
	public ArrayList<Laser> getLasers() {
		return lasers;
	}
	
	public Point getCenterPoint() {
		return heroShip.position.toPoint();
	}
	
	public void toggleHeroPThruster(boolean on) {
		heroFleet.togglePThruster(on);
	}
	
	public void toggleHeroSThruster(boolean on) {
		heroFleet.toggleSThruster(on);
	}
}
