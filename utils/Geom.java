package utils;

import java.awt.*;

/**
 * Utilerías para realizar algunos cálculos geométricos
 */
public class Geom {
  /**
   * Función para calcular cuanto se debe escalar una imagen con la
   * dimension original para que encaje en la dimensión objetivo
   * */
  public static double calcularEscalaAjuste(Dimension original, Dimension objetivo) {
    double escalaAncho = (double)objetivo.width / (double)original.width;
    double altoEscalado = escalaAncho * original.height;

    double escalaAlto = (double)objetivo.height / (double)original.height;

    if (altoEscalado <= objetivo.height)
      return escalaAncho;
    else
      return escalaAlto;
  }

  /** Esta clase no se puede instanciar */
  private Geom() {}

}
