package componentes;

import java.awt.*;
import java.util.function.*;

/**
 * Utilerías para los componentes
 */
public class CompUtils {

  public static void centrarEscritorio(Window ventana, IntUnaryOperator fwidth, IntUnaryOperator fheight) {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    ventana.setSize(new Dimension(fwidth.applyAsInt(screen.width),
                                  fheight.applyAsInt(screen.height)));
    ventana.setLocationRelativeTo(null);
  }

  private CompUtils() {}
}
