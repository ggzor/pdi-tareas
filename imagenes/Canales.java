package imagenes;

import java.awt.image.*;

public class Canales {
  public static BufferedImage[] separarCanales(BufferedImage img) {
    BufferedImage[] resultado = new BufferedImage[img.getRaster().getNumBands()];

    for (int canal = 0; canal < resultado.length; canal++) {
      int canalref = canal;

      int valor[] = new int[1];
      resultado[canal] = OperadoresPunto.aplicar(img, canales -> {
        valor[0] = canales[canalref];
        return valor;
      }, BufferedImage.TYPE_BYTE_GRAY);
    }

    return resultado;
  }

  public static void toHSL(float arr[]) {
    float r = arr[0];
    float g = arr[1];
    float b = arr[2];
    float h, s, l;

		r /= 255;
		g /= 255;
		b /= 255;

		float max = Math.max(r, Math.max(g, b));
		float min = Math.min(r, Math.min(g, b));

		l = (max + min) / 2;

		if (max == min) {
			h = s = 0;
		} else {
			float d = max - min;
			s = l > 0.5f ? d / (2.0f - max - min) : d / (max + min);

			if (max == r) {
				h = (g - b) / d + (g < b ? 6 : 0);
			} else if (max == g) {
				h = (b - r) / d + 2;
			} else {
				h = (r - g) / d + 4;
			}

			h /= 6;
		}

    arr[0] = h;
    arr[1] = s;
    arr[2] = l;
  }

  public static void toHSV(float arr[]) {
    float r = arr[0];
    float g = arr[1];
    float b = arr[2];

		r /= 255;
		g /= 255;
		b /= 255;

		float max = Math.max(r, Math.max(g, b));
		float min = Math.min(r, Math.min(g, b));

    float h, s, v = max;

    float d = max - min;
		s = max == 0 ? 0 : d / max;

		if (max == min) {
			h = 0;
		} else {
			if (max == r) {
				h = (g - b) / d + (g < b ? 6 : 0);
			} else if (max == g) {
				h = (b - r) / d + 2;
			} else {
				h = (r - g) / d + 4;
			}

			h /= 6;
		}

    arr[0] = h;
    arr[1] = s;
    arr[2] = v;
  }

  public static BufferedImage convertirHSL(BufferedImage img) {
		float valores[] = new float[3];
    return OperadoresPunto.aplicar(img, canales -> {
			valores[0] = canales[0];
			valores[1] = canales[1];
			valores[2] = canales[2];

      toHSL(valores);

			canales[0] = (int)(valores[0] * 255);
			canales[1] = (int)(valores[1] * 255);
			canales[2] = (int)(valores[2] * 255);

      return canales;
    }, img.getType());
  }

  public static BufferedImage convertirHSV(BufferedImage img) {
		float valores[] = new float[3];
    return OperadoresPunto.aplicar(img, canales -> {
			valores[0] = canales[0];
			valores[1] = canales[1];
			valores[2] = canales[2];

      toHSV(valores);

			canales[0] = (int)(valores[0] * 255);
			canales[1] = (int)(valores[1] * 255);
			canales[2] = (int)(valores[2] * 255);

      return canales;
    }, img.getType());
  }

  public static BufferedImage
    unirCanales(BufferedImage r,
                BufferedImage g,
                BufferedImage b) {
    WritableRaster rasterR = r.getRaster(),
                   rasterG = g.getRaster(),
                   rasterB = b.getRaster();

    int valores[] = new int[3];
    return OperadoresPunto.aplicar(r, (x, y, valor) -> {
      valores[0] = rasterR.getSample(x, y, 0);
      valores[1] = rasterG.getSample(x, y, 0);
      valores[2] = rasterB.getSample(x, y, 0);

      return valores;
    }, BufferedImage.TYPE_INT_RGB);
  }

  private Canales() { }
}
