package utils;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Tipo de datos algebraico para representar errores en funciones o
 * métodos sin la necesidad de utilizar excepciones
 *
 * Básicamente es una clase para permitir dos tipos distintos como valor
 * de retorno, pero de forma mutuamente exclusiva.
 * */
public final class Either<L, R> {
  private final boolean isRight;
  private L left = null;
  private R right = null;

  private Either(L left) {
    isRight = false;
    this.left = left;
  }

  private Either(R right, Object tag) {
    if (tag != null)
      throw new IllegalArgumentException("Este constructor sólo debe ser llamado con null");
    isRight = true;
    this.right = right;
  }

  /**
   * La única forma de acceder a los valores internos es utilizando esta
   * función, manejando ambos casos
   * */
  public void match(Consumer<R> rightAction, Consumer<L> leftAction) {
    if (isRight)
      rightAction.accept(right);
    else
      leftAction.accept(left);
  }

  public static <L, R> Either<L, R> left(L left) {
    return new Either<L, R>(left);
  }

  public static <L, R> Either<L, R> right(R right) {
    return new Either<L, R>(right, null);
  }

  public <T> T either(Function<R, T> r, Function<L, T> l) {
    if (isRight)
      return r.apply(right);
    else
      return l.apply(left);
  }
}
