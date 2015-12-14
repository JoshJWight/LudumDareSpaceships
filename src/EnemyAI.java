
public class EnemyAI {
	private GameModel model;
	public Fleet fleet;
	public boolean running;
	
	public double INNER_RANGE = 300;
	public double OUTER_RANGE = 500;
	public double AIM_WINDOW = Math.PI/64;
	public double MAX_SPEED = 1;
	public double MAX_ANGULAR_SPEED = 0.005;
	
	public EnemyAI(GameModel model, Fleet fleet) {
		this.model = model;
		this.fleet = fleet;
	}
	
	public void update() {
		RealPoint heroPos = model.heroFleet.center;
		RealPoint myPos = fleet.center;
		double orientation = fleet.getEnabledThrusterOrientation();
		double speed = GameMath.magnitude(fleet.velocity);
		
		RealPoint offset = new RealPoint(heroPos.x - myPos.x, heroPos.y - myPos.y);
		double magnitude = GameMath.magnitude(offset);
		double offsetOri = GameMath.cartesianToPolar(offset.x, offset.y).y;
		
		double odiff = GameMath.orientationDiff(orientation, offsetOri);
		
		boolean p = false;
		boolean s = false;
		
		if(magnitude>OUTER_RANGE && speed<MAX_SPEED) {
			running = false;
			//if the fleet is too far from the hero, turn to face him or move towards him if facing him
			if(odiff<AIM_WINDOW || odiff>(Math.PI*2 - AIM_WINDOW)) {
				p = s = true;
			} else if(odiff<Math.PI) {
				s = true;
			} else {
				p = true;
			}
		} else if(magnitude<INNER_RANGE) {
			running = true;
			//if the fleet is too close to the hero, turn away and fly away.
			if(odiff<Math.PI/2) {
				p = true;
			} else if(odiff>Math.PI*3/2) {
				s = true;
			} else {
				p = s = true;
			}
			
		} else if(speed<MAX_SPEED){
			if (running){
				p = s = true;
			} else if(odiff<Math.PI && odiff>AIM_WINDOW && fleet.angularSpeed>MAX_ANGULAR_SPEED*-1) {
				s = true;
			} else if(odiff>Math.PI && odiff<(Math.PI*2 - AIM_WINDOW) && fleet.angularSpeed<MAX_ANGULAR_SPEED) {
				p = true;
			}
		}
		
		fleet.togglePThruster(p);
		fleet.toggleSThruster(s);
	}
}
