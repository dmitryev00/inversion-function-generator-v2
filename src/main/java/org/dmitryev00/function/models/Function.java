package org.dmitryev00.function.models;

public class Function {

	private final String expression;
	private final double start;
	private final double end;
	private final double step;

	public Function(String expression, double start, double end, double step) {
		this.expression = expression;
		this.start = start;
		this.end = end;
		this.step = step;
	}

	public String getExpression() {
		return expression;
	}

	public double getStart() {
		return start;
	}

	public double getEnd() {
		return end;
	}

	public double getStep() {
		return step;
	}
}
