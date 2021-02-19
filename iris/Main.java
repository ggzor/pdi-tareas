package iris;

import java.awt.image.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

import imagenes.Canales;
import imagenes.ImagenesIO;
import imagenes.ModoBN;
import static imagenes.OperadoresPunto.*;

public class Main {
  private static final int[] NEGRO = new int[] { 0, 0, 0 };
  private static final int[] COLOR_FALSO = new int[] { 0, 69, 25 };

  public static BufferedImage procesarImagen(BufferedImage img) {
    BufferedImage mask1, img1, pestanas_finas, pupila, regiones_oscuras,
                  maskRGB, maskHSL, maskFinal, imgFinal;

    mask1 = blancoNegro(img, ModoBN.TV_GAMMA);
    mask1 = brillo(mask1, 53);
    mask1 = contraste(mask1, 0.633);
    mask1 = sigmoide(mask1, 0.1852);
    mask1 = sigmoide(mask1, 0.1852);
    mask1 = invertir(mask1);

    img1 = enmascarar(img, mask1);

    pestanas_finas = diferencias(img1, 117);
    pupila = invertir(diferencias(img1, 0));
    regiones_oscuras = separarRGB(img1, 96);

    maskRGB = diff(mask1, regiones_oscuras);
    maskRGB = diff(maskRGB, pestanas_finas);
    maskRGB = diff(maskRGB, pupila);

    maskHSL = Canales.umbralizacionHSLPorS(img, 0.094f, 1.834f);

    maskFinal = interseccion(maskRGB, maskHSL);
    imgFinal = enmascarar(img, maskFinal);

    return recolorear(imgFinal, NEGRO, COLOR_FALSO);
  }

  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("No se proporcionaron todos los argumentos necesarios.");
      System.out.println("Uso: java iris.Main CARPETA_ORIGEN CARPETA_DESTINO");
      System.out.println();
      System.out.println("  Procesa las imágenes en la carpeta de origen y coloca");
      System.out.println("  los resultados en la carpeta destino");

      System.exit(1);
    }

    Path origen = Paths.get(args[0]);
    Path destino = Paths.get(args[1]);

    if (!Files.isDirectory(origen)) {
      exitError("La carpeta de origen no es un directorio o no existe: "
               + origen.toAbsolutePath());
    }

    if (Files.exists(destino) && !Files.isDirectory(destino)) {
      exitError("La carpeta de destino existe pero no es un directorio: "
               + destino.toAbsolutePath());
    }

    if (!Files.exists(destino)) {
      try {
        Files.createDirectory(destino);
      } catch (IOException ex) {
        exitError("No se pudo crear la carpeta destino.");
      }
    }

    try {

    Stream<Path> imagenes = Files.list(origen)
                                 .filter(p -> p.toString().endsWith(".jpg")
                                         || p.toString().endsWith(".JPG"));

    imagenes.parallel().forEach(p ->
        ImagenesIO.abrirImagen(p.toAbsolutePath().toFile())
          .match(
            img ->
              ImagenesIO.guardarImagen(
                   procesarImagen(img),
                   Paths.get(destino.toAbsolutePath().toString(),
                             p.getFileName().toString())
                          .toAbsolutePath()
                          .toFile()
                )
                .match(
                  ok -> System.out.println("Imagen procesada: "
                                           + p.getFileName()),
                  err -> System.err.println("Error al guardar: "
                                           + p.getFileName())),
            err ->
              System.err.println("Ocurrió un error al leer el archivo:"
                                 + p.toString())
          )
    );

    } catch (IOException ex) {
      exitError("Ocurrió un error al procesar las imágenes.");
    }
  }

  private static <T> T exitError(String mensaje) {
    System.err.println(mensaje);
    System.exit(1);
    return null;
  }
}

