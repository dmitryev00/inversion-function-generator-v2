package services;

import models.FunctionValues;
import models.Point;

import java.util.ArrayList;
import java.util.List;

public class InversionService {


	public FunctionValues invert(FunctionValues cdfValues) {
		List<Point> points = cdfValues.getPoints();
		if (points.isEmpty()) {
			return new FunctionValues();
		}

		// 1. Нормализуем CDF к [0,1]
		double minF = points.get(0).getY();
		double maxF = points.get(points.size() - 1).getY();
		double rangeF = maxF - minF;

		if (Math.abs(rangeF) < 1e-10) {
			throw new IllegalArgumentException("CDF не изменяется");
		}

		// Нормализованные значения CDF
		List<Point> normalizedPoints = new ArrayList<>();
		for (Point p : points) {
			double normalizedY = (p.getY() - minF) / rangeF;
			normalizedPoints.add(new Point(p.getX(), normalizedY));
		}
		FunctionValues normalizedCDF = new FunctionValues(normalizedPoints);

		// 2. Создаем равномерные y от 0 до 1
		int numPoints = 1000; // сколько точек в обратной функции
		List<Point> inversePoints = new ArrayList<>();

		for (int i = 0; i <= numPoints; i++) {
			double y = i / (double) numPoints; // y ∈ [0,1]

			// 3. Находим x: F(x) = y
			double x = findXForY(y, normalizedCDF);
			inversePoints.add(new Point(y, x)); // Обратная: (y, x)
		}

		return new FunctionValues(inversePoints);
	}

	private double findXForY(double y, FunctionValues normalizedCDF) {
		// Ищем x такой, что F(x) = y
		// Используем бинарный поиск + интерполяцию

		List<Point> points = normalizedCDF.getPoints();

		// Граничные случаи
		if (y <= points.get(0).getY()) return points.get(0).getX();
		if (y >= points.get(points.size() - 1).getY()) return points.get(points.size() - 1).getX();

		// Находим интервал, где F переходит через y
		int left = 0;
		int right = points.size() - 1;

		while (left <= right) {
			int mid = (left + right) / 2;
			double midY = points.get(mid).getY();

			if (Math.abs(midY - y) < 1e-10) {
				return points.get(mid).getX(); // Точное совпадение
			}

			if (midY < y) {
				left = mid + 1;
			} else {
				right = mid - 1;
			}
		}

		// Теперь left указывает на точку с F > y
		// Интерполируем между points.get(left-1) и points.get(left)
		Point p1 = points.get(left - 1);
		Point p2 = points.get(left);

		double t = (y - p1.getY()) / (p2.getY() - p1.getY());
		return p1.getX() + t * (p2.getX() - p1.getX());
	}
}