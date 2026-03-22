package org.dmitryev00.ui;

import org.dmitryev00.controller.Controller;
import org.dmitryev00.function.models.FunctionValues;
import org.dmitryev00.function.models.ModeledNumbers;
import org.dmitryev00.function.models.Function;
import org.dmitryev00.math.statistic.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Map;

public class App {
	private JFrame mainFrame;
	private Controller controller;

	private JTextField cdfField;
	private JTextField pdfField;
	private JTextField inverseField;
	private JTextField fromField;
	private JTextField toField;
	private JTextField stepField;
	private JTextField amountField;
	private JTextField binsField;

	private JComboBox<String> strategySpinner;
	private JButton analyzeButton;

	private ChartPanel chartPanel;
	private JTable dataTable;
	private DefaultTableModel tableModel;

	private boolean distributionBuilt = false;
	private ModeledNumbers lastSample;

	private StrategyManager strategyManager;

	public App() {
		controller = new Controller();
		strategyManager = new StrategyManager();
		strategyManager.addStrategy(new ChiSquare(10));
		strategyManager.addStrategy(new KolmogorovCriteria());
		initUI();
	}

	private void initUI() {
		mainFrame = new JFrame("Inversion Method Generator");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new BorderLayout(10, 10));

		mainFrame.add(createInputPanel(), BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new BorderLayout());
		chartPanel = new ChartPanel(null);
		chartPanel.setPreferredSize(new Dimension(800, 400));
		chartPanel.setBackground(Color.WHITE);
		centerPanel.add(chartPanel, BorderLayout.CENTER);

		// Добавляем DataGrid под графиком
		tableModel = new DefaultTableModel(new Object[]{"Индекс", "Значение"}, 0);
		dataTable = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(dataTable);
		scrollPane.setPreferredSize(new Dimension(800, 150));
		centerPanel.add(scrollPane, BorderLayout.SOUTH);

		mainFrame.add(centerPanel, BorderLayout.CENTER);
		mainFrame.add(createStatusBar(), BorderLayout.SOUTH);

		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}

	private JPanel createInputPanel() {
		JPanel panel = new JPanel(new GridLayout(6, 4, 5, 5));
		panel.setBorder(BorderFactory.createTitledBorder("Параметры распределения"));

		panel.add(new JLabel("CDF (F(x)):"));
		cdfField = new JTextField("x");
		panel.add(cdfField);

		panel.add(new JLabel("min:"));
		fromField = new JTextField("0");
		panel.add(fromField);

		panel.add(new JLabel("PDF (f(x)):"));
		pdfField = new JTextField("");
		pdfField.setToolTipText("Оставьте пустым для автоматического расчета");
		panel.add(pdfField);

		panel.add(new JLabel("max:"));
		toField = new JTextField("1");
		panel.add(toField);

		panel.add(new JLabel("Inverse (F⁻¹(x)):"));
		inverseField = new JTextField("");
		inverseField.setToolTipText("Оставьте пустым для автоматического расчета");
		panel.add(inverseField);

		panel.add(new JLabel("шаг:"));
		stepField = new JTextField("0.01");
		panel.add(stepField);

		panel.add(new JLabel("Интервалы"));
		binsField = new JTextField("");
		panel.add(binsField);

		panel.add(new JLabel(""));
		JButton buildButton = new JButton("Построить распределение");
		buildButton.addActionListener(e -> buildDistribution());
		panel.add(buildButton);

		panel.add(new JLabel("Количество:"));
		amountField = new JTextField("1000");
		panel.add(amountField);

		panel.add(new JLabel(""));
		JButton generateButton = new JButton("Сгенерировать выборку");
		generateButton.addActionListener(e -> generateSample());
		panel.add(generateButton);

		panel.add(new JLabel("Стратегия анализа:"));
		strategySpinner = new JComboBox<>(new String[]{"Хи-квадрат", "Критерий Колмогорова", "Все"});
		panel.add(strategySpinner);

		analyzeButton = new JButton("Проанализировать модель");
		analyzeButton.addActionListener(e -> analyzeSample());
		panel.add(analyzeButton);

		return panel;
	}

	private JPanel createStatusBar() {
		JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		statusBar.setBorder(BorderFactory.createEtchedBorder());
		statusBar.add(new JLabel("Готово"));
		return statusBar;
	}

	private void buildDistribution() {
		try {
			String cdf = cdfField.getText().trim();
			String pdf = pdfField.getText().trim();
			String inverse = inverseField.getText().trim();

			double from = Double.parseDouble(fromField.getText());
			double to = Double.parseDouble(toField.getText());
			double step = Double.parseDouble(stepField.getText());

			controller.buildDistribution(cdf, pdf, inverse, from, to, step);
			distributionBuilt = true;

			JOptionPane.showMessageDialog(mainFrame,
					"Распределение успешно построено!\n" +
							"CDF точек: " + controller.getCDFValues().size() + "\n" +
							"PDF точек: " + controller.getPDFValues().size() + "\n" +
							"Inverse точек: " + controller.getInverseValues().size(),
					"Успех",
					JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(mainFrame,
					"Ошибка построения: " + e.getMessage(),
					"Ошибка",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void generateSample() {
		if (!distributionBuilt) {
			JOptionPane.showMessageDialog(mainFrame,
					"Сначала постройте распределение!",
					"Ошибка",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			int amount = Integer.parseInt(amountField.getText());
			lastSample = controller.generateSample(amount);
			plotHistogram(lastSample);
			fillDataTable(lastSample);

			JOptionPane.showMessageDialog(mainFrame,
					"Сгенерировано " + amount + " чисел\n" +
							"Минимум: " + String.format("%.3f", lastSample.getMin()) + "\n" +
							"Максимум: " + String.format("%.3f", lastSample.getMax()) + "\n" +
							"Среднее: " + String.format("%.3f", lastSample.getMean()),
					"Результат",
					JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(mainFrame,
					"Ошибка генерации: " + e.getMessage(),
					"Ошибка",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void fillDataTable(ModeledNumbers sample) {
		tableModel.setRowCount(0);
		List<Double> numbers = sample.getNumbers();
		for (int i = 0; i < numbers.size(); i++) {
			tableModel.addRow(new Object[]{i + 1, numbers.get(i)});
		}
	}

	private void analyzeSample() {
		if (lastSample == null || lastSample.isEmpty()) {
			JOptionPane.showMessageDialog(mainFrame,
					"Сначала сгенерируйте выборку!",
					"Ошибка",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		String selectedStrategy = (String) strategySpinner.getSelectedItem();
		Function cdfFunction = new Function(cdfField.getText().trim(),
				Double.parseDouble(fromField.getText()),
				Double.parseDouble(toField.getText()),
				Double.parseDouble(stepField.getText()));

		StringBuilder result = new StringBuilder();
		if ("Все".equals(selectedStrategy)) {
			Map<String, Double> results = strategyManager.evaluateAll(lastSample, cdfFunction);
			for (String name : results.keySet()) {
				result.append(name).append(": ").append(results.get(name)).append("\n");
			}
		} else {
			double value = strategyManager.evaluate(selectedStrategy, lastSample, cdfFunction);
			result.append(selectedStrategy).append(": ").append(value);
		}

		JOptionPane.showMessageDialog(mainFrame,
				result.toString(),
				"Результаты анализа",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void plotHistogram(ModeledNumbers numbers) {
		if (numbers == null || numbers.isEmpty()) return;

		List<Double> values = numbers.getNumbers();
		double[] data = values.stream().mapToDouble(Double::doubleValue).toArray();

		HistogramDataset histogramDataset = new HistogramDataset();
		histogramDataset.setType(HistogramType.SCALE_AREA_TO_1);

		int bins = Objects.equals(binsField.getText(), "") ? Math.min(50, values.size() / 10)
				: Integer.parseInt(binsField.getText());

		histogramDataset.addSeries("Выборка", data, bins);

		JFreeChart chart = ChartFactory.createHistogram(
				"Гистограмма выборки и теоретическая плотность",
				"x",
				"Плотность вероятности",
				histogramDataset,
				PlotOrientation.VERTICAL,
				true,
				true,
				false
		);

		XYPlot plot = chart.getXYPlot();

		XYBarRenderer barRenderer = (XYBarRenderer) plot.getRenderer();
		barRenderer.setSeriesPaint(0, new Color(28, 133, 218, 150));
		barRenderer.setDrawBarOutline(true);
		barRenderer.setSeriesOutlinePaint(0, Color.BLUE);
		barRenderer.setMargin(0.1);

		FunctionValues pdfValues = controller.getPDFValues();
		if (pdfValues != null && !pdfValues.isEmpty()) {
			XYSeries pdfSeries = createPDFSeries(pdfValues, values);
			XYSeriesCollection pdfDataset = new XYSeriesCollection();
			pdfDataset.addSeries(pdfSeries);
			plot.setDataset(1, pdfDataset);

			XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer();
			lineRenderer.setSeriesPaint(0, Color.RED);
			lineRenderer.setSeriesStroke(0, new BasicStroke(2.0f));
			lineRenderer.setSeriesShapesVisible(0, false);
			plot.setRenderer(1, lineRenderer);
		}

		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

		chartPanel.setChart(chart);
		chartPanel.repaint();
	}

	private XYSeries createPDFSeries(FunctionValues pdfValues, List<Double> sample) {
		XYSeries series = new XYSeries("Теоретическая PDF");

		double minX = sample.stream().min(Double::compare).orElse(0.0);
		double maxX = sample.stream().max(Double::compare).orElse(1.0);
		double margin = (maxX - minX) * 0.1;
		minX -= margin;
		maxX += margin;

		List<Double> xValues = pdfValues.getXPoints();
		List<Double> yValues = pdfValues.getYPoints();

		for (int i = 0; i < xValues.size(); i++) {
			double x = xValues.get(i);
			if (x >= minX && x <= maxX) {
				series.add(x, yValues.get(i));
			}
		}
		return series;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				new App();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Ошибка запуска приложения: " + e.getMessage(),
						"Ошибка",
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}