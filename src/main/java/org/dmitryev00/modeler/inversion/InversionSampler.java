package org.dmitryev00.modeler.inversion;

import org.dmitryev00.function.models.FunctionValues;
import org.dmitryev00.function.models.ModeledNumbers;
import org.dmitryev00.math.InterpolationService;
import org.dmitryev00.modeler.Sampler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class InversionSampler implements Sampler {

	private final FunctionValues inversionFunctionValues;
	private final InterpolationService interpolationService;
	private final Random random;

	public InversionSampler(FunctionValues inversionFunctionValues, InterpolationService interpolationService){
		this.inversionFunctionValues = inversionFunctionValues;
		this.interpolationService = interpolationService;
		this.random = new Random();
	}

	@Override
	public ModeledNumbers sample(int amount)
	{
		List<Double> numbers = new ArrayList<>();
		for(int i = 0; i < amount; i++)
		{
			double number = random.nextDouble();
			Optional<Double> interpolated = interpolationService.interpolate(inversionFunctionValues, number);
			if (interpolated.isPresent()) {
				numbers.add(interpolated.get());
			} else {
				i--;
			}
		}
		return new ModeledNumbers(numbers);
	}
}
