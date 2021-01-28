package visor;

import componentes.*;
import imagenes.*;
import reactive.*;
import utils.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import java.util.function.*;

/**
 * La clase principal para la tarea 1
 * */
public class Main extends JFrame {
  // Constante para medidas que realmente no importan
  private static final int NO_IMPORTA = 10000;

  // Instanciar un componente selector de imágenes con filtro para
  // los tipos de archivo soportados
  private final SelectorImagenes selector = new SelectorImagenes();

  private final ReactiveValue<BufferedImage> imagen = new ReactiveValue<>();

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

      // Agregar menú
      Runnable accionUmbralizar = () ->
        new VentanaUmbralizacion(this, imagen.get()).setVisible(true);
      JMenuBar menubar = new JMenuBar();
      {
        JMenu menu = new JMenu("Operaciones");
        {
          JMenuItem umbralizar = new JMenuItem("Umbralizar");
          imagen.isPresent().subscribeRun(umbralizar::setEnabled);
          umbralizar.addActionListener(ev -> accionUmbralizar.run());
          menu.add(umbralizar);
        }
        menubar.add(menu);
      }
      this.setJMenuBar(menubar);

      Consumer<BufferedImage> actualizarVisor = imagen ->
        visor.establecerImagen.accept(
            ImageUtils.escalar(
              imagen,
              MathUtils.clamp(0.1, 1.0,
                Geom.calcularEscalaAjuste(
                  new Dimension(imagen.getWidth(), imagen.getHeight()),
                  new Dimension(
                    visor.componente.getWidth() - VisorImagenes.BORDE * 2,
                    visor.componente.getHeight() - VisorImagenes.BORDE * 2
                  )
                )
              )
            )
        );

      imagen.subscribe(
          actualizarVisor
          .andThen(histogramas.establecerImagen)
      );

      JPanel controles = new JPanel(); {
        JButton boton = new JButton("Abrir");
        boton.addActionListener(a -> {
          // Manejo de la acción de abrir una imagen
          selector.abrirImagen(this).map(resultado -> {
            resultado.match(
              imagen::set,
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

