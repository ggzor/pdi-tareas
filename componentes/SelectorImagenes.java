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
 * Componente para la selección de archivos con los formatos deseados
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
                     .anyMatch(ext -> f.getName().toLowerCase().endsWith(ext));
      }

      @Override
      public String getDescription() {
        return "Imágenes";
      }
    });

    setAcceptAllFileFilterUsed(false);
  }

  // Función para abrir una imagen
  // Devuelve Optional.empty si se ha cancelado la operación
  public Optional<Either<ErrorImagen, BufferedImage>> abrirImagen(Component padre) {
    boolean seSeleccionoArchivo = showOpenDialog(padre) == JFileChooser.APPROVE_OPTION;

    if (seSeleccionoArchivo && getSelectedFile() != null)
      return Optional.of(ImagenesIO.abrirImagen(getSelectedFile()));
    else
      return Optional.empty();
  }
}
