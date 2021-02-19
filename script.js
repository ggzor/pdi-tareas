// Primer paso: eliminación de piel y esclerótica
var $brillo = $a * 255;
var $contraste = $b * 3.0;
var $sigmoide = $c * 0.2;

// prettier-ignore
var ops1 = pipe(
  bn,
  brillo($brillo),
  contraste($contraste),
  sigmoide($sigmoide),
  sigmoide($sigmoide),
  inv
);
var mask1 = ops1($imagen);
var img1 = enmascarar(mask1, $imagen);

// Segundo paso: Manipulación en el espacio de colores RGB
$d *= 255;
var pestañas_finas = diferencias($d, img1);

$e *= 10;
var pupila = inv(diferencias($e, img1));

$f *= 255;
var regiones_oscuras = separarRGB($f, img1);

// prettier-ignore
var opsRGB = pipe(
  diff(regiones_oscuras),
  diff(pestañas_finas),
  diff(pupila)
)

var maskRGB = opsRGB(mask1);

// Tercer paso: Manipulación en el espacio de colores HSL
$g *= 1.0;
$h += 1.0;
var maskHSL = umbralizacionHSLPorS($imagen, $g, $h);

// Combinación final de las máscaras
var maskFinal = interseccion(maskHSL, maskRGB);
// Generación de la imagen final
var imgFinal = enmascarar(maskFinal, $imagen);

// Lo que se muestra en pantalla
imgFinal;
