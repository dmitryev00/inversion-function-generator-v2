package org.dmitryev00.math;

import org.dmitryev00.function.models.ModeledNumbers;

import java.util.List;
import java.util.stream.Collectors;

public class MathUtils {
	public static ModeledNumbers normalize(ModeledNumbers numbers)
	{
		List<Double> nums = numbers.getNumbers();
		if (nums == null || nums.isEmpty()) return new ModeledNumbers(nums);
		double sum = nums.stream().mapToDouble(Double::doubleValue).sum();
		List<Double> normalizedNums = nums.stream().map(x -> x/sum).collect(Collectors.toList());
		return new ModeledNumbers(normalizedNums);
	}
}
