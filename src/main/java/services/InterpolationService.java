package services;

import models.FunctionValues;
import models.Point;

import java.util.List;
import java.util.Optional;

public class InterpolationService {


	public Optional<Double> interpolate(FunctionValues function, double x) {
		List<Point> points = function.getPoints();

		if (points.isEmpty()) return Optional.of(Double.NaN);

		Point first = points.get(0);
		Point last = points.get(points.size() - 1);

		if (x <= first.getX()) return Optional.of(first.getY());
		if (x >= last.getX()) return Optional.of(last.getY());

		for (int i = 0; i < points.size() - 1; i++) {
			Point p1 = points.get(i);
			Point p2 = points.get(i + 1);

			if (x >= p1.getX() && x <= p2.getX()) {
				double t = (x - p1.getX()) / (p2.getX() - p1.getX());
				return Optional.of(p1.getY() + t * (p2.getY() - p1.getY()));
			}
		}

		return Optional.empty();
	}
}
