package imagenes;

import java.awt.image.*;
import java.util.function.*;

/**
 * Clase principal para realizar el procesamiento de una imagen
 */
public class Procesamiento {

  /**
   * Interfaz para representar una operación sobre una imagen por pixel
   * */
  @FunctionalInterface
  public static interface OperadorPixel {
    public void operar(int x, int y, int[] canales);
  }

  /**
   * La función central de todas las operaciones que trabajan con imagenes
   * */
  public static void iterarPixeles(BufferedImage img, OperadorPixel f) {
    int width = img.getWidth();
    int height = img.getHeight();
    WritableRaster raster = img.getRaster();

    int[] canales = new int[raster.getNumBands()];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        raster.getPixel(x, y, canales);
        f.operar(x, y, canales);
      }
    }
  }

  public static BufferedImage
    generarImagen(int ancho, int alto, int tipo, Consumer<int[]> generadorCanales) {
    BufferedImage resultado = new BufferedImage(ancho, alto, tipo);
    WritableRaster raster = resultado.getRaster();

    iterarPixeles(resultado, (x, y, canales) -> {
      generadorCanales.accept(canales);
      raster.setPixel(x, y, canales);
    });

    return resultado;
  }

  private Procesamiento() {}
}

