package imagenes;

/**
 * Interfaz para representar un consumidor sobre cada pixel
 * */
@FunctionalInterface
public interface OperadorPixel {
  public void operar(int x, int y, int[] canales);
}

