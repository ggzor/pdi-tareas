package imagenes;

@FunctionalInterface
public interface OperadorMatrizPixel<T> {
	/**
	 * Aplica la operación sobre el objeto acumulador dado el valor
	 * en la imagen y la posición en el kernel.
	 * */
	public T aplicar(T temp, int valor, int kx, int ky);
}

