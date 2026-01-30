package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunctionValues {

	private List<Point> points;

	public FunctionValues() {
		this.points = new ArrayList<>();
	}

	public FunctionValues(List<Point> points) {
		this.points = new ArrayList<>(points);
		Collections.sort(this.points);
	}

	public void addPoint(Point point)
	{
		points.add(point);
	}

	public void addPoint(double x, double y)
	{
		points.add(new Point(x, y));
	}

	public List<Double> getXPoints()
	{
		List<Double> values = new ArrayList<>();
		for (Point point : points)
		{
			values.add(point.getX());
		}
		return values;
	}

	public List<Double> getYPoints()
	{
		List<Double> values = new ArrayList<>();
		for (Point point : points)
		{
			values.add(point.getY());
		}
		return values;
	}

	public int size()
	{
		return points.size();
	}

	public boolean isEmpty()
	{
		return points.isEmpty();
	}

	public List<Point> getPoints() {
		return Collections.unmodifiableList(points);
	}
}
