package imagenes;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

/**
 * InfoImagen
 */
public class InfoImagen {

  private InfoImagen() { }

  public static int calcularUmbralOtsu(BufferedImage imagen) {
    if (imagen.getRaster().getNumBands() != 1)
      throw new IllegalArgumentException("Sólo soporta imágenes de un canal");

    double p[] = Procesamiento.crearHistogramaNormalizado(imagen)[0];

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

