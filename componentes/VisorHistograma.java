package componentes;

import static gui.DSL.*;
import imagenes.*;
import reactive.*;
import static reactive.ReactiveValueUtils.*;

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

  public VisorHistograma(Dimension medidaGrafica) {
    ReactiveValue<BufferedImage> imagen = new ReactiveValue<>();
    ReactiveValue<double[][]> histogramaR = imagen.map(InfoImagen::crearHistogramaNormalizado);
    ReactiveValue<Double> maximoHistograma =
      histogramaR.map(h -> Arrays.stream(h)
                                 .flatMapToDouble(Arrays::stream)
                                 .max().getAsDouble());
    ReactiveValue<Boolean> esRGBR = histogramaR.mapOpt(opt -> opt.map(h -> h.length == 3).orElse(false));
    ReactiveValue<Boolean> maximosIgualadosR = new ReactiveValue<>(true);

    // Crear los histogramas para todos los canales
    Histograma histogramas[] = new Histograma[4];
    {
      Color colores[] = { Color.RED, Color.GREEN, Color.BLUE, Color.GRAY };
      for (int i = 0; i < 4; i++) {
        int iref = i;
        Histograma histograma = new Histograma(colores[i]);

        ReactiveValue<Boolean> histogramaVisible =
          and(histogramaR.isPresent(), i < 3 ? esRGBR : esRGBR.map(b -> !b));

        // Escuchar los cambios
        combineLatest(histogramaR, maximosIgualadosR,
          (histogramaActual, estanMaximosIgualados) -> {
            if (histogramaVisible.get()) {
              histograma.componente.setPreferredSize(medidaGrafica);
              histograma.establecerValores.accept(
                  histogramaActual[iref % 3],
                  estanMaximosIgualados ? Optional.of(maximoHistograma.get())
                                        : Optional.empty()
              );
            }

            return null;
          });

        // Visibilidad del histograma
        histogramaVisible
          .subscribeRun(histograma.componente::setVisible);

        histogramas[i] = histograma;
      }
    }

   componente =
      vpanel()
        .add(
          label("Histogramas de la imagen")
            .visibleWhen(histogramaR.isPresent())
            .end()
        )
        .add(
          Arrays.stream(histogramas)
                .map(h -> h.componente)
                .toArray(JComponent[]::new))
        .add(
          button(maximosIgualadosR.map(b -> b ? "Graficar Individualmente"
                                              : "Igualar máximos"))
            .visibleWhen(and(histogramaR.isPresent(), esRGBR))
            .onClick(() -> maximosIgualadosR.set(!maximosIgualadosR.get()))
            .end()
        )
        .end();

    establecerImagen = imagen::set;
  }
}

