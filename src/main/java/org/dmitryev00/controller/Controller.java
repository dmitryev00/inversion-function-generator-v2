package org.dmitryev00.controller;

import org.dmitryev00.function.models.Function;
import org.dmitryev00.function.models.FunctionValues;
import org.dmitryev00.function.models.ModeledNumbers;
import org.dmitryev00.function.FunctionService;
import org.dmitryev00.function.FunctionUtils;
import org.dmitryev00.math.DifferentiationService;
import org.dmitryev00.math.InterpolationService;
import org.dmitryev00.modeler.inversion.InversionSampler;
import org.dmitryev00.math.InversionService;

public class Controller {

	private FunctionValues currentCDFValues;
	private FunctionValues currentPDFValues;
	private FunctionValues currentInverseValues;

	private final DifferentiationService diffService;
	private final InversionService invService;

	public Controller() {
		this.diffService = new DifferentiationService();
		this.invService = new InversionService();
	}


	public void buildDistribution(String cdfExp, String pdfExp, String inverseExp, double from, double to, double step)
	{
		if (cdfExp == null || cdfExp.trim().isEmpty()) {
			throw new IllegalArgumentException("CDF обязательна!");
		}

		Function cdfFunction = new Function(cdfExp, from, to, step);
		FunctionValues cdfValues = new FunctionService(cdfFunction).getValues();

		if (!FunctionUtils.isMonotone(cdfValues)) {
			throw new IllegalArgumentException("CDF должна быть монотонной!");
		}

		boolean pdfEmpty = pdfExp == null || pdfExp.trim().isEmpty();
		boolean invEmpty = inverseExp == null || inverseExp.trim().isEmpty();

		if (pdfEmpty && invEmpty) {
			buildAuto(cdfExp, from, to, step);
		}
		else if (!pdfEmpty && !invEmpty) {
			buildManual(cdfExp, pdfExp, inverseExp, from, to, step);
		}
		else {
			throw new IllegalArgumentException(
					"Введите либо только CDF, либо все три функции!");
		}
	}

	private void buildManual(String cdfExp, String pdfExp, String inverseExp, double from, double to, double step)
	{
		Function cdfFunction = new Function(cdfExp, from, to, step);
		FunctionService cdfService = new FunctionService(cdfFunction);
		currentCDFValues = cdfService.getValues();

		Function pdfFunction = new Function(pdfExp, from, to, step);
		FunctionService pdfService = new FunctionService(pdfFunction);
		currentPDFValues = pdfService.getValues();
		if(!FunctionUtils.isMonotone(currentPDFValues)) {
			throw new IllegalArgumentException("CDF должна быть монотонной!");
		}

		Function inverseFunction = new Function(inverseExp, from, to, step);
		FunctionService inverseService = new FunctionService(inverseFunction);
		currentInverseValues = inverseService.getValues();
	}


	public void buildAuto(String cdfExp, double from, double to, double step)
	{
		Function cdfFunction = new Function(cdfExp, from, to, step);
		FunctionService cdfService = new FunctionService(cdfFunction);
		currentCDFValues = cdfService.getValues();


		currentPDFValues = diffService.differentiate(currentCDFValues);
		if(!FunctionUtils.isMonotone(currentPDFValues)) {
			throw new IllegalArgumentException("CDF должна быть монотонной!");
		}

		currentInverseValues = invService.invert(currentCDFValues);
	}

	public ModeledNumbers generateSample(int amount) {
		if (currentInverseValues == null) {
			throw new IllegalStateException("Сначала постройте распределение!");
		}

		InterpolationService interpolator = new InterpolationService();
		InversionSampler generator = new InversionSampler(currentInverseValues, interpolator);
		return generator.sample(amount);
	}

	public FunctionValues getCDFValues() { return currentCDFValues; }
	public FunctionValues getPDFValues() { return currentPDFValues; }
	public FunctionValues getInverseValues() { return currentInverseValues; }
}
