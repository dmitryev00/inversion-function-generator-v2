package models;

public class Point implements Comparable<Point>{
	private final double x;
	private final double y;
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public double getX() {
		return x;
	}

	public double getY()
	{
		return y;
	}

	@Override
	public int compareTo(Point other) {
		return Double.compare(this.x, other.x);
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
}
