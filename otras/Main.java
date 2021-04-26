package otras;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import static java.util.stream.Collectors.*;

import imagenes.ImagenesIO;
import imagenes.OperadoresGenerales;

/**
 * Main
 */
public class Main {

  public static BufferedImage unsafeInvoke(Method m, Object args[]) {
    try {
      return (BufferedImage) m.invoke(null, args);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
      return exitError("Ocurrió un error en la invocación de la operación: " + m.getName());
    }
  }

  private static Object[] parseArgs(Method m, String[] args) {
    Object[] result = new Object[args.length];

    Parameter[] params = m.getParameters();
    for (int i = 0; i < args.length; i++) {
      Parameter p = params[i];
      String arg = args[i];

      if (p.getType().equals(BufferedImage.class)) {
        result[i] = ImagenesIO.abrirImagen(new File(arg)).either(bi -> bi,
            err -> exitError(String.format("No se pudo abrir la imagen: %s", arg)));
      } else if (p.getType().equals(double.class)) {
        try {
          result[i] = Double.parseDouble(arg);

        } catch (NumberFormatException ex) {
          exitError(String.format("No se pudo leer el número de punto flotante: %s", arg));
        }
      } else if (p.getType().equals(int.class)) {
        try {
          result[i] = Integer.parseInt(arg);

        } catch (NumberFormatException ex) {
          exitError(String.format("No se pudo leer el entero: %s", arg));
        }
      }
    }

    return result;
  }

  private static Map<String, Parameter[]> params = new HashMap<>();
  private static Map<String, Function<String[], BufferedImage>> operaciones = Arrays
      .stream(OperadoresGenerales.class.getDeclaredMethods())
      .filter(m -> Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())).map(m -> {
        params.put(m.getName(), m.getParameters());
        return m;
      }).collect(toMap(m -> m.getName(), m -> margs -> unsafeInvoke(m, parseArgs(m, margs))));

  private static String nombreLegible(Class<?> c) {
    if (BufferedImage.class.equals(c))
      return "Imagen";
    String[] comps = c.getName().split("\\.");
    return comps[comps.length - 1];
  }

  private static void imprimirUso() {
    System.out.println("No se proporcionaron todos los argumentos necesarios.");
    System.out.println("Uso: java otras.Main OPERACION SALIDA ...PARAMETROS");
    System.out.println();
    System.out.println("  Realiza el procesamiento con los parámetros dados");
    System.out.println();
    System.out.println("Operaciones disponibles:");
    for (String m : operaciones.keySet()) {
      System.out.printf("  - %s(%s)\n", m, Arrays.stream(params.get(m))
          .map(p -> String.format("%s %s", nombreLegible(p.getType()), p.getName())).collect(joining(", ")));
    }
    System.out.println();

    System.exit(1);
  }

  public static void main(String[] args) {
    if (args.length < 1)
      imprimirUso();

    String operador = args[0];
    if (!params.containsKey(operador))
      exitError(String.format("No existe el operador %s", args[0]));

    if (params.get(operador).length + 1 != args.length - 1)
      exitError(String.format("Se proporcionaron %d parametros pero se esperaban %d", args.length - 1,
          params.get(operador).length + 1));

    String salida = args[1];
    BufferedImage resultado = operaciones.get(operador).apply(Arrays.stream(args).skip(2).toArray(String[]::new));

    ImagenesIO.guardarImagen(resultado, new File(salida));
  }

  private static <T> T exitError(String mensaje) {
    System.err.println(mensaje);
    System.exit(1);
    return null;
  }
}
