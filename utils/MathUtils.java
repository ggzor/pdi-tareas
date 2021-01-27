package utils;

/**
 * Utilerías matemáticas no presentes en la librería estándar
 */
public class MathUtils {
  /**
   * Restringe un valor en el intervalo [start, end]
   * */
  public static double clamp(double start, double end, double value) {
    return Math.max(start, Math.min(value, end));
  }

  private MathUtils() {}
}
