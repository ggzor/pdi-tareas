package utils;

import java.util.function.Consumer;

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
      throw new IllegalCallerException("Este constructor sólo debe ser llamado con null");
    isRight = true;
    this.right = right;
  }

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
}
