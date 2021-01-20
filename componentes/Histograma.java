package componentes;

import java.awt.*;
import java.util.Optional;
import java.util.function.*;

import javax.swing.JComponent;

import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.Range;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Clase para visualizar un histograma a partir de los datos
 */
public class Histograma {
  public final JComponent componente;
  private final ChartPanel panel;
  public final BiConsumer<double[], Optional<Double>> establecerValores;

  public Histograma(Color color) {
    componente = panel = new ChartPanel(null, false, false, false, true, true);

    establecerValores = (nuevosValores, max) -> {
      // Crear conjunto de datos
      DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      for (int i = 0; i < nuevosValores.length; i++) {
        dataset.addValue((Number)nuevosValores[i], "Frecuencia", i);
      }

      JFreeChart grafica = ChartFactory.createBarChart(null, null, null, dataset);
      grafica.removeLegend();
      CategoryPlot cp = (CategoryPlot)grafica.getPlot();
      cp.getDomainAxis().setTickMarksVisible(false);
      cp.getDomainAxis().setTickLabelsVisible(false);

      // Establecer maximo si así se desea
      max.<Void>map(maximo -> {
        cp.getRangeAxis().setRange(new Range(0.0, maximo));
        return null;
      });

      cp.setBackgroundPaint(Color.WHITE);
      cp.setRangeGridlinePaint(Color.GRAY);
      cp.getRenderer().setSeriesPaint(0, color);

      panel.setChart(grafica);
      panel.setMouseZoomable(false);
      panel.setRangeZoomable(false);
      panel.setDomainZoomable(false);
    };
  }
}

