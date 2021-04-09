package imagenes;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.function.*;

import reactive.ReactiveValueUtils.TriFunction;
import utils.MathUtils;

/**
 * Los operadores convolucionales
 */
public class OperadoresConvolucionales {

  @FunctionalInterface
  public static interface InicializadorRegional<T> {
    public T get(int x, int y, int i, TriFunction<Integer, Integer, Integer, Integer> leerPixel);
  }

  /**
   * La función principal para llevar a cabo las convoluciones sobre una
   * imagen a partir de un kernel dado
   * */
  public static <T> BufferedImage aplicar(
                                    BufferedImage bImg,
                                    int wk, int hk,
                                    InicializadorRegional<T> inicial,
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
      temp.add(null);

    TriFunction<Integer, Integer, Integer, Integer> leerPixel = img::getSample;

    for (int y = 0; y < nh; y++) {
      for (int x = 0; x < nw; x++) {
        for (int i = 0; i < bands.length; i++)
          temp.set(i, inicial.get(x, y, i, leerPixel));

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
                   (x, y, i, im) -> 0.0,
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
                   (x, y, i, im) -> new double[2],
                   (temp, x, kx, ky) -> {
                     temp[0] += sobelHorizontal[ky][kx] * x;
                     temp[1] += sobelVertical[ky][kx] * x;
                     return temp;
                   },
                   temp -> Math.sqrt(Math.pow(temp[0], 2) + Math.pow(temp[1], 2)));
  }

  private static final double[][] kernelLaplaciano0 =
    {
      {  0, -1,  0 },
      { -1,  4, -1 },
      {  0, -1,  0 },
    };

  public static BufferedImage laplaciano0(BufferedImage img) {
    return aplicar(img, kernelLaplaciano0);
  }

  private static final double[][] kernelLaplaciano45 =
    {
      { -1, 0, -1 },
      {  0, 4,  0 },
      { -1, 0, -1 },
    };

  public static BufferedImage laplaciano45(BufferedImage img) {
    return aplicar(img, kernelLaplaciano45);
  }

  private static final double[][] kernelLaplacianoMulti =
    {
      { -1, -1, -1 },
      { -1,  8, -1 },
      { -1, -1, -1 },
    };

  public static BufferedImage laplacianoMulti(BufferedImage img) {
    return aplicar(img, kernelLaplacianoMulti);
  }

  private static final double[][] kernelLaplacianoInverso =
    {
      { 1,  1, 1 },
      { 1, -8, 1 },
      { 1,  1, 1 },
    };

  public static BufferedImage laplacianoInverso(BufferedImage img) {
    return aplicar(img, kernelLaplacianoInverso);
  }

  public static BufferedImage pasaBajas3x3(BufferedImage img) {
    return aplicar(
        img,
        3, 3,
        (x, y, i, im) -> new double[] { im.apply(x + 1, y + 1, i), 0.0 },
        (temp, x, kx, ky) -> {
          if (kx == 1 && ky == 1)
            temp[1] += (x * x) / temp[0];
          else
            temp[1] += x / temp[0];

          return temp;
        },
        arr -> arr[1]);
  }

  private static final double[][] kernelSegOrdenRobinson90 = sobelHorizontal;

  public static BufferedImage kernelSegOrdenRobinson90(BufferedImage img) {
    return aplicar(img,
                   3, 3,
                   (x, y, i, im) -> 0.0,
                   (temp, x, kx, ky) -> temp + x * kernelSegOrdenRobinson90[ky][kx],
                   d -> 128 + d);
  }

  private static final double[][] kernelBordesFHD = new double[][] {
    {  1, 0,   0,    1,   0, 0, 1},
    {  0, 0,   0,    0,   0, 0, 0},
    {  0, 0, 0.5,    0, 0.5, 0, 0},
    {  0, 0,   0, -8.5,   0, 0, 0},
    {  0, 0, 0.5,    0, 0.5, 0, 0},
    {  0, 0,   0,    0,   0, 0, 0},
    {  1, 0,   0,    1,   0, 0, 1},
  };

  public static BufferedImage bordesFHD(BufferedImage img) {
    return aplicar(img, kernelBordesFHD);
  }

}
