package services;

import models.FunctionValues;
import models.Point;

import java.util.ArrayList;
import java.util.List;


public class DifferentiationService {

	public FunctionValues differentiate(FunctionValues cdfValues) {
		List<Point> points = cdfValues.getPoints();
		List<Point> derivativePoints = new ArrayList<>();

		int n = points.size();
		if (n < 2) {
			return new FunctionValues();
		}

		// 1. Первая точка
		Point p0 = points.get(0);
		Point p1 = points.get(1);
		double dx = p1.getX() - p0.getX();
		double df = p1.getY() - p0.getY();
		derivativePoints.add(new Point(p0.getX(), df/dx));

		// 2. Средние точки
		for (int i = 1; i < n - 1; i++) {
			Point prev = points.get(i - 1);
			Point next = points.get(i + 1);

			dx = next.getX() - prev.getX();
			df = next.getY() - prev.getY();

			double derivative = df / dx;
			derivativePoints.add(new Point(points.get(i).getX(), derivative));
		}

		// 3. Последняя точка
		Point pn2 = points.get(n - 2);
		Point pn1 = points.get(n - 1);
		dx = pn1.getX() - pn2.getX();
		df = pn1.getY() - pn2.getY();
		derivativePoints.add(new Point(pn1.getX(), df/dx));

		return new FunctionValues(derivativePoints);
	}
}
