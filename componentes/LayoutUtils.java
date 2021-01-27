package componentes;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.swing.*;

import utils.ReactiveValue;

/**
 * LayoutUtils
 */
public class LayoutUtils {

  public static enum Align {
    LEFT(true, Component.LEFT_ALIGNMENT);

    public final boolean isX;
    private final float alignment;

    private Align(boolean isX, float alignment) {
      this.isX = isX;
      this.alignment = alignment;
    }

    public void apply(JComponent component) {
      if (this.isX)
        component.setAlignmentX(this.alignment);
      else
        component.setAlignmentY(this.alignment);
    }
  }

  public static abstract class AbstractBuilder<Self extends AbstractBuilder<Self, T>
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

  public static class ComponentBuilder<T extends JComponent>
                        extends AbstractBuilder<ComponentBuilder<T>, T> {
    public ComponentBuilder(T component) {
      super(component);
    }
  }

  public static class ButtonBuilder<T extends AbstractButton>
                        extends AbstractBuilder<ButtonBuilder<T>, T> {
    public ButtonBuilder(T component) {
      super(component);
    }

    public ButtonBuilder<T> onClick(Runnable action) {
      value.addActionListener(ev -> action.run());
      return this;
    }
  }

  public static JComponent vgap(int size) {
    JPanel panel = new JPanel();
    panel.add(Box.createVerticalStrut(size));
    return panel;
  }

  public static ComponentBuilder<JPanel> vpanel() {
    return panel().boxY();
  }

  public static ComponentBuilder<JPanel> panel() {
    return with(new JPanel());
  }

  public static <T extends AbstractButton> ButtonBuilder<T> with(T component) {
    return new ButtonBuilder<>(component);
  }

  public static <T extends JComponent> ComponentBuilder<T> with(T component) {
    return new ComponentBuilder<>(component);
  }

  public static JLabel reactiveLabel(ReactiveValue<String> value) {
    String inicial = value.get();
    if (inicial == null)
      inicial = "";

    JLabel label = new JLabel(inicial);
    value.subscribe(label::setText);

    return label;
  }

  private LayoutUtils() {}
}
