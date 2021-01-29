package componentes;

import gui.*;
import static gui.DSL.*;
import imagenes.*;
import reactive.*;
import static reactive.ReactiveValueUtils.*;
import utils.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import java.util.Optional;
import java.util.function.*;

/**
 * VentanaUmbralizacion
 */
public class VentanaUmbralizacion extends VentanaDialogo {
  public VentanaUmbralizacion(JFrame padre, BufferedImage imagenInicial)  {
    super(padre, true);
    centrarEnEscritorio();
    setTitle("Umbralización");

    // Lógica
    ReactiveValue<Double> escala = new ReactiveValue<>(1.0);
    ReactiveValue<BufferedImage>
      imagen = escala.map(s -> ImageUtils.escalar(imagenInicial, s)),
      imagenBN = imagen.map(im -> OperadoresPunto.blancoNegro(im, ModoBN.TV_GAMMA));

    ReactiveValue<Integer> umbral = new ReactiveValue<>(255 / 2);
    ReactiveValue<Boolean> esManual = new ReactiveValue<>(false),
                           esMonocromatica = new ReactiveValue<>(true),
                           invertir = new ReactiveValue<>(false);

    int umbralOtsu = InfoImagen.calcularUmbralOtsu(imagenBN.get());

    ReactiveValue<Function<BufferedImage, BufferedImage>> funcion =
      combineLatest(esManual, umbral, esMonocromatica, invertir,
        (hacerManual, umbralManual, monocromatica, hacerInversion) -> {
           return bi -> {
             BufferedImage resultado = imagenBN.get();

             int umbralAplicar = hacerManual
                                 ? umbralManual
                                 : umbralOtsu;

             resultado = OperadoresPunto.umbralizar(resultado, umbralAplicar);

             if (hacerInversion)
               resultado = OperadoresPunto.invertir(resultado);

             if (!monocromatica)
               resultado = OperadoresPunto.enmascarar(bi, resultado);

             return resultado;
           };
         });

    ReactiveValue<BufferedImage> imagenModificada =
      ReactiveValueUtils.combineLatest(imagen, funcion, (im, f) -> f.apply(im));


    ReactiveValue<Void> ajustarEscala = new ReactiveValue<>();
    // Para ajustar la escala al abrir la ventana
    Consumer<JComponent> ajustarEscalaAlAbrir =
      comp -> {
        Runnable ajustarImagenes = () -> escala.set(
          Geom.calcularEscalaAjuste(
            new Dimension(imagenInicial.getWidth(), imagenInicial.getHeight()),
            new Dimension(Math.max(50, comp.getWidth() - 2 * VisorImagenes.BORDE - 20),
                          Math.max(50, comp.getHeight() / 2 - 2 * VisorImagenes.BORDE - 50)))
        );

        this.cuandoAbra(ajustarImagenes);
        ajustarEscala.subscribeOpt(v -> ajustarImagenes.run());
      };

    ButtonGroup grupoManual = new ButtonGroup(),
                grupoVisualizacion = new ButtonGroup();

    // Visualización
    setContentPane(
      panel().uniformColumns()
        .add(
          // Visualización de imagenes
          panel().uniformRows()
            .add(
              crearVisorImagen("Original", imagen),
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
                  label(umbral.map(value -> String.format("Umbral: %d", value)))
                    .end(),
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
              with(new JRadioButton(
                    String.format("Automática con Otsu (corte en %d)", umbralOtsu),
                    !esManual.get()))
                .tap(grupoManual::add)
                .onClick(() -> esManual.set(false))
                .end(),
              // Controles de la salida
              with(new JLabel("Resultado:")).borderTop(10).end(),
              with(new JRadioButton("Monocromático", esMonocromatica.get()))
                .tap(grupoVisualizacion::add)
                .onClick(() -> esMonocromatica.set(true))
                .end(),
              with(new JRadioButton("Sobre imagen original", esMonocromatica.get()))
                .tap(grupoVisualizacion::add)
                .onClick(() -> esMonocromatica.set(false))
                .end(),
              with(new JCheckBox("Invertir máscara"))
                .border(10, 0)
                .tap(c -> invertir.subscribeRun(c::setSelected))
                .onClick(() -> invertir.set(!invertir.get()))
                .end(),
              button("Ajustar escala")
                .onClick(() -> ajustarEscala.setOpt(Optional.empty()))
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
