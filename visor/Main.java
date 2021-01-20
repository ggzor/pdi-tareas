package visor;

import java.awt.*;
import javax.swing.*;

import componentes.*;

/**
 * La clase principal para la tarea 1
 * */
public class Main extends JFrame {
  // Constante para medidas que realmente no importan
  private static final int NO_IMPORTA = 10000;

  // Instanciar un componente selector de imágenes con filtro para
  // los tipos de archivo soportados
  private final SelectorImagenes selector = new SelectorImagenes();

  public Main() {
    // Creación de la interfaz de usuario principal
    Container principal = getContentPane(); {
      VisorImagenes visor = new VisorImagenes();
      principal.add(visor.componente, BorderLayout.CENTER);

      // Agregar el panel de la derecha con la información de las imágenes
      PanelVertical panelDerecha = new PanelVertical(true);
      VisorHistograma histogramas = new VisorHistograma(new Dimension(450, 200));

      // Ajustar tamaños para evitar que no se muestren los componentes
      panelDerecha.componente.setPreferredSize(new Dimension(500, NO_IMPORTA));
      histogramas.componente.setMaximumSize(new Dimension(NO_IMPORTA, 700));

      panelDerecha.agregarComponente.accept(histogramas.componente);
      principal.add(panelDerecha.componente, BorderLayout.EAST);

      JPanel controles = new JPanel(); {
        JButton boton = new JButton("Abrir");
        boton.addActionListener(a -> {
          // Manejo de la acción de abrir una imagen
          selector.abrirImagen(this).<Void>map(resultado -> {
            resultado.match(
              visor.establecerImagen
                   .andThen(histogramas.establecerImagen),
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
    setSize(800, 600);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);
    setExtendedState(JFrame.MAXIMIZED_BOTH);
  }

  public static void main(String[] args) {
    new Main();
  }
}

