package reactive;

import utils.*;

import java.util.*;
import java.util.function.*;

import javax.swing.Timer;

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

  public static <A, B, R> ReactiveValue<R> combineLatest(ReactiveValue<A> a, ReactiveValue<B> b, BiFunction<A, B, R> mapper) {
    Optional<R> initial = a.getOpt().flatMap(av -> b.getOpt().map(bv -> mapper.apply(av, bv)));

    ReactiveValue<R> result = new ReactiveValue<>(initial);

    a.subscribeOpt(
        optA -> optA.flatMap(av -> b.getOpt().map(bv -> mapper.apply(av, bv)))
                    .ifPresent(result::set));

    b.subscribeOpt(
        optB -> optB.flatMap(bv -> a.getOpt().map(av -> mapper.apply(av, bv)))
                    .ifPresent(result::set));

    return result;
  }

  @FunctionalInterface
  public static interface TriFunction<A, B, C, R> {
    public R apply(A a, B b, C c);
  }

  public static <A, B, C, R> ReactiveValue<R>
    combineLatest(
        ReactiveValue<A> a,
        ReactiveValue<B> b,
        ReactiveValue<C> c,
        TriFunction<A, B, C, R> mapper) {

    ReactiveValue<Function<C, R>> fs =
      combineLatest(a, b, (av, bv) -> cv -> mapper.apply(av, bv, cv));

    return combineLatest(c, fs, (cv, f) -> f.apply(cv));
  }

  @FunctionalInterface
  public static interface FourFunction<A, B, C, D, R> {
    public R apply(A a, B b, C c, D d);
  }

  public static <A, B, C, D, R> ReactiveValue<R>
    combineLatest(
        ReactiveValue<A> a,
        ReactiveValue<B> b,
        ReactiveValue<C> c,
        ReactiveValue<D> d,
        FourFunction<A, B, C, D, R> mapper) {

    ReactiveValue<Function<D, R>> fs =
      combineLatest(a, b, c, (av, bv, cv) -> dv -> mapper.apply(av, bv, cv, dv));

    return combineLatest(d, fs, (dv, f) -> f.apply(dv));
  }

  private ReactiveValueUtils() {}

  public static ReactiveValue<Boolean> and(ReactiveValue<Boolean> a, ReactiveValue<Boolean> b) {
    return combineLatest(a, b).map(bs -> bs.primero && bs.segundo);
  }

  private static class DistinctReactiveValue<T> extends ReactiveValue<T> {
    private Optional<T> previous = Optional.empty();
    public DistinctReactiveValue(ReactiveValue<T> base) {
      base.subscribeOpt(v -> {
        v.flatMap(vnew ->
            previous.map(vprevious ->
              Objects.equals(vnew, vprevious)
                ? Optional.<T>empty()
                : Optional.of(vnew)
            )
              .orElse(Optional.of(vnew))
        ).ifPresent(vnew -> {
          previous = Optional.of(vnew);
          set(vnew);
        });
      });
    }
  }

  public static <T> ReactiveValue<T> distinctUntilChanged(ReactiveValue<T> value) {
    return new DistinctReactiveValue<T>(value);
  }

  public static <T> ReactiveValue<T> debounce(int milis, ReactiveValue<T> value) {
    ReactiveValue<T> result = new ReactiveValue<>();
    Stack<T> box = new Stack<>();

    Timer t = new Timer(milis, ev -> {
      if (!box.isEmpty()) {
        result.set(box.pop());
      }
    });

    value.subscribeRunOpt(opt -> {
      if (opt.isPresent()) {
        t.stop();

        box.clear();
        box.push(opt.get());

        t.start();
      }
    });

    return result;
  }
}
