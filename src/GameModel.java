import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class GameModel {	
	public static final int BASE_MAX_FLEETS = 5;
	public static final int SCORE_TO_SPAWN = 5;
	public static final int TIME_TO_SPAWN = 6000;
	public static final int SPAWN_DISTANCE = 1000;
	
	private static final int SMALL_EXPLOSION = 25;
	private static final int LARGE_EXPLOSION = 100;
	private static final int GAME_OVER_TIME = 300;
	
	public static final Color ENEMY_COLORS[] = {Color.BLUE, Color.MAGENTA, Color.CYAN, Color.GREEN, Color.LIGHT_GRAY, Color.PINK, Color.YELLOW, Color.ORANGE};
	
	public ArrayList<Fleet> fleets;
	public ArrayList<Ship> ships;
	public ArrayList<Laser> lasers;
	public ArrayList<Spark> sparks;
	public Ship heroShip;
	public Fleet heroFleet;
	
	private Random rand;
	
	public boolean gameOver;
	public int gameOverCountdown;
	
	public int score;
	public int time;
	
	public GameModel() {
		reset();
	}
	
	public void reset() {
		gameOver = false;
		gameOverCountdown = GAME_OVER_TIME;
		score = 0;
		time = 0;
		
		ships = new ArrayList<Ship>();
		fleets = new ArrayList<Fleet>();
		lasers = new ArrayList<Laser>();
		sparks = new ArrayList<Spark>();
		rand = new Random();
		
		heroShip = new Ship(new RealPoint(0, 0), 0, Color.RED);
		heroShip.lasersDisabled = true;
		ships.add(heroShip);
		heroFleet = new Fleet();
		heroFleet.addShip(heroShip);
		fleets.add(heroFleet);
	}
	
	private void spawnShip(RealPoint position, double orientation, Color color) {
		Ship s = new Ship(position, orientation, color);
		Fleet f = new Fleet();
		f.ai = new EnemyAI(this, f);
		f.addShip(s);
		fleets.add(f);
		ships.add(s);
	}
	
	private void spawnExplosion(RealPoint position, int size) {
		for(int i=0; i<size; i++) {
			sparks.add(new Spark(position));
		}
	}
	
	public void tick() {
		time++;
		if(gameOver) {
			gameOverCountdown--;
		}
		
		//update positions
		for(Fleet f: fleets) {
			if(f.ai!=null) {
				f.ai.update();
			}
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
		
		for(int i=0; i<sparks.size(); i++) {
			Spark s = sparks.get(i);
			s.update();
			if(s.lifespan<=0) {
				sparks.remove(s);
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
					Laser l = new Laser(s);
					l.isHero = s.fleet==heroFleet;
					lasers.add(l);
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
						spawnExplosion(s.position, LARGE_EXPLOSION);
					} else if(s.pThrusterDisabled) {
						s.sThrusterDisabled = true;
						spawnExplosion(s.hullCorners[2], SMALL_EXPLOSION);
					} else if(s.sThrusterDisabled) {
						s.pThrusterDisabled = true;
						spawnExplosion(s.hullCorners[3], SMALL_EXPLOSION);
					}else {
						if(rand.nextBoolean()) {
							s.pThrusterDisabled = true;
							spawnExplosion(s.hullCorners[3], SMALL_EXPLOSION);
						} else {
							s.sThrusterDisabled = true;
							spawnExplosion(s.hullCorners[2], SMALL_EXPLOSION);
						}
					}
					if(l.isHero) {
						score++;
					}
					lasers.remove(l);
					i--;
					break;
				}
			}
		}
		
		//clean up dead ships and damaged ships far from the hero
		for(int i=0; i<ships.size(); i++) {
			Ship s = ships.get(i);
			
			double heroDist = GameMath.magnitude(new RealPoint(s.position.x-heroShip.position.x, s.position.y-heroShip.position.y));
			if(heroDist>SPAWN_DISTANCE && (s.pThrusterDisabled || s.sThrusterDisabled)) {
				s.dead=true;
				spawnExplosion(s.position, SMALL_EXPLOSION);
			}
			
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
						} else {
							f.ai = new EnemyAI(this, f);
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
					if(s.fleet==heroFleet) {
						score+=5;
					}
				}
			}
		}
		
		//spawn a new ship 1000 units from the hero with probability .001 if there is room
		if(fleets.size()<BASE_MAX_FLEETS + score/SCORE_TO_SPAWN + time/TIME_TO_SPAWN) {
			double offsetOrientation = rand.nextDouble() * Math.PI*2;
			double orientation = rand.nextDouble() * Math.PI*2;
			RealPoint pos = GameMath.polarToCartesian(SPAWN_DISTANCE, offsetOrientation).add(heroShip.position);
			Color color = ENEMY_COLORS[rand.nextInt(ENEMY_COLORS.length)];
			spawnShip(pos, orientation, color);
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
