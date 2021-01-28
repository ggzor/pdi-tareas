package imagenes;

import java.awt.image.*;
import java.util.function.Function;

import utils.MathUtils;

/**
 * OperadoresPunto
 */
public class OperadoresPunto {

  public static BufferedImage umbralizar(BufferedImage imagen, int corte) {
    int[] valores = new int[imagen.getRaster().getNumBands()];

    return aplicar(imagen, canales -> {
      for (int i = 0; i < canales.length; i++) {
        valores[i] = canales[i] < corte ? 0 : 255;
      }
      return valores;
    }, imagen.getType());
  }

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

  public static BufferedImage aplicar(BufferedImage src, Function<int[], int[]> f, int tipo) {
    return aplicar(src, (x, y, canales) -> f.apply(canales), tipo);
  }

  @FunctionalInterface
  public static interface OperadorPixel {
    public int[] operar(int x, int y, int[] canales);
  }

  public static BufferedImage aplicar(BufferedImage src, OperadorPixel f, int tipo) {
    int width = src.getWidth();
    int height = src.getHeight();
    WritableRaster raster = src.getRaster();

    BufferedImage nueva = new BufferedImage(width, height, tipo);
    WritableRaster rasterNueva = nueva.getRaster();

    int[] bandas = new int[raster.getNumBands()];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        raster.getPixel(x, y, bandas);
        rasterNueva.setPixel(x, y, f.operar(x, y, bandas));
      }
    }

    return nueva;
  }

  private OperadoresPunto() { }
}
