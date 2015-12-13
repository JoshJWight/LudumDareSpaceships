
public class Laser {
	public static final double SPEED_FACTOR = 2;
	public static final double SPAWN_SPACE = 5;
	public static final double MAX_AGE = 10000;
	
	public double orientation;
	public RealPoint position;
	public int age;
	
	public Laser(RealPoint position, double orientation) {
		this.position = position;
		this.orientation = orientation;
		this.age=0;
	}
	
	public Laser(Ship s) {
		this(GameMath.polarToCartesian(Ship.SHIP_LENGTH/2 + SPAWN_SPACE, s.orientation).add(s.position), s.orientation);
	}
	
	public void update() {
		age++;
		RealPoint delta = GameMath.polarToCartesian(SPEED_FACTOR, orientation);
		position.add(delta);
	}
}
