package componentes;

import static gui.DSL.*;

import java.awt.image.*;
import javax.swing.*;

import reactive.ReactiveValue;

import java.util.function.*;

/**
 * Componente para visualizar una imagen y permitir su actualización
 */
public class VisorImagenes {
  public static final int BORDE = 16;

  // Función para establecer la imagen
  public Consumer<BufferedImage> establecerImagen;
  // El componente que se debe agregar para mostrar la imagen
  public final JComponent componente;

  public VisorImagenes() {
    componente =
      panel()
        .add(
          panel().border(BORDE)
            .add(
              with(new JLabel())
                .tap(l -> {
                  establecerImagen = imagen -> l.setIcon(new ImageIcon(imagen));
                })
                .end()
            )
            .end()
        )
        .gridBag()
        .scrollable()
        .end();
  }

  public static JComponent de(ReactiveValue<BufferedImage> imagen) {
    return
      panel()
        .add(
          panel().border(BORDE)
            .add(with(new JLabel())
                   .tap(l -> imagen.subscribeRunOpt(
                          opt -> opt.ifPresent(
                            im -> l.setIcon(new ImageIcon(im)))))
                   .end())
            .end()
        )
        .gridBag()
        .scrollable()
        .end();
  }
}

