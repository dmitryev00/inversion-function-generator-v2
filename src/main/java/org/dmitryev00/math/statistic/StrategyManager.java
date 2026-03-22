package org.dmitryev00.math.statistic;

import org.dmitryev00.function.models.Function;
import org.dmitryev00.function.models.ModeledNumbers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StrategyManager {

	private final List<AnalysisStrategy> strategies = new ArrayList<>();
	private final Map<String, AnalysisStrategy> nameMap = new HashMap<>();

	public void addStrategy(AnalysisStrategy strategy) {
		strategies.add(strategy);
		nameMap.put(strategy.getName(), strategy);
	}

	public double evaluate(String strategyName, ModeledNumbers sample, Function cdf) {
		AnalysisStrategy strategy = nameMap.get(strategyName);
		if (strategy == null) {
			throw new IllegalArgumentException("Стратегия '" + strategyName + "' не найдена");
		}
		return strategy.evaluate(sample, cdf);
	}

	public Map<String, Double> evaluateAll(ModeledNumbers sample, Function cdf) {
		Map<String, Double> results = new HashMap<>();
		for (AnalysisStrategy strategy : strategies) {
			double result = strategy.evaluate(sample, cdf);
			results.put(strategy.getName(), result);
		}
		return results;
	}

	public List<String> getAvailableStrategies() {
		return new ArrayList<>(nameMap.keySet());
	}
}