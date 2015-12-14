import java.util.Random;

public class Spark {
	public static final double FRICTION = .99;
	private static final double POSITION_NOISE = 10;
	private static final double MAX_VELOCITY = 1;
	private static final int MAX_LIFESPAN = 200;
	
	public RealPoint position;
	public RealPoint velocity;
	public int lifespan;
	
	public Spark(RealPoint position) {
		Random rand = new Random();
		
		this.position = new RealPoint((rand.nextDouble()-0.5) * POSITION_NOISE, (rand.nextDouble()-0.5) * POSITION_NOISE).add(position);
		this.velocity = new RealPoint((rand.nextDouble()-0.5) * MAX_VELOCITY * 2, (rand.nextDouble()-0.5) * MAX_VELOCITY * 2);
		this.lifespan = MAX_LIFESPAN/3 + rand.nextInt(MAX_LIFESPAN*2/3);
	}
	
	public void update() {
		lifespan--;
		velocity.multiply(FRICTION);
		position.add(velocity);
	}
}
