package models;

public class Function {

	private String expression;
	private double start;
	private double end;
	private double step;

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
