package reactive;

import java.util.*;
import java.util.function.*;

/**
 * Un valor que puede cambiar con el tiempo y al que se pueden agregar observadores
 */
public class ReactiveValue<T> {
  private Optional<T> value;
  private ArrayList<Consumer<Optional<T>>> listeners = new ArrayList<>();

  public ReactiveValue() {
    value = Optional.empty();
  }

  public ReactiveValue(T initial) {
    value = Optional.of(initial);
  }

  public ReactiveValue(Optional<T> optionalValue) {
    value = optionalValue;
  }

  // Setters imperativos
  public void setOpt(Optional<T> newValue) {
    value = newValue;
    listeners.forEach(c -> c.accept(newValue));
  }

  public void set(T newValue) {
    setOpt(Optional.of(newValue));
  }

  // Getters imperativos
  /**
   * Inseguro: Puede devolver null si no se ha establecido el valor inical
   * */
  public T get() {
    return value.orElse(null);
  }

  public Optional<T> getOpt() {
    return value.map(id -> id);
  }

  // Suscripciones
  public void subscribeOpt(Consumer<Optional<T>> listener) {
    if (listener == null)
      throw new IllegalArgumentException("Escuchador nulo");

    listeners.add(listener);
  }

  public void subscribe(Consumer<T> listener) {
    if (listener == null)
      throw new IllegalArgumentException("Escuchador nulo");

    subscribeOpt(opt ->
      opt.map(value -> {
        listener.accept(value);
        return null;
      })
    );
  }

  // Suscripciones con recepción del primer valor de inmediato
  /**
   * Inseguro: Puede pasar null cuando no se ha establecido el valor inicial
   * */
  public void subscribeRun(Consumer<T> listener) {
    subscribe(listener);
    listener.accept(get());
  }

  public void subscribeRunOpt(Consumer<Optional<T>> listener) {
    subscribeOpt(listener);
    listener.accept(value);
  }

  // Operaciones para derivar nuevos valores reactivos a partir de otros
  public <R> ReactiveValue<R> mapOpt(Function<Optional<T>, R> f) {
    ReactiveValue<R> newReactiveValue = new ReactiveValue<R>(f.apply(value));
    this.subscribeOpt(newValue ->
        newReactiveValue.set(f.apply(newValue))
    );
    return newReactiveValue;
  }

  public <R> ReactiveValue<R> map(Function<T, R> f) {
    ReactiveValue<R> newReactiveValue = new ReactiveValue<R>(value.map(f));
    this.subscribe(newValue ->
        newReactiveValue.set(f.apply(newValue))
    );
    return newReactiveValue;
  }

  // Propiedad reactiva para verificar cuando es haya establecido un valor
  public ReactiveValue<Boolean> isPresent() {
    return this.mapOpt(Optional::isPresent);
  }
}

