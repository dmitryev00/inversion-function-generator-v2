package controller;

import models.Function;
import models.FunctionValues;
import models.GeneratedNumbers;
import services.*;

public class AppController {

	private FunctionValues currentCDFValues;
	private FunctionValues currentPDFValues;
	private FunctionValues currentInverseValues;

	private final DifferentiationService diffService;
	private final InversionService invService;

	public AppController() {
		this.diffService = new DifferentiationService();
		this.invService = new InversionService();
	}


	public void buildDistribution(String cdfExp, String pdfExp, String inverseExp, double from, double to, double step)
	{
		if (cdfExp == null || cdfExp.trim().isEmpty()) {
			throw new IllegalArgumentException("CDF обязательна!");
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

		currentInverseValues = invService.invert(currentCDFValues);
	}

	public GeneratedNumbers generateSample(int amount) {
		if (currentInverseValues == null) {
			throw new IllegalStateException("Сначала постройте распределение!");
		}

		InterpolationService interpolator = new InterpolationService();
		GeneratorService generator = new GeneratorService(currentInverseValues, interpolator);
		return generator.generateNumbers(amount);
	}

	public FunctionValues getCDFValues() { return currentCDFValues; }
	public FunctionValues getPDFValues() { return currentPDFValues; }
	public FunctionValues getInverseValues() { return currentInverseValues; }
}
