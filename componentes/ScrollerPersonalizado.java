package componentes;

import javax.swing.JScrollPane;

/**
 * Clase que modifica el funcionamiento estándar para hacer scroll más rápido
 */
public class ScrollerPersonalizado extends JScrollPane {
  public static final int INCREMENTO_SCROLL = 10;

  public ScrollerPersonalizado() {
    // Permitir un desplazamiento rápido
    getVerticalScrollBar().setUnitIncrement(INCREMENTO_SCROLL);
    getHorizontalScrollBar().setUnitIncrement(INCREMENTO_SCROLL);
  }
}

