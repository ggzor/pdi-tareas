package utils;

/**
 * Utilerías matemáticas no presentes en la librería estándar
 */
public class MathUtils {
  /**
   * Restringe un valor en el intervalo [start, end]
   */
  public static double clamp(double start, double end, double value) {
    return Math.max(start, Math.min(value, end));
  }

  public static int clamp(int start, int end, int value) {
    return Math.max(start, Math.min(value, end));
  }

  public static double gauss1(int x, double sigma) {
    return (1.0 / Math.sqrt(2 * Math.PI) * Math.exp(-Math.pow(x, 2) / (2 * Math.pow(sigma, 2))));
  }

  public static double[][] gauss2DKernel(int d, int sigma) {
    double[][] kernel = new double[d][d];

    double mean = d / 2;
    double sum = 0.0; // For accumulating the kernel values
    for (int x = 0; x < d; ++x)
      for (int y = 0; y < d; ++y) {
        kernel[x][y] = Math.exp(-0.5 * (Math.pow((x - mean) / sigma, 2.0) + Math.pow((y - mean) / sigma, 2.0)))
            / (2 * Math.PI * sigma * sigma);

        // Accumulate the kernel values
        sum += kernel[x][y];
      }

    // Normalize the kernel
    for (int x = 0; x < d; ++x)
      for (int y = 0; y < d; ++y)
        kernel[x][y] /= sum;
    return kernel;
  }

  /** Esta clase no se
  puede instanciar */

    private MathUtils() {}

}
