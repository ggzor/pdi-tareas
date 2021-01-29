package componentes;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * VentanaDialogo
 */
public class VentanaDialogo extends JDialog {
  public VentanaDialogo(JFrame padre, boolean modal) {
    super(padre, modal);
  }

  public void cuandoAbra(Runnable action) {
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowOpened(WindowEvent e) {
        action.run();
      }
    });
  }

  public void centrarEnEscritorio() {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    setSize(new Dimension(screen.width / 2, screen.height / 2));
    setLocationRelativeTo(null);
    setTitle("Umbralización");
  }
}
