package imagenes;

public enum ErrorImagen {
  NO_EXISTE("El archivo seleccionado no existe"),
  NO_SOPORTADO("El archivo contiene un formato de imagen no soportado"),
  ERROR_LECTURA("No se pudo leer el archivo de imagen");

  public final String mensaje;
  private ErrorImagen(String mensaje) {
    this.mensaje = mensaje;
  }
};

