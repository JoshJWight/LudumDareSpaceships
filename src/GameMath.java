import java.awt.Point;

public class GameMath {
	public static RealPoint cartesianToPolar(double x, double y) {
		double r = Math.sqrt(x*x + y*y);
		double theta = Math.atan2(y, x); //sweet, didn't even expect Math to have this
		return new RealPoint(r, theta);
	}
	
	public static RealPoint polarToCartesian(double r, double theta) {
		double x = r * Math.cos(theta);
		double y = r * Math.sin(theta);
		return new RealPoint(x, y);
	}
	
	public static RealPoint[] getRectCorners(RealPoint center, double orientation, double width, double length) {
		RealPoint points[] = new RealPoint[4];
		
		//the polar coordinates of the front port corner relative to the center
		RealPoint offsetPolar = cartesianToPolar(length/2, width/2);
		
		//calculate offsets
		RealPoint fpo = polarToCartesian(offsetPolar.x, orientation - offsetPolar.y);
		RealPoint fso = polarToCartesian(offsetPolar.x, orientation + offsetPolar.y);
		RealPoint rso = polarToCartesian(offsetPolar.x, orientation + Math.PI - offsetPolar.y);
		RealPoint rpo = polarToCartesian(offsetPolar.x, orientation + Math.PI + offsetPolar.y);
				
		//add offsets to center to get points
		points[0] = new RealPoint(center.x+fpo.x, center.y+fpo.y);
		points[1] = new RealPoint(center.x+fso.x, center.y+fso.y);
		points[2] = new RealPoint(center.x+rso.x, center.y+rso.y);
		points[3] = new RealPoint(center.x+rpo.x, center.y+rpo.y);
		
		return points;
	}
	
	public static boolean rectContainsPoint(RealPoint rect[], RealPoint p) {
		//there has to be a better way to do this
		
		double angle1 = getAngle(p, rect[0], rect[1]);
		double angle2 = getAngle(p, rect[1], rect[2]);
		double angle3 = getAngle(p, rect[2], rect[3]);
		double angle4 = getAngle(p, rect[3], rect[0]);
		
		return (angle1<Math.PI/2 && angle2<Math.PI/2 && angle3<Math.PI/2 && angle4<Math.PI/2);
	}
	
	public static boolean rectsIntersect(RealPoint rect1[], RealPoint rect2[]) {
		for(int i=0; i<4; i++) {
			if(rectContainsPoint(rect1, rect2[i]) || rectContainsPoint(rect2, rect1[i])) {
				return true;
			}
		}
		return false;
	}
	
	
	//Buncha linear algebra junk ripped from a Project Euler problem
	
	public static double dotProduct(RealPoint u, RealPoint v) {
		return u.x*v.x + u.y*v.y;
	}
	/**
	 * maginitude of 2d vector
	 * @param v
	 * @return
	 */
	public static double magnitude(RealPoint v) {
		return Math.sqrt(v.x*v.x + v.y*v.y);
	}
	
	/**
	 * Get angle between 2d vectors
	 * @param u
	 * @param v
	 * @return
	 */
	public static double getAngle(RealPoint u, RealPoint v) {
		return Math.acos(dotProduct(u, v)/(magnitude(u)*magnitude(v)));
	}
	
	/**
	 * Get angle formed by 2d points
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static double getAngle(RealPoint a, RealPoint b, RealPoint c) {	
		RealPoint u = new RealPoint(a.x-b.x, a.y-b.y);
		RealPoint v = new RealPoint(c.x-b.x, c.y-b.y);
		return getAngle(u, v);
	}
}
