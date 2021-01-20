package imagenes;

import java.awt.image.*;

/**
 * Clase principal para realizar el procesamiento de una imagen
 */
public class Procesamiento {

  /**
   * Crea el histograma de la imagen dada sin normalizar
   * */
  public static double[][] crearHistograma(BufferedImage imagen) {
    WritableRaster raster = imagen.getRaster();
    int canales = raster.getNumBands();
    double[][] histograma = new double[canales][];

    for (int i = 0; i < canales; i++) {
      histograma[i] = new double[256];
    }

    // Iterar por cada pixel por cada canal
    for (int y = 0; y < raster.getHeight(); y++) {
      for (int x = 0; x < raster.getWidth(); x++) {
        for (int b = 0; b < canales; b++) {
          int valor = raster.getSample(x, y, b);
          histograma[b][valor]++;
        }
      }
    }

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

  private Procesamiento() {}
}

