package imagenes;

import java.awt.geom.*;
import java.awt.image.*;

/**
 * Utilerías para mostrar imágenes en componentes
 */
public class ImageUtils {
  public static final double ESCALA_EPSILON = 0.01;

  public static BufferedImage escalar(BufferedImage imagen, double escala) {
    int nuevoAncho = (int)((double)imagen.getWidth() * escala);
    int nuevoAlto = (int)((double)imagen.getHeight() * escala);

    if (Math.abs(escala - 1.0) > ESCALA_EPSILON) {
      BufferedImage nueva = new BufferedImage(nuevoAncho, nuevoAlto, imagen.getType());
      AffineTransform scaleInstance = AffineTransform.getScaleInstance(escala, escala);
      AffineTransformOp op = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BILINEAR);
      op.filter(imagen, nueva);
      return nueva;
    } else {
      return imagen;
    }
  }

  private ImageUtils() {}
}
