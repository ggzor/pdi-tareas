package script;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import javax.script.*;
import javax.swing.*;

import componentes.*;
import gui.Align;
import imagenes.ImageUtils;
import imagenes.ImagenesIO;

import static gui.DSL.*;
import reactive.*;
import utils.Either;
import utils.Geom;

import static reactive.ReactiveValueUtils.*;

/**
 * Main
 */
public class Main extends JFrame {
  private static final String SCRIPT_FILE = "script.js";
  private static final String BUILTINS_FILE = "builtins.js";
  private static final String VARS_FILE = "vars.txt";
  private static final String IMAGEN_FILE = "resources/IMG_001_L_3.JPG";

  private JComponent comp;

  private static String leerArchivo(String archivo) {
    try {
      return new String(Files.readAllBytes(Paths.get(archivo)));
    } catch (IOException ex) {
      return "";
    }
  }

  private static void escribirArchivo(String archivo, String contenido) {
    try {
      Files.write(Paths.get(archivo), contenido.getBytes());
    } catch (IOException ex) {
      System.err.println("No se pudo escribir el archivo: " + archivo);
    }
  }

  private static String BUILTINS = leerArchivo(BUILTINS_FILE);
  private static String codigoInicial = leerArchivo(SCRIPT_FILE);

  public Main() throws Exception {
    BufferedImage todasImagenes[] =
      Files.list(Paths.get("resources/"))
           .filter(p -> p.toString().endsWith("JPG"))
           .map(p ->
               ImagenesIO.abrirImagen(p.toAbsolutePath().toFile())
                         .either(im -> im, err -> null))
           .toArray(BufferedImage[]::new);

    ReactiveValue<Integer> indiceImagen = new ReactiveValue<>(0);
    Consumer<Integer> modificarIndice = i -> {
      indiceImagen.set((indiceImagen.get() + i + todasImagenes.length) % todasImagenes.length);
    };

    ReactiveValue<Boolean> recargar = new ReactiveValue<>(false);
    ReactiveValue<BufferedImage> imagen = indiceImagen.map(i -> todasImagenes[i]);

    ReactiveValue<BufferedImage> original = combineLatest(imagen, recargar, (img, r) -> {
      if (comp != null) {
        double escala = Geom.calcularEscalaAjuste(
          new Dimension(img.getWidth(), img.getHeight()),
          new Dimension(Math.max(50, comp.getWidth() - 2 * VisorImagenes.BORDE - 20),
                        Math.max(50, comp.getHeight() / 2 - 2 * VisorImagenes.BORDE - 50)));
        return ImageUtils.escalar(img, escala);
      } else {
        return img;
      }
    });

    ReactiveValue<BufferedImage> modificada = new ReactiveValue<>();

    ScriptEngineManager factory = new ScriptEngineManager();
    ScriptEngine engine = factory.getEngineByName("nashorn");

    ReactiveValue<Optional<String>> error = new ReactiveValue<>(Optional.empty());
    ReactiveValue<String> codigo = new ReactiveValue<>(codigoInicial);
    Timer t = new Timer(200, ev -> {
      String nuevoCodigo = leerArchivo(SCRIPT_FILE);
      if (!nuevoCodigo.equals(codigo.get())) {
        codigo.set(nuevoCodigo);
      }
    });
    t.setRepeats(true);
    t.start();

    // Bindings de variables
    HashMap<Character, Integer> valores = new HashMap<>();
    for (char i = 'a'; i <= 'z'; i++)
      valores.put(i, 0);
    if (Files.exists(Paths.get(VARS_FILE))) {
      Files.lines(Paths.get(VARS_FILE))
           .map(String::trim)
           .filter(s -> s.length() > 0)
           .forEach(l -> {
             String comps[] = l.split(" ");
             if (comps.length == 2 && comps[0].length() == 1) {
               try {
                valores.put(comps[0].charAt(0), Integer.parseInt(comps[1]));
               } catch(NumberFormatException ex) {
                System.err.printf("Variable inválida: %c = %s\n", comps[0], comps[1]);
               }
             }
           });
    }

    ReactiveValue<Boolean> valorCambiado = new ReactiveValue<>(false);
    debounce(400, valorCambiado).subscribe(vc -> {
      escribirArchivo(
          VARS_FILE,
          valores.keySet()
            .stream()
            .filter(c -> valores.get(c) != 0)
            .map(c -> String.format("%c %d", c, valores.get(c)))
            .collect(Collectors.joining("\n")));
    });

    combineLatest(codigo, original, valorCambiado, (c, img, vc) -> {
      Either<String, BufferedImage> result;

      Bindings bindings = engine.createBindings();
      engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

      bindings.put("$imagen", img);
      bindings.put("applyBinding", new BiConsumer<String,Object>(){
        @Override
        public void accept(String t, Object u) {
          bindings.put(t, u);
        }
      });

      for (char k : valores.keySet())
        bindings.put(String.format("$%c", k), (double)valores.get(k) / 1000.0);

      try {
        Object resultado = engine.eval(BUILTINS + '\n' + c);
        if (resultado instanceof BufferedImage) {
          result = Either.right((BufferedImage)resultado);
        } else {
          result = Either.left("No se devolvió una imagen en el script");
          System.out.println(resultado);
        }
      } catch (ScriptException ex) {
        result = Either.left("Ocurrió un error en el script");
        System.err.println(ex.getMessage());
      } catch (Exception ex) {
        result = Either.left("Ocurrió otro tipo de error - " + ex.getClass().getName());
        System.err.println(ex.getMessage());
      }

      return result;
    }).subscribe(result ->
      result.match(
        img -> {
          modificada.set(img);
          error.set(Optional.empty());
        },
        err -> error.set(Optional.of(err))
      ));

    setContentPane(
      panel().uniformColumns().border(10)
        .add(
          ComparadorImagenes
            .de(original, modificada)
            .tap(p -> comp = p)
            .end(),
          vpanel().border(10)
            .children(Align.LEFT::apply)
            .add(
              label(error.map(e -> e.orElse("Script correcto")))
                .end(),
              vpanel()
                .children(Align.LEFT::apply)
                .add(
                  hpanel()
                    .add(
                      button("Recargar imagen")
                        .onClick(() -> recargar.set(true))
                        .end(),
                      button("Anterior imagen")
                        .onClick(() -> modificarIndice.accept(-1))
                        .end(),
                      button("Siguiente imagen")
                        .onClick(() -> modificarIndice.accept(+1))
                        .end()
                    )
                    .end(),
                  vpanel()
                    .add(
                      valores.keySet().stream()
                        .map(c ->
                          vpanel()
                            .children(Align.LEFT::apply)
                            .add(
                              label(String.format("Variable $%c", c)).end(),
                              slider(0, 1000, valores.get(c))
                                .onChange(nv -> {
                                  valores.put(c, nv);
                                  valorCambiado.set(true);
                                })
                                .end()
                            )
                            .end()
                        )
                        .toArray(JComponent[]::new)
                    )
                    .end()
                )
                .scrollable()
                .end()
            )
            .end()
        )
        .end()
    );

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowOpened(WindowEvent e) {
        recargar.set(true);
      }
    });

    CompUtils.centrarEscritorio(this, w -> w / 3, h -> h / 2);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
	}

  public static void main(String[] args) throws Exception {
    new Main();
  }
}
