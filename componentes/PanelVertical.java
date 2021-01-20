package componentes;

import java.util.function.*;
import javax.swing.*;

/**
 * Un panel que contiene un layout vertical con scroll
 */
public class PanelVertical {

  public final JComponent componente;
  public final Consumer<JComponent> agregarComponente;

  public PanelVertical(boolean usarScroll) {
    JPanel contenido = new JPanel();
    contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
    agregarComponente = contenido::add;

    if (usarScroll) {
      ScrollerPersonalizado scroller = new ScrollerPersonalizado();
      scroller.setViewportView(contenido);
      componente = scroller;
    } else {
      componente = contenido;
    }
  }
}

