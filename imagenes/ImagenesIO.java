package imagenes;

import utils.Either;

import java.io.*;
import java.awt.image.BufferedImage;

import javax.imageio.*;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;

/**
 * Clase con funciones de utilería para obtener imágenes a partir de archivos
 * */
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

  public static Either<Void, Void> guardarImagen(BufferedImage imagen, File archivo) {
    try {
      if (archivo.getName().toLowerCase().endsWith(".jpg")) {
        try (FileImageOutputStream os = new FileImageOutputStream(archivo)) {
          JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
          jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
          jpegParams.setCompressionQuality(1f);

          ImageWriter iw = ImageIO.getImageWritersByFormatName("jpg").next();
          iw.setOutput(os);
          iw.write(null, new IIOImage(imagen, null, null), jpegParams);

          return Either.right(null);
        }
      } else {
        ImageIO.write(imagen, "bmp", archivo);
        return Either.right(null);
      }
    } catch(IOException ex) {
      return Either.left(null);
    }
  }

  // Constructor privado para evitar instanciamiento
  private ImagenesIO() { }
}

