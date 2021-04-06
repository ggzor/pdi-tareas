package imagenes;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.function.*;

import utils.MathUtils;

/**
 * Los operadores convolucionales
 */
public class OperadoresConvolucionales {

  /**
   * La función principal para llevar a cabo las convoluciones sobre una
   * imagen a partir de un kernel dado
   * */
  public static <T> BufferedImage aplicar(
                                    BufferedImage bImg,
                                    int wk, int hk,
                                    Supplier<T> inicial,
                                    OperadorMatrizPixel<T> operador,
                                    Function<T, Double> finalizar) {
    int w = bImg.getWidth();
    int h = bImg.getHeight();
    WritableRaster img = bImg.getRaster();

    int nw = w - wk + 1;
    int nh = h - hk + 1;

    BufferedImage resultado = new BufferedImage(nw, nh, bImg.getType());
    WritableRaster raster = resultado.getRaster();

    int bands[] = new int[img.getNumBands()];

    ArrayList<T> temp = new ArrayList<>();
    for (int i = 0; i < bands.length; i++)
      temp.add(inicial.get());

    for (int y = 0; y < nh; y++) {
      for (int x = 0; x < nw; x++) {
        for (int i = 0; i < bands.length; i++)
          temp.set(i, inicial.get());

        for (int dy = 0; dy < hk; dy++) {
          for (int dx = 0; dx < wk; dx++) {
            img.getPixel(x + dx, y + dy, bands);

            for (int i = 0; i < img.getNumBands(); i++)
              temp.set(i, operador.aplicar(temp.get(i), bands[i], dx, dy));
          }
        }

        for (int i = 0; i < img.getNumBands(); i++)
          bands[i] = MathUtils.clamp(0, 255, (int)((double)finalizar.apply(temp.get(i))));

        raster.setPixel(x, y, bands);
      }
    }

    return resultado;
  }

  public static BufferedImage aplicar(BufferedImage img, double[][] kernel) {
    return aplicar(img,
                   kernel[0].length, kernel.length,
                   () -> 0.0,
                   (temp, x, kx, ky) -> temp + x * kernel[ky][kx],
                   d -> d);
  }

  private static final double[][] sobelHorizontal =
    {
      {1, 0, -1},
      {2, 0, -2},
      {1, 0, -1},
    };

  private static final double[][] sobelVertical =
    {
      { 1,  2,  1},
      { 0,  0,  0},
      {-1, -2, -1},
    };

  public static BufferedImage sobel(BufferedImage img) {
    return aplicar(img,
                   3, 3,
                   () -> new double[2],
                   (temp, x, kx, ky) -> {
                     temp[0] += sobelHorizontal[ky][kx] * x;
                     temp[1] += sobelVertical[ky][kx] * x;
                     return temp;
                   },
                   temp -> Math.sqrt(Math.pow(temp[0], 2) + Math.pow(temp[1], 2)));
  }
}
