import java.awt.Color;
import java.util.ArrayList;

public class Ship {
	public static final int SHIP_WIDTH = 20;
	public static final int SHIP_LENGTH = 50;
	public static final int THRUSTER_WIDTH = 5;
	public static final int THRUSTER_LENGTH = 15;
	public static final int MAX_RECHARGE = 250;
	
	
	public RealPoint position;
	public double orientation; //angle between the length axis of the ship and the positive x axis
	public Color color;
	public Fleet fleet; 
	
	public ArrayList<Ship> linkedShips;
	
	public double fleetOrientation; //angle between the fleet axis and the length axis of the ship
	public RealPoint fleetOffset; //polar coordinates of ship position relative to fleet position
	
	public boolean pThrusterOn = false;
	public boolean sThrusterOn = false;
	
	public boolean pThrusterDisabled = false;
	public boolean sThrusterDisabled = false;
	
	public boolean lasersDisabled = false;
	
	public boolean dead = false;
	
	//positions of the ship's corners - front, rear, starboard, port
	public RealPoint hullCorners[];
	public RealPoint sThrusterCorners[];//starboard
	public RealPoint pThrusterCorners[];//port
	
	//number of ticks to next shot
	public int recharge = MAX_RECHARGE;
	
	public Ship(RealPoint position, double orientation, Color color) {
		this.position = position;
		this.orientation = orientation;
		this.color = color;
		linkedShips = new ArrayList<Ship>();
		updateBounds();
	}
	
	public void joinFleet(Fleet f) {
		fleet = f;
	}
	
	public void leaveFleet() {
		fleet = null;
	}
	
	public void updateBounds() {
		hullCorners = GameMath.getRectCorners(position, orientation, SHIP_WIDTH, SHIP_LENGTH);
		pThrusterCorners = GameMath.getRectCorners(hullCorners[3], orientation, THRUSTER_WIDTH, THRUSTER_LENGTH);
		sThrusterCorners = GameMath.getRectCorners(hullCorners[2], orientation, THRUSTER_WIDTH, THRUSTER_LENGTH);
	}
}
