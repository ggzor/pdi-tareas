package imagenes;

/**
 * ModoBN
 */
public enum ModoBN {
  PROMEDIO(1.0/3.0, 1.0/3.0, 1.0/3.0),
  TV_LUMINANCIA(0.299, 0.587, 0.114),
  ITU(0.2126, 0.7152, 0.0722),
  TV_GAMMA(0.309, 0.609, 0.082),
  ;
  public final double pesoR, pesoG, pesoB;

  private ModoBN(double pesoR, double pesoG, double pesoB) {
    this.pesoR = pesoR;
    this.pesoG = pesoG;
    this.pesoB = pesoB;
  }
}
