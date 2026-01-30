package services;

import models.FunctionValues;
import models.GeneratedNumbers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GeneratorService {

	private final FunctionValues inversionFunctionValues;
	private final InterpolationService interpolationService;
	private final Random random;

	public GeneratorService(FunctionValues inversionFunctionValues, InterpolationService interpolationService){
		this.inversionFunctionValues = inversionFunctionValues;
		this.interpolationService = interpolationService;
		this.random = new Random();
	}

	public GeneratedNumbers generateNumbers(int amount)
	{
		List<Double> numbers = new ArrayList<Double>();
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
		return new GeneratedNumbers(numbers);
	}
}
