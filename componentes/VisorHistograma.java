package componentes;

import imagenes.InfoImagen;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import java.util.*;
import java.util.function.*;

/**
 * Clase para mostrar los diferentes histogramas de una imagen dependiendo si es RGB o
 * en escala de grises
 */
public class VisorHistograma {
  public final Consumer<BufferedImage> establecerImagen;
  public final JComponent componente;

  private double[][] histograma = null;
  private Supplier<Boolean> esRGB = () -> histograma != null && histograma.length == 3;
  private Double maxHistograma;

  private boolean maximosIgualados = true;
  String etiquetasBoton[] = { "Igualar máximos", "Graficar individualmente" };
  private Supplier<Integer> maximosIgualadosInt = () -> maximosIgualados ? 1 : 0;

  public VisorHistograma(Dimension medidaGrafica) {
    PanelVertical contenido = new PanelVertical(false);

    JLabel titulo = new JLabel("Histogramas de la imagen");
    titulo.setVisible(false);
    contenido.agregarComponente.accept(titulo);

    // Crear los histogramas para todos los canales
    Histograma histogramas[] = new Histograma[4];
    {
      Color colores[] = { Color.RED, Color.GREEN, Color.BLUE, Color.GRAY };
      for (int i = 0; i < 4; i++) {
        histogramas[i] = new Histograma(colores[i]);
      }

      {
        for (Histograma histograma : histogramas) {
          contenido.agregarComponente.accept(histograma.componente);
          histograma.componente.setPreferredSize(medidaGrafica);
          histograma.componente.setVisible(false);
        }
      }
    }

    JButton botonMaximos = new JButton(etiquetasBoton[maximosIgualadosInt.get()]);
    botonMaximos.setVisible(false);
    contenido.agregarComponente.accept(botonMaximos);

    // Acción para anexar la información a los histogramas
    Runnable anexarInformacionHistogramas = () -> {
      if (esRGB.get()) {
        for (int i = 0; i < 3; i++) {
          histogramas[i].establecerValores.accept(
              histograma[i],
              maximosIgualados ? Optional.of(maxHistograma) : Optional.empty());
        }
      } else {
        histogramas[3].establecerValores.accept(
            histograma[0],
            maximosIgualados ? Optional.of(maxHistograma) : Optional.empty());
      }
    };

    establecerImagen = imagen -> {
      histograma = InfoImagen.crearHistogramaNormalizado(imagen);

      // Establecer visibilidad histogramas
      titulo.setVisible(true);
      botonMaximos.setVisible(esRGB.get());
      for (int i = 0; i < 3; i++) {
        histogramas[i].componente.setVisible(esRGB.get());
      }
      histogramas[3].componente.setVisible(!esRGB.get());

      // Calcular el valor maximo del histograma
      maxHistograma = Arrays.stream(histograma)
                         .flatMapToDouble(Arrays::stream)
                         .max()
                         .getAsDouble();

      anexarInformacionHistogramas.run();
    };

    botonMaximos.addActionListener(ev -> {
      maximosIgualados = !maximosIgualados;
      botonMaximos.setText(etiquetasBoton[maximosIgualadosInt.get()]);
      anexarInformacionHistogramas.run();
    });

    this.componente = contenido.componente;
  }
}

