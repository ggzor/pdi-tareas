package imagenes;

import utils.Either;

import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ImagenesIO {
  public static Either<ErrorImagen, BufferedImage> abrirImagen(File archivo) {
    if (archivo == null)
      throw new NullPointerException("El archivo proporcionado fue nulo");

    if (!archivo.exists())
      return Either.left(ErrorImagen.NO_EXISTE);

    try {
      BufferedImage imagen = ImageIO.read(archivo);
      if (imagen == null)
        return Either.left(ErrorImagen.NO_SOPORTADO);

      return Either.right(imagen);
    } catch (IOException ex) {
      return Either.left(ErrorImagen.ERROR_LECTURA);
    }
  }

  private ImagenesIO() { }
}

