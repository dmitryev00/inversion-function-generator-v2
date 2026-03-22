package org.dmitryev00.math.statistic;

import org.dmitryev00.function.FunctionService;
import org.dmitryev00.function.models.Function;
import org.dmitryev00.function.models.ModeledNumbers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KolmogorovCriteria implements AnalysisStrategy {

	@Override
	public String getName() {
		return "Критерий Колмогорова";
	}

	@Override
	public double evaluate(ModeledNumbers sample, Function cdf) {
		List<Double> numbers = new ArrayList<>(sample.getNumbers());
		if (numbers.isEmpty()) return 0.0;

		Collections.sort(numbers);
		int n = numbers.size();

		FunctionService cdfService = new FunctionService(cdf);
		double D = 0;

		for (int i = 0; i < n; i++) {
			double x = numbers.get(i);
			double F_emp = (i + 1.0) / n;
			double F_theor = cdfService.getValue(x);
			double diff = Math.abs(F_emp - F_theor);
			if (diff > D) D = diff;
		}

		return D;
	}
}
