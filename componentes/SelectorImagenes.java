package componentes;

import imagenes.*;
import utils.*;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * SelectorImagenes
 */
public class SelectorImagenes extends JFileChooser {
  private static final String extensiones[] = new String[] { ".jpeg", ".jpg", ".bmp" };

  public SelectorImagenes() {
    addChoosableFileFilter(new FileFilter() {

      @Override
      public boolean accept(File f) {
        if (f.isDirectory())
          return true;

        return Arrays.stream(extensiones)
                     .anyMatch(ext -> f.getName().endsWith(ext));
      }

      @Override
      public String getDescription() {
        return "Imágenes";
      }
    });

    setAcceptAllFileFilterUsed(false);
  }

  public Optional<Either<ErrorImagen, BufferedImage>> abrirImagen(Component padre) {
    boolean seSeleccionoArchivo = showOpenDialog(padre) == JFileChooser.APPROVE_OPTION;

    if (seSeleccionoArchivo && getSelectedFile() != null)
      return Optional.of(ImagenesIO.abrirImagen(getSelectedFile()));
    else
      return Optional.empty();
  }
}
