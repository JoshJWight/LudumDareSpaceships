import java.awt.Point;

public class RealPoint {
	public double x;
	public double y;
	public RealPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point toPoint() {
		return new Point((int)x, (int)y);
	}
	
	public RealPoint add(RealPoint p) {
		x += p.x;
		y += p.y;
		return this;
	}
	
	public RealPoint multiply(double n) {
		x = x*n;
		y = y*n;
		return this;
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
