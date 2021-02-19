package imagenes;

import java.awt.image.*;
import java.util.*;
import java.util.function.*;

import utils.MathUtils;

/**
 * La implementación del algoritmo genérico para los operadores punto sobre
 * una imagen, así como implementaciones particulares de operadores punto
 */
public class OperadoresPunto {

  /**
   * Interfaz para representar una transformación de una imagen por pixel
   * utilizando todos los canales
   * */
  @FunctionalInterface
  public static interface OperadorPixel {
    public int[] operar(int x, int y, int[] canales);
  }

  /**
   * Aplicar un operador a todos los canales dadas las coordenadas
   * */
  @FunctionalInterface
  public static interface OperadorPixelMonoCanal {
    public int operar(int x, int y, int valor);
  }

  /**
   * Función de orden superior para crear operadores de punto
   * */
  public static BufferedImage aplicar(BufferedImage src, OperadorPixel f, int tipo) {
    BufferedImage nueva = new BufferedImage(src.getWidth(), src.getHeight(), tipo);

    Procesamiento.iterarPixeles(src, (x, y, canales) -> {
      int[] nuevosCanales = f.operar(x, y, canales);
      nueva.getRaster().setPixel(x, y, nuevosCanales);
    });

    return nueva;
  }

  /**
   * Función de utilería cuando no importan las coordenadas del pixel
   * */
  public static BufferedImage aplicar(BufferedImage src, Function<int[], int[]> f, int tipo) {
    return aplicar(src, (x, y, canales) -> f.apply(canales), tipo);
  }

  /**
   * Función para aplicar una operación en todos los canales de una imagen
   * */
  public static BufferedImage aplicarMulticanal(BufferedImage src, OperadorPixelMonoCanal f) {
    return aplicar(src, (x, y, canales) -> {
      for (int i = 0; i < canales.length; i++) {
        canales[i] = f.operar(x, y, canales[i]);
      }
      return canales;
    }, src.getType());
  }

  public static BufferedImage aplicarMulticanal(BufferedImage src, IntUnaryOperator f) {
    return aplicarMulticanal(src, (x, y, v) -> f.applyAsInt(v));
  }

  public static BufferedImage aplicarMulticanalRestringido(BufferedImage src, IntUnaryOperator f) {
    return aplicarMulticanal(src, v -> MathUtils.clamp(0, 255, f.applyAsInt(v)));
  }

  /**
   * Operador de umbralización uniforme en todos sus canales
   * */
  public static BufferedImage umbralizar(BufferedImage src, int corte) {
    return aplicarMulticanal(src, valor -> valor < corte ? 0 : 255);
  }

  /**
   * Transformación a blanco y negro
   * */
  public static BufferedImage blancoNegro(BufferedImage imagen, ModoBN modo) {
    if (imagen.getRaster().getNumBands() == 3) {
      // Un arreglo para ir guardando los valores resultantes
      // Es una optimización para no crear un nuevo arreglo
      int[] resultado = new int[1];

      return aplicar(imagen, canales -> {
        double valor = modo.pesoR * canales[0] +
                       modo.pesoG * canales[1] +
                       modo.pesoB * canales[2];
        resultado[0] = MathUtils.clamp(0, 255, (int)valor);
        return resultado;
      }, BufferedImage.TYPE_BYTE_GRAY);
    } else {
      return imagen;
    }
  }

  /**
   * Enmascarar una imagen utilizando otra como referencia
   * generalmente una imagen en blanco y negro
   * */
  public static BufferedImage enmascarar(BufferedImage src, BufferedImage mask) {
    if (src.getWidth() != mask.getWidth() || src.getHeight() != mask.getHeight())
      throw new IllegalArgumentException("No coinciden los tamaños de las imágenes");

    WritableRaster rasterMask = mask.getRaster();

    int valores[] = new int[src.getRaster().getNumBands()];
    return aplicar(src, (x, y, canales) -> {
      for (int i = 0; i < canales.length; i++) {
        int m = rasterMask.getSample(x, y, Math.min(rasterMask.getNumBands() - 1, i));
        valores[i] = m == 0 ? 0 : canales[i];
      }

      return valores;
    }, src.getType());
  }

  /**
   * Realiza la inversión de la imagen que le es dada
   * */
  public static BufferedImage invertir(BufferedImage src) {
    return aplicarMulticanal(src, valor -> 255 - valor);
  }

  public static BufferedImage exponenciar(BufferedImage src, double exp) {
    return aplicarMulticanalRestringido(src, valor ->
             (int)Math.pow(valor, exp)
           );
  }

  public static BufferedImage gamma(BufferedImage src, double exp) {
    return aplicarMulticanalRestringido(src, valor ->
             (int)(255 * Math.pow(valor / 255.0, exp))
           );
  }

  public static BufferedImage seno(BufferedImage src, double k) {
    return aplicarMulticanalRestringido(src, valor ->
             (int)(k * 255.0 * Math.sin(Math.PI / 510.0 * valor))
           );
  }

  public static BufferedImage brillo(BufferedImage src, double b) {
    return aplicarMulticanalRestringido(src, valor ->
             (int)(valor + b)
           );
  }

  public static BufferedImage contraste(BufferedImage src, double a) {
    return aplicarMulticanal(src, valor ->
             (int)(valor * a)
           );
  }

  public static BufferedImage sigmoide(BufferedImage src, double a) {
    return aplicarMulticanalRestringido(src, valor ->
             (int)(127.5 * (1 + Math.tanh(a * (valor - 127.5)))
           ));
  }

  public static BufferedImage diferencias(BufferedImage src, int umbral) {
    int resultado[] = new int[1];
    return aplicar(src, canales -> {
      int dif = (canales[0] - canales[1]) + (canales[1] - canales[2]);
      resultado[0] = dif < umbral ? 0 : 255;
      return resultado;
    }, BufferedImage.TYPE_BYTE_GRAY);
  }

  public static BufferedImage separarRGB(BufferedImage src, int umbral) {
    int resultado[] = new int[1];
    return aplicar(src, canales -> {
      int suma = canales[0] + canales[1] + canales[2];
      resultado[0] = suma < umbral * 3 ? 0 : 255;
      return resultado;
    }, BufferedImage.TYPE_BYTE_GRAY);
  }

  public static BufferedImage interseccion(BufferedImage src, BufferedImage src2) {
    WritableRaster wr = src2.getRaster();
    return aplicarMulticanal(src, (x, y, valor) -> {
      int valor2 = wr.getSample(x, y, 0);
      return valor == valor2 && valor == 255 ? 255 : 0;
    });
  }

  public static BufferedImage diff(BufferedImage src, BufferedImage src2) {
    WritableRaster wr = src2.getRaster();
    return aplicarMulticanal(src, (x, y, valor) -> {
      int valor2 = wr.getSample(x, y, 0);
      return Math.max(0, valor - valor2);
    });
  }

  public static BufferedImage recolorear(BufferedImage src, int original[], int destino[]) {
    return aplicar(src, canales ->
         (canales[0] == original[0] && canales[1] == original[1]
                                    && canales[2] == original[2])
         ? destino : canales,
         src.getType());
  }

  private OperadoresPunto() { }
}

