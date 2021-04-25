package utils;

/**
 * Matriz
 */
public class Matriz {
  public final double[][] datos;
  public final int filas, columnas;

  public Matriz(double[][] datos) {
    this.datos = datos;
    this.filas = datos.length;
    this.columnas = datos[0].length;
  }

  public Matriz multiplicar(Matriz otra) {
    double[][] resultado = new double[this.filas][otra.columnas];

    for (int i = 0; i < this.filas; i++) {
      for (int j = 0; j < otra.columnas; j++) {
        double x = 0.0;

        for (int k = 0; k < this.columnas; k++)
          x += this.datos[i][k] * otra.datos[k][j];

        resultado[i][j] = x;
      }
    }

    return new Matriz(resultado);
  }

  public static Matriz rotacion(double anguloGrados) {
    final double cos = Math.cos(anguloGrados * Math.PI / 180.0);
    final double sin = Math.sin(anguloGrados * Math.PI / 180.0);

    return new Matriz(new double[][] { { cos, -sin, 0 }, { sin, cos, 0 }, { 0, 0, 1 } });
  }

  public static Matriz rotacionCCW(double anguloGrados) {
    final double cos = Math.cos(anguloGrados * Math.PI / 180.0);
    final double sin = Math.sin(anguloGrados * Math.PI / 180.0);

    return new Matriz(new double[][] { { cos, sin, 0 }, { -sin, cos, 0 }, { 0, 0, 1 } });
  }

  public static Matriz translacion(double dx, double dy) {
    return new Matriz(new double[][] { { 1, 0, dx }, { 0, 1, dy }, { 0, 0, 1 } });
  }
}
