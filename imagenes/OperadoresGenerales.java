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

  private static double enIzquierda(Matriz p, Matriz a, Matriz b) {
    double px = p.datos[0][0];
    double py = p.datos[1][0];

    double ax = a.datos[0][0];
    double ay = a.datos[1][0];

    double bx = b.datos[0][0];
    double by = b.datos[1][0];

    return (ax - px) * (by - py) - (bx - px) * (ay - py);
  }

  private static boolean puntoEnRectangulo(Matriz a, Matriz b, Matriz c, Matriz d, Matriz p) {
    return enIzquierda(p, a, b) >= 0 && enIzquierda(p, b, c) >= 0 && enIzquierda(p, c, d) >= 0
        && enIzquierda(p, d, a) >= 0;
  }

  public static BufferedImage rotacion(double factor, BufferedImage im) {
    if ((Math.abs(factor - Math.floor(factor)) < 1e-3) && ((int) Math.floor(factor)) % 90 == 0)
      return rotacionEspecial((((int) Math.floor(factor)) % 360) / 90, im);

    Matriz rotacion = Matriz.rotacionCCW(factor);

    double wm = Math.floor(im.getWidth() / 2.0);
    double hm = Math.floor(im.getHeight() / 2.0);

    Matriz[] extremos = Stream.of(punto(-wm, hm), punto(-wm, -hm), punto(wm, -hm), punto(wm, hm))
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

    boolean rellenos[][] = new boolean[hfinal][wfinal];

    BufferedImage rotada = reordenar(im, wfinal, hfinal, (x, y) -> {
      Matriz tp = transformacion.multiplicar(punto(x, y));
      rellenos[(int) tp.datos[1][0]][(int) tp.datos[0][0]] = true;
      return new double[] { tp.datos[0][0], tp.datos[1][0] };
    });

    // Ajustar extremos a sus coordenadas reales
    for (Matriz m : extremos) {
      m.datos[0][0] -= xmin;
      m.datos[1][0] -= ymin;
    }

    // Agregando relleno de ruido
    WritableRaster raster = rotada.getRaster();
    for (int y = 0; y < hfinal; y++) {
      for (int x = 0; x < wfinal; x++) {
        Matriz p = punto(x, y);
        if (!rellenos[y][x] && puntoEnRectangulo(extremos[0], extremos[1], extremos[2], extremos[3], p)) {
          for (int i = 0; i < raster.getNumBands(); i++) {
            double v = 0.0;
            int c = 0;

            for (int dy = -2; dy <= 2; dy++) {
              for (int dx = -2; dx <= 2; dx++) {
                int fx = x + dx;
                int fy = y + dy;

                if (0 <= fx && fx < wfinal && 0 <= fy && fy < hfinal && rellenos[fy][fx]) {
                  v += raster.getSample(fx, fy, i);
                  c++;
                }
              }
            }

            if (c > 0) {
              v /= (double) c;
            }

            raster.setSample(x, y, i, MathUtils.clamp(0, 255, (int) v));
          }
        }
      }
    }

    return rotada;
  }

  private static double pendiente(int[] p1, int p2[]) {
    // (y1 - y0) / (x1 - x0)
    return (double) (p2[1] - p1[1]) / (p2[0] - p1[0]);
  }

  private static double calcularY(double m, int x, int y0) {
    // m * (x - x0) + y0 = m * x + y0 por que x0 siempre es igual a 0
    return m * x + y0;
  }

  private static void interpolarDireccion(int[] p1, int[] p2, WritableRaster raster, int incrX, int incrY, int factor) {
    // Interpola los puntos que se encuentran entre p1 y p2
    int x = 1;
    int[] pAux = new int[] { p1[0] + incrX, p1[1] + incrY };
    double m = 0;
    int nCanales = raster.getNumBands();
    int[] y0xCanal = new int[3];
    int[] y1xCanal = new int[3];
    int[] pixel = new int[3];
    raster.getPixel(p1[0], p1[1], y0xCanal);
    raster.getPixel(p2[0], p2[1], y1xCanal);

    while (!Arrays.equals(p2, pAux)) {
      for (int i = 0; i < nCanales; i++) {
        m = pendiente(new int[] { 0, y0xCanal[i] }, new int[] { factor, y1xCanal[i] });
        pixel[i] = (int) Math.floor(calcularY(m, x, y0xCanal[i]));
      }

      raster.setPixel(pAux[0], pAux[1], pixel);
      x++;
      pAux[0] += incrX;
      pAux[1] += incrY;
    }

  }

  private static void interpolarArea(int[] pivoteSupIzq, WritableRaster raster, int factor) {
    // Caso base
    if (factor <= 1)
      return;

    int[] sigPivote = new int[3];
    int[] pivoteSupDer = new int[] { pivoteSupIzq[0] + factor, pivoteSupIzq[1] };
    int[] pivoteInfIzq = new int[] { pivoteSupIzq[0], pivoteSupIzq[1] + factor };
    int[] pivoteInfDer = new int[] { pivoteSupDer[0], pivoteInfIzq[1] };
    int[][] interpolacionHorizontalSup = new int[][] { pivoteSupIzq, pivoteSupDer };
    int[][] interpolacionHorizontalInf = new int[][] { pivoteInfIzq, pivoteInfDer };
    int[][] interpolacionVerticalIzq = new int[][] { pivoteSupIzq, pivoteInfIzq };
    int[][] interpolacionVerticalDer = new int[][] { pivoteSupDer, pivoteInfDer };
    int[][] interpolacionDiagonal = new int[][] { pivoteSupIzq, pivoteInfDer };
    int[][] interpolacionDiagonalInv = new int[][] { pivoteInfIzq, pivoteSupDer };

    interpolarDireccion(interpolacionHorizontalSup[0], interpolacionHorizontalSup[1], raster, 1, 0, factor);
    interpolarDireccion(interpolacionHorizontalInf[0], interpolacionHorizontalInf[1], raster, 1, 0, factor);

    interpolarDireccion(interpolacionVerticalIzq[0], interpolacionVerticalIzq[1], raster, 0, 1, factor);
    interpolarDireccion(interpolacionVerticalDer[0], interpolacionVerticalDer[1], raster, 0, 1, factor);

    interpolarDireccion(interpolacionDiagonal[0], interpolacionDiagonal[1], raster, 1, 1, factor);
    interpolarDireccion(interpolacionDiagonalInv[0], interpolacionDiagonalInv[1], raster, 1, -1, factor);

    sigPivote[0] = pivoteSupIzq[0] + 1;
    sigPivote[1] = pivoteSupIzq[1] + 1;
    interpolarArea(sigPivote, raster, factor - 1);
  }

  public static BufferedImage escaladoInterpolacion(int factor, BufferedImage imagen) {
    final int nuevoWidth = imagen.getWidth() * factor;
    final int nuevoHeight = imagen.getHeight() * factor;

    int[] pixel = new int[3];
    BufferedImage nueva = new BufferedImage(nuevoWidth, nuevoHeight, imagen.getType());
    WritableRaster rasterOriginal = imagen.getRaster();
    WritableRaster rasterNueva = nueva.getRaster();

    // Inicializar la nueva imagen con los valores pivote
    for (int x = 0; x < imagen.getWidth(); x++) {
      for (int y = 0; y < imagen.getHeight(); y++) {
        rasterOriginal.getPixel(x, y, pixel);
        rasterNueva.setPixel(x * factor, y * factor, pixel);
      }
    }

    // Iterar sobre cada area(factor x factor) e intepolar los valores faltantes
    for (int x = 0; x < nuevoWidth - factor; x += factor) {
      for (int y = 0; y < nuevoHeight - factor; y += factor) {
        interpolarArea(new int[] { x, y }, rasterNueva, factor);
      }
    }

    return nueva;
  }

  public static BufferedImage fusionCombinacionLineal(double alfa, BufferedImage imagen1, BufferedImage imagen2) {
    final double beta = 1.0 - alfa;

    // Inicializacion de imagenes/raster
    BufferedImage nueva = new BufferedImage(imagen1.getWidth(), imagen1.getHeight(), imagen1.getType());
    WritableRaster raster1 = imagen1.getRaster();
    WritableRaster raster2 = imagen2.getRaster();
    WritableRaster rasterNueva = nueva.getRaster();
    double[] pixel1 = new double[3];
    double[] pixel2 = new double[3];

    // Iteracion sobre cada pixel en ambas imagenes
    for (int x = 0; x < imagen1.getWidth(); x++) {
      for (int y = 0; y < imagen1.getHeight(); y++) {
        raster1.getPixel(x, y, pixel1);
        raster2.getPixel(x, y, pixel2);

        rasterNueva.setPixel(x, y, new double[] { pixel1[0] * alfa + pixel2[0] * beta,
            pixel1[1] * alfa + pixel2[1] * beta, pixel1[2] * alfa + pixel2[2] * beta });
      }
    }

    return nueva;
  }

  public static BufferedImage escaladoSimple(int factor, BufferedImage imagen) {
    final int widthEscalado = imagen.getWidth() * factor;
    final int heightEscalado = imagen.getHeight() * factor;
    BufferedImage nueva = new BufferedImage(widthEscalado, heightEscalado, imagen.getType());
    WritableRaster originalRaster = imagen.getRaster();
    WritableRaster raster = nueva.getRaster();
    float[] pixelPivote = new float[3];

    // Iterar sobre cada pixel de la imagen origianl
    for (int x = 0; x < imagen.getWidth(); x++) {
      for (int y = 0; y < imagen.getHeight(); y++) {
        originalRaster.getPixel(x, y, pixelPivote);

        // Duplicar el pixel dependiendo del factor
        for (int rX = 0; rX < factor; rX++) {
          for (int rY = 0; rY < factor; rY++) {
            raster.setPixel(x * factor + rX, y * factor + rY, pixelPivote);
          }
        }

      }
    }

    return nueva;
  }
}
