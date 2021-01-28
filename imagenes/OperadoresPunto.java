package imagenes;

import java.awt.image.*;
import java.util.function.Function;

import utils.MathUtils;

/**
 * La implementación del algoritmo genérico para los operadores punto sobre
 * una imagen, así como implementaciones particulares de operadores punto
 */
public class OperadoresPunto {

  /**
   * Interfaz para representar una transformación de una imagen por pixel
   * utilizando todos los canales
   * */
  @FunctionalInterface
  public static interface OperadorPixel {
    public int[] operar(int x, int y, int[] canales);
  }

  /**
   * Función de orden superior para crear operadores de punto
   * */
  public static BufferedImage aplicar(BufferedImage src, OperadorPixel f, int tipo) {
    BufferedImage nueva = new BufferedImage(src.getWidth(), src.getHeight(), tipo);

    Procesamiento.iterarPixeles(src, (x, y, canales) -> {
      int[] nuevosCanales = f.operar(x, y, canales);
      nueva.getRaster().setPixel(x, y, nuevosCanales);
    });

    return nueva;
  }

  /**
   * Función de utilería cuando no importan las coordenadas del pixel
   * */
  public static BufferedImage aplicar(BufferedImage src, Function<int[], int[]> f, int tipo) {
    return aplicar(src, (x, y, canales) -> f.apply(canales), tipo);
  }

  /**
   * Operador de umbralización uniforme en todos sus canales
   * */
  public static BufferedImage umbralizar(BufferedImage imagen, int corte) {
    int[] valores = new int[imagen.getRaster().getNumBands()];

    return aplicar(imagen, canales -> {
      for (int i = 0; i < canales.length; i++) {
        valores[i] = canales[i] < corte ? 0 : 255;
      }
      return valores;
    }, imagen.getType());
  }

  /**
   * Transformación a blanco y negro
   * */
  public static BufferedImage blancoNegro(BufferedImage imagen, ModoBN modo) {
    if (imagen.getRaster().getNumBands() == 3) {
      // Un arreglo para ir guardando los valores resultantes
      // Es una optimización para no crear un nuevo arreglo
      int[] resultado = new int[1];

      return aplicar(imagen, canales -> {
        double valor = modo.pesoR * canales[0] +
                       modo.pesoG * canales[1] +
                       modo.pesoB * canales[2];
        resultado[0] = MathUtils.clamp(0, 255, (int)valor);
        return resultado;
      }, BufferedImage.TYPE_BYTE_GRAY);
    } else {
      return imagen;
    }
  }

  /**
   * Enmascarar una imagen utilizando otra como referencia
   * generalmente una imagen en blanco y negro
   * */
  public static BufferedImage enmascarar(BufferedImage src, BufferedImage mask) {
    if (src.getWidth() != mask.getWidth() || src.getHeight() != mask.getHeight())
      throw new IllegalArgumentException("No coinciden los tamaños de las imágenes");

    WritableRaster rasterMask = mask.getRaster();

    int valores[] = new int[src.getRaster().getNumBands()];
    return aplicar(src, (x, y, canales) -> {
      for (int i = 0; i < canales.length; i++) {
        int m = rasterMask.getSample(x, y, Math.min(rasterMask.getNumBands() - 1, i));
        valores[i] = m == 0 ? 0 : canales[i];
      }

      return valores;
    }, src.getType());
  }

  private OperadoresPunto() { }

}

