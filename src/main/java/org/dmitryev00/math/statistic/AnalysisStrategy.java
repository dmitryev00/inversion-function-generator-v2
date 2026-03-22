package org.dmitryev00.math.statistic;

import org.dmitryev00.function.models.Function;
import org.dmitryev00.function.models.ModeledNumbers;

public interface AnalysisStrategy {
	String getName();
	double evaluate(ModeledNumbers sample, Function cdf);
}