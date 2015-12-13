import java.util.ArrayList;

public class Fleet {
	public static final double FRICTION = .995;
	public static final double ANGULAR_FRICTION = .995;
	public static final double SPEED_FACTOR = .01;
	public static final double ANGULAR_SPEED_FACTOR = .00002;
	
	public ArrayList<Ship> ships;
	
	public RealPoint center; //center of mass
	public RealPoint velocity; //2d velocity vector
	public double angularSpeed;
	
	private double orientation;//arbitrary, used to maintain ship positions
	
	public Fleet() {
		ships = new ArrayList<Ship>();
		velocity = new RealPoint(0, 0);
	}
	
	public void update() {
		updateDeltas();
		moveShips();
	}
	
	private void moveShips() {
		//use deltas to update center and orientation
		orientation += angularSpeed;
		center.add(velocity);
		
		//move ships to reflect new center and orientation
		for(Ship s: ships) {
			s.orientation = this.orientation + s.fleetOrientation;
			RealPoint newpos = GameMath.polarToCartesian(s.fleetOffset.x, s.fleetOffset.y + orientation);
			newpos.add(center);
			s.position = newpos;
		}
	}
	
	private void updateDeltas() {
		//apply friction
		velocity.multiply(FRICTION);
		angularSpeed = angularSpeed * ANGULAR_FRICTION;
		
		//calculate and apply acceleration from thrusters
		RealPoint acceleration = new RealPoint(0, 0);
		for(Ship s: ships) {
			if(s.pThrusterOn&& !s.pThrusterDisabled) {
				acceleration.add(GameMath.polarToCartesian(1, s.orientation));
			}
			if(s.sThrusterOn && !s.sThrusterDisabled) {
				acceleration.add(GameMath.polarToCartesian(1, s.orientation));
			}
		}
		acceleration.multiply(SPEED_FACTOR/ships.size());
		velocity.add(acceleration);
		
		//calculate and apply angular acceleration from thrusters
		double angularAcceleration = 0;
		for(Ship s: ships) {
			if(s.pThrusterOn && !s.pThrusterDisabled) {
				angularAcceleration += getTorque(s.hullCorners[3], s.orientation);
			}
			if(s.sThrusterOn && !s.sThrusterDisabled) {
				angularAcceleration += getTorque(s.hullCorners[2], s.orientation);
			}
		}
		angularAcceleration *= ANGULAR_SPEED_FACTOR / ships.size();
		angularSpeed += angularAcceleration;
	}
	
	private double getTorque(RealPoint pos, double ori) {
		RealPoint offset = GameMath.cartesianToPolar(center.x-pos.x, center.y-pos.y);
		double theta = ori - offset.y;
		return offset.x * Math.sin(theta) * -1; //the length of the lever arm originating from the center that would be at a right angle to the thruster
	}
	
	private void recalculate() {
		calcCenter();
		orientation = 0;
		for(Ship s: ships) {
			s.fleetOrientation = s.orientation;
			s.fleetOffset = GameMath.cartesianToPolar(s.position.x - center.x, s.position.y - center.y);
		}
	}
	
	private void calcCenter() {
		RealPoint p = new RealPoint(0, 0);
		for(Ship s: ships) {
			p.add(s.position);
		}
		center = new RealPoint(p.x/(double)ships.size(), p.y/(double)ships.size());
	}
	
	public void addShip(Ship s) {
		s.joinFleet(this);
		ships.add(s);
		recalculate();
	}
	
	public void removeShip(Ship s) {
		s.leaveFleet();
		ships.remove(s);
		if(ships.size()>0) {
			recalculate();
		}
	}
	
	public void togglePThruster(boolean on) {
		for(Ship s: ships) {
			s.pThrusterOn=on;
		}
	}
	
	public void toggleSThruster(boolean on) {
		for(Ship s: ships) {
			s.sThrusterOn=on;
		}
	}
	
	public void absorbFleet(Fleet f) {
		while(f.ships.size()>0) {
			Ship s = f.ships.get(0);
			f.removeShip(s);
			addShip(s);			
		}
	}
	
	public void recursiveAdd(Ship s) {
		s.fleet.removeShip(s);
		addShip(s);
		for(Ship s2: s.linkedShips) {
			if(s2.fleet!=this) {
				recursiveAdd(s2);
			}
		}
	}
}
