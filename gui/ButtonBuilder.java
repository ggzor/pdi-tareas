package gui;

import javax.swing.*;

/**
 * Clase para agregar características a un botón de algún tipo
 * */
public class ButtonBuilder<T extends AbstractButton>
                      extends AbstractBuilder<ButtonBuilder<T>, T> {
  public ButtonBuilder(T component) {
    super(component);
  }

  public ButtonBuilder<T> onClick(Runnable action) {
    value.addActionListener(ev -> action.run());
    return this;
  }
}
