package gui;

import java.util.function.Consumer;

import javax.swing.*;

/**
 * Clase para agregar características a un botón de algún tipo
 * */
public class JSliderBuilder extends AbstractBuilder<JSliderBuilder, JSlider> {
  public JSliderBuilder(JSlider component) {
		super(component);
	}

	public JSliderBuilder limits(int min, int max) {
		value.setMinimum(min);
		value.setMaximum(max);
		return this;
	}

	public JSliderBuilder onChange(Consumer<Integer> escuchador) {
		value.addChangeListener(ev -> escuchador.accept(value.getValue()));
		return this;
	}

	public JSliderBuilder value(int value) {
		this.value.setValue(value);
		return this;
	}
}
