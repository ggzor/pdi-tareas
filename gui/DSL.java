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

  public static ComponentBuilder<JPanel> vpanel() {
    return panel().boxY();
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

  private DSL() {}
}

