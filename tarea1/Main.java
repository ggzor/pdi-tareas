package tarea1;

import java.awt.*;
import javax.swing.*;

import componentes.*;

public class Main extends JFrame {
  private final JFileChooser selector = new JFileChooser();

  public Main() {
    Container principal = getContentPane(); {
      VisorImagenes visor = new VisorImagenes();
      principal.add(visor.componente, BorderLayout.CENTER);

      JPanel controles = new JPanel(); {
        JButton boton = new JButton("Abrir");
        boton.addActionListener(a -> {
          Utils.abrirImagen(this, selector).<Void>map(resultado -> {
            resultado.match(
              visor.establecerImagen,
              error -> JOptionPane.showMessageDialog(
                this, error.mensaje, "Error", JOptionPane.ERROR_MESSAGE)
            );

            return null;
          });
        });
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);

        controles.add(boton);
      }
      principal.add(controles, BorderLayout.SOUTH);
    }

    setTitle("Tarea 1 - Axel Suárez Polo");
    setSize(400, 500);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);
  }

  public static void main(String[] args) {
    new Main();
  }
}

