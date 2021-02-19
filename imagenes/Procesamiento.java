package imagenes;

import java.awt.image.*;

/**
 * Clase principal para realizar el procesamiento de una imagen
 */
public class Procesamiento {
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

  /**
   * Crea una imagen con las dimensiones dadas y el tipo dado utilizando el
   * generador especificado
   * */
  public static BufferedImage
    generarImagen(int ancho, int alto, int tipo, OperadorPixel generadorCanales) {
    BufferedImage resultado = new BufferedImage(ancho, alto, tipo);
    WritableRaster raster = resultado.getRaster();

    iterarPixeles(resultado, (x, y, canales) -> {
      generadorCanales.operar(x, y, canales);
      raster.setPixel(x, y, canales);
    });

    return resultado;
  }

  private Procesamiento() {}
}

