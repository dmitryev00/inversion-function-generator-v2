package org.dmitryev00.function;

import org.dmitryev00.function.models.FunctionValues;
import org.dmitryev00.function.models.Point;

public class FunctionUtils {

	public static boolean isMonotone(FunctionValues values) {
		double epsilon = 1e-12;
		boolean hasPositive = false;
		boolean hasNegative = false;

		for (Point p : values.getPoints()) {
			if (p.getY() > epsilon) hasPositive = true;
			if (p.getY() < -epsilon) hasNegative = true;
			if (hasPositive && hasNegative) return false;
		}

		return true;
	}
}
