package componentes;

import static componentes.LayoutUtils.*;
import imagenes.*;
import utils.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import java.util.function.*;

/**
 * VentanaUmbralizacion
 */
public class VentanaUmbralizacion extends VentanaDialogo {
  public VentanaUmbralizacion(JFrame padre, BufferedImage imagen)  {
    super(padre, true);
    centrarEnEscritorio();
    setTitle("Umbralización");

    // Lógica
    ReactiveValue<Integer> umbral = new ReactiveValue<>(255 / 2);
    ReactiveValue<Boolean> esManual = new ReactiveValue<>(true),
                           noEsManual = esManual.map(b -> !b),
                           esMonocromatica = new ReactiveValue<>(true);

    ReactiveValue<Function<BufferedImage, BufferedImage>> funcion =
      ReactiveValueUtils.combineLatest(esManual, umbral)
                        .map(p -> {
                          boolean hacerManual = p.primero;
                          int umbralManual = p.segundo;

                          return bi -> hacerManual
                                  ? Operaciones.escalar(bi, 1.0 + (double)umbralManual / 255.0)
                                  : Operaciones.escalar(bi, 0.5);
                        });

    ReactiveValue<Double> escala = new ReactiveValue<>(1.0);
    ReactiveValue<BufferedImage> imagenOriginal = escala.map(s -> Operaciones.escalar(imagen, s));

    ReactiveValue<BufferedImage> imagenModificada =
      ReactiveValueUtils.combineLatest(imagenOriginal, funcion)
                        .map(p -> p.segundo.apply(p.primero));


    // Para ajustar la escala al abrir la ventana
    Consumer<JComponent> ajustarEscalaAlAbrir =
      comp -> this.cuandoAbra(() -> escala.set(
        Geom.calcularEscalaAjuste(
          new Dimension(imagen.getWidth(), imagen.getHeight()),
          new Dimension(Math.max(50, comp.getWidth() - 2 * VisorImagenes.BORDE - 20),
                        Integer.MAX_VALUE))
    ));

    ButtonGroup grupoManual = new ButtonGroup(),
                grupoVisualizacion = new ButtonGroup();

    // Visualización
    setContentPane(
      panel().uniformColumns()
        .add(
          // Visualización de imagenes
          panel().uniformRows()
            .add(
              crearVisorImagen("Original", imagenOriginal),
              crearVisorImagen("Modificada", imagenModificada)
            )
            .tap(ajustarEscalaAlAbrir)
            .end(),

          // Controles
          vpanel().border(20)
            .children(Align.LEFT::apply)
            .add(
              new JLabel("Umbralización"),
              // Controles para manual
              with(new JRadioButton("Manual", esManual.get()))
                .tap(grupoManual::add)
                .onClick(() -> esManual.set(true))
                .end(),
              vpanel().border(10, 20, 10, 10)
                .children(Align.LEFT::apply)
                .add(
                  reactiveLabel(
                    umbral.map(value -> String.format("Umbral: %d", value))
                  ),
                  with(new JSlider(JSlider.HORIZONTAL, 0, 255, umbral.get()))
                    .tap(s -> {
                      s.setMajorTickSpacing(50);
                      s.setPaintTicks(true);
                      s.setPaintLabels(true);

                      esManual.subscribeRun(s::setEnabled);
                      s.addChangeListener(ev -> umbral.set(s.getValue()));
                    })
                    .end()
                )
                .end(),
              // Controles para Otsu
              with(new JRadioButton("Automática con Otsu", !esManual.get()))
                .tap(grupoManual::add)
                .onClick(() -> esManual.set(false))
                .end(),
              // Controles de la salida
              new JLabel("Resultado:"),
              with(new JRadioButton("Monocromático", esMonocromatica.get()))
                .tap(grupoVisualizacion::add)
                .end()
            )
            .end()
        )
        .end()
    );
  }

  private JComponent crearVisorImagen(String titulo, ReactiveValue<BufferedImage> imagen) {
    VisorImagenes visor = new VisorImagenes();
    imagen.subscribeRun(visor.establecerImagen);

    return
      vpanel().border(5)
        .children(Align.LEFT::apply)
        .add(
          new JLabel(titulo),
          visor.componente
        )
        .end();
  }
}
