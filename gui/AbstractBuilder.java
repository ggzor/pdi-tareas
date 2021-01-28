package gui;

import java.awt.*;
import javax.swing.*;

import java.util.*;
import java.util.function.*;

/**
 * Clase que implementa el patrón builder para crear interfaces de Swing con
 * menor repetición y mayor facilidad
 * */
public abstract class AbstractBuilder<Self extends AbstractBuilder<Self, T>
                                     , T extends JComponent> {
  protected final T value;
  protected final ArrayList<Consumer<JComponent>> hooks = new ArrayList<>();

  public AbstractBuilder(T component) {
    this.value = component;
  }

  public Self boxY() {
    value.setLayout(new BoxLayout(value, BoxLayout.Y_AXIS));
    return self();
  }

  public Self uniformColumns() {
    return uniformGrid(1, 0);
  }

  public Self uniformRows() {
    return uniformGrid(0, 1);
  }

  public Self uniformGrid(int rows, int columns) {
    value.setLayout(new GridLayout(rows, columns));
    return self();
  }

  public Self children(Consumer<JComponent> hook) {
    hooks.add(hook);
    return self();
  }

  public Self alignLeft() {
    value.setAlignmentX(Component.LEFT_ALIGNMENT);
    return self();
  }

  public Self border(int allBorders) {
    return self().border(allBorders, allBorders, allBorders, allBorders);
  }

  public Self border(int top, int left, int bottom, int right) {
    value.setBorder(
      BorderFactory.createEmptyBorder(top, left, bottom, right));

    return self();
  }

  public Self borderTop(int top) {
    Insets original;

    if (value.getBorder() != null)
      original = value.getBorder().getBorderInsets(value);
    else
      original = new Insets(0, 0, 0, 0);

    return border(top, original.left, original.bottom, original.right);
  }

  public Self tap(Consumer<? super T> action) {
    action.accept(value);
    return self();
  }

  public Self add(JComponent ... components) {
    for (JComponent component : components) {
      hooks.forEach(h -> h.accept(component));
      value.add(component);
    }
    return self();
  }

  public T end() { return value; }

  private Self self() {
    return (Self) this;
  }
}
