// var $brillo = $a * 255;
// var $contraste = $b * 3.0;
// var $sigmoide = $c * 0.2;

// // prettier-ignore
// var ops = pipe(
//   bn,
//   brillo($brillo),
//   contraste($contraste),
//   sigmoide($sigmoide),
//   sigmoide($sigmoide),
//   inv
// );

// $f *= 255;
// var brillos = invertir(umb($f, invertir(bn($imagen))));

// var mask = ops($imagen);
// // brillos
// var im = enmascarar(mask, $imagen);

// $z *= 255;
// var pestañas = diferencias($z, im);
// pestañas;
// mask;

// $y *= 255;
// var mask2 = separarRGB($y, im);

// diferencias($z, enmascarar(mask2, im));
// mask2;

// diff(pestañas, diff(mask2, mask));
var canales = separarCanales($imagen);

$p *= 2;
var im = sig($q, exp($p, canales[0]));

var InfoImagen = Java.type("imagenes.InfoImagen");
var otsu = InfoImagen.calcularUmbralOtsu(im);

var canales2 = separarCanales(convertirHSV($imagen));
$o *= 2;
canales2[1];
