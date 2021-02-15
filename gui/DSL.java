package gui;

import reactive.*;

import javax.swing.*;

/**
 * Clase fachada para el DSL utilizado para crear layouts y componentes
 * de manera declarativa
 */
public class DSL {

  public static ComponentBuilder<JPanel> panel() {
    return with(new JPanel());
  }

  public static ComponentBuilder<JPanel> hpanel() {
    return panel().boxX();
  }

  public static ComponentBuilder<JPanel> vpanel() {
    return panel().boxY();
  }

  public static <T extends AbstractButton> ButtonBuilder<T> with(T component) {
    return new ButtonBuilder<>(component);
  }

  public static <T extends JComponent> ComponentBuilder<T> with(T component) {
    return new ComponentBuilder<>(component);
  }

  public static ComponentBuilder<JLabel> label(String text) {
    return with(new JLabel(text));
  }

  public static ComponentBuilder<JLabel> label(ReactiveValue<String> text) {
    String inicial = text.get();
    if (inicial == null)
      inicial = "";

    JLabel label = new JLabel(inicial);
    text.subscribe(label::setText);

    return with(label);
  }

  public static ButtonBuilder<JButton> button(String text) {
    return with(new JButton(text));
  }

  public static ButtonBuilder<JButton> button(ReactiveValue<String> text) {
    String inicial = text.get();
    if (inicial == null)
      inicial = "";

    JButton button = new JButton(inicial);
    text.subscribe(button::setText);

    return with(button);
  }

  public static JSliderBuilder slider(int min, int max, int value) {
    return new JSliderBuilder(new JSlider()).limits(min, max).value(value);
  }

  public static JComponent gap(int size) {
    return panel().border(size, size, 0, 0).end();
  }

  private DSL() {}
}

