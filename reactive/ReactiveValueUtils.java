package reactive;

import utils.*;

import java.util.*;

/**
 * ReactiveValueUtils
 */
public class ReactiveValueUtils {

  public static <A, B> ReactiveValue<Par<A, B>> combineLatest(ReactiveValue<A> a, ReactiveValue<B> b) {
    Optional<Par<A, B>> current =
      a.getOpt().flatMap(avalue ->
          b.getOpt().map(bvalue -> Par.de(avalue, bvalue)));

    ReactiveValue<Par<A, B>> result = new ReactiveValue<>(current);

    a.subscribe(avalue ->
        b.getOpt().map(bvalue -> Par.de(avalue, bvalue))
                  .ifPresent(result::set));

    b.subscribe(bvalue ->
        a.getOpt().map(avalue -> Par.de(avalue, bvalue))
                  .ifPresent(result::set));

    return result;
  }

  private ReactiveValueUtils() {}

  public static ReactiveValue<Boolean> and(ReactiveValue<Boolean> a, ReactiveValue<Boolean> b) {
    return combineLatest(a, b).map(bs -> bs.primero && bs.segundo);
  }
}
