package visor;

import componentes.*;
import static gui.DSL.*;
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
  // Instanciar un componente selector de imágenes con filtro para
  // los tipos de archivo soportados
  private final SelectorImagenes selector = new SelectorImagenes();


  public Main() {
    ReactiveValue<BufferedImage> imagen = new ReactiveValue<>();

    // Creación de la interfaz de usuario principal
    VisorImagenes visor = new VisorImagenes();
    VisorHistograma histogramas = new VisorHistograma(new Dimension(450, 200));

    // Actualizar el visor centrando la imagen
    Consumer<BufferedImage> actualizarVisor = nuevaImagen ->
      visor.establecerImagen.accept(
          ImageUtils.escalar(
            nuevaImagen,
            MathUtils.clamp(0.1, 1.0,
              Geom.calcularEscalaAjuste(
                new Dimension(nuevaImagen.getWidth(), nuevaImagen.getHeight()),
                new Dimension(
                  visor.componente.getWidth() - VisorImagenes.BORDE * 2,
                  visor.componente.getHeight() - VisorImagenes.BORDE * 2
                )
              )
            )
          )
      );

    // Unión de la lógica ya los componentes
    imagen.subscribe(
        actualizarVisor
        .andThen(histogramas.establecerImagen)
    );

    setContentPane(
        panel().borderLayout()
          .addCenter(visor.componente)
          .addEast(
            vpanel().pwidth(500)
              .add(with(histogramas.componente).pheight(700).end())
              .scrollable()
              .end()
          )
          .addSouth(
            panel()
              .add(
                with(new JButton("Abrir"))
                  .onClick(() -> {
                    // Manejo de la acción de abrir una imagen
                    selector.abrirImagen(this).map(resultado -> {
                      resultado.match(
                        imagen::set,
                        error -> JOptionPane.showMessageDialog(
                          this, error.mensaje, "Error", JOptionPane.ERROR_MESSAGE)
                      );

                      return null;
                    });
                  })
                  .alignCenterX()
                  .end()
              )
              .end()
          )
          .end()
    );

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


    // Mostrar la ventana
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

