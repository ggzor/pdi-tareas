package componentes;

import imagenes.*;
import utils.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.Optional;

/**
 * Utils
 */
public class Utils {
  public static Optional<Either<ErrorImagen, BufferedImage>>
                abrirImagen(Component padre, JFileChooser selector) {
    boolean seSeleccionoArchivo =
      selector.showOpenDialog(padre) == JFileChooser.APPROVE_OPTION;

    if (seSeleccionoArchivo && selector.getSelectedFile() != null)
      return Optional.of(ImagenesIO.abrirImagen(selector.getSelectedFile()));
    else
      return Optional.empty();
  }
}

