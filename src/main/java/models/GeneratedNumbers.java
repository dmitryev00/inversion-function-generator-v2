package models;

import java.util.List;

public class GeneratedNumbers {

	List<Double> numbers;

	public GeneratedNumbers(List<Double> numbers) {
		this.numbers = numbers;
	}

	public List<Double> getNumbers() {
		return numbers;
	}

	public double getMean() {
		return numbers.stream().mapToDouble(Double::doubleValue).average().orElse(0);
	}

	public double getVariance() {
		double mean = getMean();
		return numbers.stream()
				.mapToDouble(x -> Math.pow(x - mean, 2))
				.average().orElse(0);
	}

	public double getStdDev() {
		return Math.sqrt(getVariance());
	}

	public double getMin() {
		return numbers.stream().min(Double::compare).orElse(Double.NaN);
	}

	public double getMax() {
		return numbers.stream().max(Double::compare).orElse(Double.NaN);
	}

	public boolean isEmpty() {
		return numbers == null || numbers.isEmpty();
	}
}