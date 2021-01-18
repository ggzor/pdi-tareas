package tarea1;

import java.awt.*;
import javax.swing.*;

import componentes.*;

/**
 * La clase principal para la tarea 1
 * */
public class Main extends JFrame {
  // Instanciar un componente selector de imágenes con filtro para
  // los tipos de archivo soportados
  private final SelectorImagenes selector = new SelectorImagenes();

  public Main() {
    // Creación de la interfaz de usuario principal
    Container principal = getContentPane(); {
      VisorImagenes visor = new VisorImagenes();
      principal.add(visor.componente, BorderLayout.CENTER);

      JPanel controles = new JPanel(); {
        JButton boton = new JButton("Abrir");
        boton.addActionListener(a -> {
          // Manejo de la acción de abrir una imagen
          selector.abrirImagen(this).<Void>map(resultado -> {
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

