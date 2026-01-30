package ui;

import controller.AppController;
import models.FunctionValues;
import models.GeneratedNumbers;
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
import java.awt.*;
import java.util.List;

public class App {
	private JFrame mainFrame;
	private AppController controller;

	// Поля ввода
	private JTextField cdfField;
	private JTextField pdfField;
	private JTextField inverseField;
	private JTextField fromField;
	private JTextField toField;
	private JTextField stepField;
	private JTextField amountField;

	// График
	private ChartPanel chartPanel;

	// Статус
	private boolean distributionBuilt = false;

	public App() {
		controller = new AppController();
		initUI();
	}

	private void initUI() {
		mainFrame = new JFrame("Inversion Method Generator");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new BorderLayout(10, 10));

		// Панель ввода
		mainFrame.add(createInputPanel(), BorderLayout.NORTH);

		// График
		chartPanel = new ChartPanel(null);
		chartPanel.setPreferredSize(new Dimension(800, 500));
		chartPanel.setBackground(Color.WHITE);
		mainFrame.add(chartPanel, BorderLayout.CENTER);

		// Статус бар
		mainFrame.add(createStatusBar(), BorderLayout.SOUTH);

		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}

	private JPanel createInputPanel() {
		JPanel panel = new JPanel(new GridLayout(4, 4, 5, 5));
		panel.setBorder(BorderFactory.createTitledBorder("Параметры распределения"));

		// CDF
		panel.add(new JLabel("CDF (F(x)):"));
		cdfField = new JTextField("x");
		panel.add(cdfField);

		panel.add(new JLabel("min:"));
		fromField = new JTextField("0");
		panel.add(fromField);

		// PDF
		panel.add(new JLabel("PDF (f(x)):"));
		pdfField = new JTextField("");
		pdfField.setToolTipText("Оставьте пустым для автоматического расчета");
		panel.add(pdfField);

		panel.add(new JLabel("max:"));
		toField = new JTextField("1");
		panel.add(toField);

		// Inverse
		panel.add(new JLabel("Inverse (F⁻¹(x)):"));
		inverseField = new JTextField("");
		inverseField.setToolTipText("Оставьте пустым для автоматического расчета");
		panel.add(inverseField);

		panel.add(new JLabel("шаг:"));
		stepField = new JTextField("0.01");
		panel.add(stepField);

		// Кнопки
		JButton buildButton = new JButton("Построить распределение");
		buildButton.addActionListener(e -> buildDistribution());
		panel.add(buildButton);

		panel.add(new JLabel("Количество:"));
		amountField = new JTextField("1000");
		panel.add(amountField);

		JButton generateButton = new JButton("Сгенерировать выборку");
		generateButton.addActionListener(e -> generateSample());
		panel.add(generateButton);

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

		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(mainFrame,
					"Ошибка в числовых полях: " + e.getMessage(),
					"Ошибка",
					JOptionPane.ERROR_MESSAGE);
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(mainFrame,
					e.getMessage(),
					"Ошибка",
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(mainFrame,
					"Неожиданная ошибка: " + e.getMessage(),
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
			if (amount <= 0) {
				throw new IllegalArgumentException("Количество должно быть > 0");
			}

			GeneratedNumbers numbers = controller.generateSample(amount);
			plotHistogram(numbers);

			JOptionPane.showMessageDialog(mainFrame,
					"Сгенерировано " + amount + " чисел\n" +
							"Минимум: " + String.format("%.3f", numbers.getMin()) + "\n" +
							"Максимум: " + String.format("%.3f", numbers.getMax()) + "\n" +
							"Среднее: " + String.format("%.3f", numbers.getMean()),
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

	private void plotHistogram(GeneratedNumbers numbers) {
		if (numbers == null || numbers.isEmpty()) {
			return;
		}

		List<Double> values = numbers.getNumbers();
		double[] data = values.stream().mapToDouble(Double::doubleValue).toArray();

		// Гистограмма
		HistogramDataset histogramDataset = new HistogramDataset();
		//histogramDataset.setType(HistogramType.SCALE_AREA_TO_1);

		int bins = Math.min(50, values.size() / 10);
		bins = Math.max(10, bins);

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

		// Настройка гистограммы
		XYBarRenderer barRenderer = (XYBarRenderer) plot.getRenderer();
		barRenderer.setSeriesPaint(0, new Color(70, 130, 180, 150));
		barRenderer.setDrawBarOutline(true);
		barRenderer.setSeriesOutlinePaint(0, Color.BLUE);
		barRenderer.setMargin(0.1);

		// Добавление теоретической PDF если есть
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

		// Настройка внешнего вида
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

		chartPanel.setChart(chart);
		chartPanel.repaint();
	}

	private XYSeries createPDFSeries(FunctionValues pdfValues, List<Double> sample) {
		XYSeries series = new XYSeries("Теоретическая PDF");

		// Определяем диапазон по выборке
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