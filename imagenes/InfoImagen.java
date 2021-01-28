package imagenes;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

/**
 * Funciones que sólamente calculan información de la imagen sin modificarla
 */
public class InfoImagen {

  /** Esta clase no se puede instanciar */
  private InfoImagen() { }

  /**
   * Crea el histograma de la imagen dada sin normalizar
   * */
  public static double[][] crearHistograma(BufferedImage img) {
    int canales = img.getRaster().getNumBands();
    double[][] histograma = new double[canales][];

    for (int i = 0; i < canales; i++) {
      histograma[i] = new double[256];
    }

    // Iterar por cada pixel por cada canal
    Procesamiento.iterarPixeles(img, (x, y, valores) -> {
      for (int b = 0; b < canales; b++) {
        histograma[b][valores[b]]++;
      }
    });

    return histograma;
  }

  /**
   * Crea el histograma y aplicar la normalización
   * */
  public static double[][] crearHistogramaNormalizado(BufferedImage imagen) {
    double[][] histograma = crearHistograma(imagen);
    double totalPixeles = (double)(imagen.getWidth() * imagen.getHeight());

    for (double[] canal : histograma) {
      for (int i = 0; i < canal.length; i++) {
        canal[i] /= totalPixeles;
      }
    }

    return histograma;
  }

  /**
   * Calcula el valor para la umbralización utilizando el algoritmo de Otsu
   * */
  public static int calcularUmbralOtsu(BufferedImage imagen) {
    if (imagen.getRaster().getNumBands() != 1)
      throw new IllegalArgumentException("Sólo soporta imágenes de un canal");

    double p[] = crearHistogramaNormalizado(imagen)[0];

    double ros[] = new double[p.length];
    Arrays.fill(ros, Double.MIN_VALUE);

    for (int t = 0; t < 255; t++) {
      int tref = t;
      Supplier<DoubleStream> P1 = () -> Arrays.stream(p).limit(tref + 1),
                             P2 = () -> Arrays.stream(p).skip(tref + 1);

      double omega1 = P1.get().sum();
      double omega2 = P2.get().sum();

      AtomicInteger indice = new AtomicInteger(-1);
      double u1 = P1.get()
                    .map(pi -> (double)indice.incrementAndGet() * pi / omega1)
                    .sum();
      indice.set(-1);
      double u2 = P2.get()
                    .map(pi -> (double)indice.incrementAndGet() * pi / omega2)
                    .sum();

      double uT = omega1 * u1 + omega2 * u2;

      ros[t] = omega1 * Math.pow((u1 - uT), 2) + omega2 * Math.pow((u2 - uT), 2);
    }

    // Encontrar índice máximo
    double maximo = Double.MIN_VALUE;
    int indiceMaximo = 0;
    for (int t = 0; t < ros.length; t++) {
      if (ros[t] > maximo) {
        maximo = ros[t];
        indiceMaximo = t;
      }
    }
    return indiceMaximo;
  }
}

