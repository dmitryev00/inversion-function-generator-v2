package org.dmitryev00.math.statistic;

import org.dmitryev00.function.FunctionService;
import org.dmitryev00.function.models.Function;
import org.dmitryev00.function.models.ModeledNumbers;

public class ChiSquare implements AnalysisStrategy{

	private final int bins;

	public ChiSquare(int bins) {
		this.bins = bins;
	}

	@Override
	public String getName() {
		return "Хи-квадрат";
	}

	@Override
	public double evaluate(ModeledNumbers sample, Function cdf) {
		double step = (sample.getMax() - sample.getMin()) / bins;
		FunctionService cdfService = new FunctionService(cdf);

		int[] observed = new int[bins];

		for (double x : sample.getNumbers()) {
			int bin = (int)((x - sample.getMin()) / step);
			if (bin >= bins) bin = bins - 1; // крайний интервал
			observed[bin]++;
		}

		double chi2 = 0;
		for (int i = 0; i < bins; i++) {
			double a = sample.getMin() + i * step;
			double b = a + step;

			double Fa = cdfService.getValue(a);
			double Fb = cdfService.getValue(b);

			double Ei = sample.getNumbers().size() * (Fb - Fa);

			double diff = observed[i] - Ei;
			chi2 += diff * diff / Ei;
		}
		return chi2 / bins;
	}
}
