package services;

import models.Function;
import models.FunctionValues;
import models.Point;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.List;

public class FunctionService {

	Function function;

	public FunctionService(Function function) {
		this.function = function;
	}

	public double getValue(double x)
	{
		try {
			Expression e = new ExpressionBuilder(function.getExpression())
					.variable("x")
					.build();
			return e.setVariable("x", x).evaluate();
		} catch (ArithmeticException e) {
			// Ловим деление на ноль
			System.err.println("Ошибка вычисления: " + e.getMessage());
			System.err.println("Функция: " + function + ", x=" + x);

			return Double.NaN;
		}
	}

	public FunctionValues getValues()
	{
		List<Point> points = new ArrayList<>();
		double step = function.getStep();
		for(double x = function.getStart(); x <= function.getEnd(); x += step)
		{
			double y =  getValue(x);
			points.add(new Point(x, y));
		}
		return new FunctionValues(points);
	}
}
