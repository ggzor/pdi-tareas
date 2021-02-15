package gui;

import java.awt.*;
import javax.swing.*;

import reactive.ReactiveValue;

import java.util.*;
import java.util.function.*;

/**
 * Clase que implementa el patrón builder para crear interfaces de Swing con
 * menor repetición y mayor facilidad
 * */
public abstract class AbstractBuilder<Self extends AbstractBuilder<Self, T>
                                     , T extends JComponent> {

  public static final int INCREMENTO_SCROLL = 10;

  protected final T value;
  protected final ArrayList<Consumer<JComponent>> hooks = new ArrayList<>();

  public AbstractBuilder(T component) {
    this.value = component;
  }

  public Self pwidth(int width) {
    value.setPreferredSize(new Dimension(width, value.getPreferredSize().height));
    return self();
  }

  public Self pheight(int height) {
    value.setPreferredSize(new Dimension(value.getPreferredSize().width, height));
    return self();
  }

  public ComponentBuilder<JScrollPane> scrollable() {
    JScrollPane scroller = new JScrollPane();
    scroller.setViewportView(value);

    // Ajustar desplazamiento
    scroller.getVerticalScrollBar().setUnitIncrement(INCREMENTO_SCROLL);
    scroller.getHorizontalScrollBar().setUnitIncrement(INCREMENTO_SCROLL);

    return DSL.with(scroller);
  }

  public Self addSouth(JComponent component) {
    value.add(component, BorderLayout.SOUTH);
    return self();
  }

  public Self addEast(JComponent component) {
    value.add(component, BorderLayout.EAST);
    return self();
  }

  public Self addCenter(JComponent component) {
    value.add(component, BorderLayout.CENTER);
    return self();
  }

  public Self visibleWhen(ReactiveValue<Boolean> visible) {
    visible.subscribeRunOpt(opt -> value.setVisible(opt.orElse(true)));
    return self();
  }

  public Self gridBag() {
    value.setLayout(new GridBagLayout());
    return self();
  }

  public Self borderLayout() {
    value.setLayout(new BorderLayout());
    return self();
  }

  public Self boxX() {
    value.setLayout(new BoxLayout(value, BoxLayout.X_AXIS));
    return self();
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

  public Self alignCenterX() {
    value.setAlignmentX(Component.CENTER_ALIGNMENT);
    return self();
  }

  public Self alignLeft() {
    value.setAlignmentX(Component.LEFT_ALIGNMENT);
    return self();
  }

  public Self border(int allBorders) {
    return self().border(allBorders, allBorders, allBorders, allBorders);
  }

  public Self border(int vertical, int horizontal) {
    return self().border(vertical, horizontal, vertical, horizontal);
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
