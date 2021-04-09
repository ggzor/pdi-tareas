package regionales;

import java.awt.image.*;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import static java.util.stream.Collectors.*;

import imagenes.ImagenesIO;
import imagenes.OperadoresConvolucionales;

public class Main {

  public static BufferedImage unsafeInvoke(Method m, BufferedImage bi) {
    try {
      return (BufferedImage) m.invoke(null, bi);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
      return exitError("Ocurrió un error en la invocación de la operación: " + m.getName());
    }
  }

  public static void main(String[] args) {
    Map<String, Function<BufferedImage, BufferedImage>> operaciones = Arrays
        .stream(OperadoresConvolucionales.class.getDeclaredMethods()).filter(m -> !m.getName().startsWith("aplicar"))
        .filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> {
          Parameter[] params = m.getParameters();
          return params.length == 1 && params[0].getType().equals(BufferedImage.class)
              && m.getReturnType().equals(BufferedImage.class);
        }).collect(toMap(m -> m.getName(), m -> bi -> unsafeInvoke(m, bi)));

    if (args.length != 3) {
      System.out.println("No se proporcionaron todos los argumentos necesarios.");
      System.out.println("Uso: java regionales.Main OPERACION CARPETA_ORIGEN CARPETA_DESTINO");
      System.out.println();
      System.out.println("  Procesa las imágenes en la carpeta de origen y coloca");
      System.out.println("  los resultados en la carpeta destino");
      System.out.println();
      System.out.println("Operaciones disponibles:");
      for (String m : operaciones.keySet()) {
        System.out.printf("  - %s\n", m);
      }
      System.out.println();

      System.exit(1);
    }

    String operacion = args[0];
    Path origen = Paths.get(args[1]);
    Path destino = Paths.get(args[2]);

    if (!operaciones.containsKey(operacion)) {
      exitError(String.format("La operación '%s' no se encuentra en la lista de operaciones disponibles", operacion));
    }

    if (origen.toAbsolutePath().toString().equals(destino.toAbsolutePath().toString())) {
      exitError("La carpeta de destino debe ser diferente a la de origen.");
    }

    if (!Files.isDirectory(origen)) {
      exitError("La carpeta de origen no es un directorio o no existe: " + origen.toAbsolutePath());
    }

    if (Files.exists(destino) && !Files.isDirectory(destino)) {
      exitError("La carpeta de destino existe pero no es un directorio: " + destino.toAbsolutePath());
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
          .filter(p -> p.toString().endsWith(".jpg") || p.toString().endsWith(".JPG"));

      imagenes.parallel()
          .forEach(
              p -> ImagenesIO.abrirImagen(p.toAbsolutePath().toFile())
                  .match(
                      img -> ImagenesIO
                          .guardarImagen(operaciones.get(operacion).apply(img),
                              Paths.get(destino.toAbsolutePath().toString(),
                                  operacion + "-" + p.getFileName().toString()).toAbsolutePath().toFile())
                          .match(ok -> System.out.println("Imagen procesada: " + p.getFileName()),
                              err -> System.err.println("Error al guardar: " + p.getFileName())),
                      err -> System.err.println("Ocurrió un error al leer el archivo:" + p.toString())));

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
