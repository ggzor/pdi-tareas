package imagenes;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import utils.MathUtils;
import utils.Matriz;

/**
 * OperadoresGenerales
 */
public class OperadoresGenerales {

  private static BufferedImage mezclar(BufferedImage im1, BufferedImage im2, BiFunction<Double, Double, Double> f) {
    WritableRaster r1 = im1.getRaster();
    WritableRaster r2 = im2.getRaster();

    BufferedImage result = new BufferedImage(r1.getWidth(), r1.getHeight(), im1.getType());
    WritableRaster rr = result.getRaster();

    for (int y = 0; y < r1.getHeight(); y++) {
      for (int x = 0; x < r1.getWidth(); x++) {
        for (int i = 0; i < Math.max(r1.getNumBands(), r2.getNumBands()); i++) {
          rr.setSample(x, y, i,
              (int) MathUtils.clamp(0, 255, f.apply(r1.getSampleDouble(x, y, Math.min(r1.getNumBands() - 1, i)),
                  r2.getSampleDouble(x, y, Math.min(r2.getNumBands() - 1, i)))));
        }
      }
    }

    return result;
  }

  public static BufferedImage escaladoSimple(double factor, BufferedImage im) {
    return im;
  }

  public static BufferedImage escaladoLineal(double factor, BufferedImage im) {
    return im;
  }

  public static BufferedImage combinacionLineal(double alpha, BufferedImage im1, BufferedImage im2) {
    return im1;
  }

  public static BufferedImage multiplicar(BufferedImage mask, BufferedImage im) {
    return mezclar(im, mask, (x, y) -> (y / 255.0) * x);
  }

  public static BufferedImage restar(BufferedImage im1, BufferedImage im2) {
    return mezclar(im1, im2, (x, y) -> (255.0 / 2.0) + (x - y) / 2.0);
  }

  public static BufferedImage fotomontaje(BufferedImage mascara1, BufferedImage im1, BufferedImage mascara2,
      BufferedImage im2) {

    im1 = multiplicar(mascara1, im1);
    im2 = multiplicar(mascara2, im2);

    return mezclar(im1, im2, (x, y) -> x + y);
  }

  private static Matriz punto(double x, double y) {
    return new Matriz(new double[][] { { x }, { y }, { 1 } });
  }

  private static BufferedImage reordenar(BufferedImage im, int nw, int nh,
      BiFunction<Double, Double, double[]> transformar) {
    BufferedImage resultado = new BufferedImage(nw, nh, im.getType());
    WritableRaster rr = resultado.getRaster();

    WritableRaster raster = im.getRaster();

    for (int y = 0; y < im.getHeight(); y++) {
      for (int x = 0; x < im.getWidth(); x++) {
        int[] ps = new int[raster.getNumBands()];
        raster.getPixel(x, y, ps);

        double coord[] = transformar.apply((double) x, (double) y);
        rr.setPixel((int) coord[0], (int) coord[1], ps);
      }
    }

    return resultado;
  }

  private static BufferedImage rotacionEspecial(int factor, BufferedImage im) {
    int w = im.getWidth();
    int h = im.getHeight();
    switch (factor) {
      case 0:
        return im;

      // 90 grados
      case 1: {
        return reordenar(im, h, w, (x, y) -> new double[] { y, w - x - 1 });
      }

      // 180 grados
      case 2: {
        return reordenar(im, w, h, (x, y) -> new double[] { w - x - 1, h - y - 1 });
      }

      // 270 grados
      case 3: {
        return reordenar(im, h, w, (x, y) -> new double[] { h - y - 1, x });
      }

      default:
        return im;
    }
  }

  public static BufferedImage rotacion(double factor, BufferedImage im) {
    if ((Math.abs(factor - Math.floor(factor)) < 1e-3) && ((int) Math.floor(factor)) % 90 == 0)
      return rotacionEspecial((((int) Math.floor(factor)) % 360) / 90, im);

    Matriz rotacion = Matriz.rotacionCCW(factor);

    double wm = Math.floor(im.getWidth() / 2.0);
    double hm = Math.floor(im.getHeight() / 2.0);

    Matriz[] extremos = Stream.of(punto(-wm, hm), punto(wm, hm), punto(wm, -hm), punto(-wm, -hm))
        .map(rotacion::multiplicar).toArray(Matriz[]::new);

    double[] xs = Arrays.stream(extremos).mapToDouble(p -> p.datos[0][0]).sorted().toArray();
    double[] ys = Arrays.stream(extremos).mapToDouble(p -> p.datos[1][0]).sorted().toArray();

    int xmin = (int) Math.floor(xs[0]);
    int ymin = (int) Math.floor(ys[0]);

    int xmax = (int) Math.ceil(xs[xs.length - 1]);
    int ymax = (int) Math.ceil(ys[ys.length - 1]);

    int wfinal = xmax - xmin + 1;
    int hfinal = ymax - ymin + 1;

    Matriz transformacion = Matriz.translacion(-xmin, -ymin)
        .multiplicar(rotacion.multiplicar(Matriz.translacion(-wm, -hm)));

    return reordenar(im, wfinal, hfinal, (x, y) -> {
      Matriz tp = transformacion.multiplicar(punto(x, y));
      return new double[] { tp.datos[0][0], tp.datos[1][0] };
    });
  }
}
