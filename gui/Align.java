package gui;

import java.awt.*;
import javax.swing.*;

/**
 * Enumeración para representar los alineamientos posibles
 * */
public enum Align {
  LEFT(true, Component.LEFT_ALIGNMENT),
  RIGHT(true, Component.RIGHT_ALIGNMENT),
  TOP(false, Component.TOP_ALIGNMENT),
  BOTTOM(false, Component.BOTTOM_ALIGNMENT),
  ;

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
