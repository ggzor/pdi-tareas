package componentes;
import javax.swing.JPanel;

import java.awt.image.*;
import java.awt.event.*;

import gui.*;
import reactive.ReactiveValue;
import static reactive.ReactiveValueUtils.*;

import static gui.DSL.*;

/**
 * ComparadorImagenes
 */
public class ComparadorImagenes {
  public static ComponentBuilder<JPanel> de(ReactiveValue<BufferedImage> original,
                                            ReactiveValue<BufferedImage> modificada) {
    ReactiveValue<Double> escalaMinima = new ReactiveValue<>(),
                          escalaSlider = new ReactiveValue<>(1.0),
                          escalaFinal =
                            distinctUntilChanged(
                              combineLatest(escalaMinima.asOpt(), escalaSlider,
                                (emin, es) -> emin.map(v -> Math.max(v, es)).orElse(es)
                              )
                            );

    return
      vpanel()
        .add(
          panel().uniformRows()
            .add(
              vpanel()
                .children(Align.LEFT::apply)
                .add(
                  label("Original").alignLeft().end(),
                  VisorImagenes.de(original)
                )
                .end(),
              vpanel().borderTop(10)
                .children(Align.LEFT::apply)
                .add(
                  label("Modificada").alignLeft().end(),
                  with(VisorImagenes.de(modificada))
                    .tap(v -> {
                      v.addComponentListener(new ComponentAdapter() {
                      });
                    })
                    .end()
                )
                .end()
            )
            .end(),
          vpanel().borderTop(10)
            .add(
            )
            .end()
        );
  }
}

