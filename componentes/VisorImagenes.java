package componentes;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.function.Consumer;

/**
 * VisorImagenes
 */
public class VisorImagenes {
  public static final int BORDE = 16;
  public static final int INCREMENTO_SCROLL = 10;

  public final Consumer<BufferedImage> establecerImagen;
  public final Component componente;

  public VisorImagenes() {
    JScrollPane contenedorImagen = new JScrollPane();
    contenedorImagen.getVerticalScrollBar().setUnitIncrement(INCREMENTO_SCROLL);
    contenedorImagen.getHorizontalScrollBar().setUnitIncrement(INCREMENTO_SCROLL);
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

