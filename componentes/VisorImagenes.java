package componentes;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import java.util.function.*;

/**
 * Componente para visualizar una imagen y permitir su actualización
 */
public class VisorImagenes {
  public static final int BORDE = 16;

  // Función para establecer la imagen
  public final Consumer<BufferedImage> establecerImagen;
  // El componente que se debe agregar para mostrar la imagen
  public final JComponent componente;

  public VisorImagenes() {
    ScrollerPersonalizado contenedorImagen = new ScrollerPersonalizado();
    {
      JPanel contenido = new JPanel();
      contenido.setBorder(BorderFactory.createEmptyBorder(BORDE, BORDE, BORDE, BORDE));
      contenido.setLayout(new GridBagLayout());
      {
        JLabel label = new JLabel();
        establecerImagen = imagen -> label.setIcon(new ImageIcon(imagen));

        contenido.add(label);
      }

      contenedorImagen.setViewportView(contenido);
    }

    componente = contenedorImagen;
  }
}

