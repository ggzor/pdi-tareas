package utils;

// Una clase de utilería para representar una
// tupla de dos elementos en Java
public class Par<A, B> {
  public final A primero;
  public final B segundo;

  public Par(A primero, B segundo) {
    this.primero = primero;
    this.segundo = segundo;
  }

  // Constructor inteligente para no tener que escribir los parámetros
  // genéricos cada vez
  public static <A, B> Par<A, B> de(A primero, B segundo) {
    return new Par<A, B>(primero, segundo);
  }
}
