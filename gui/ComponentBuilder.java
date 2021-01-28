package gui;

import javax.swing.*;

/**
 * Clase que sólo sirve para concretizar el builder abstracto
 * */
public class ComponentBuilder<T extends JComponent>
                      extends AbstractBuilder<ComponentBuilder<T>, T> {
  public ComponentBuilder(T component) {
    super(component);
  }
}
