var $brillo = $a * 255;
var $contraste = $b * 3.0;
var $sigmoide = $c * 0.2;

// prettier-ignore
var ops = pipe(
  bn,
  brillo($brillo),
  contraste($contraste),
  sigmoide($sigmoide),
  sigmoide($sigmoide),
  inv
);

$f *= 255;
var brillos = invertir(umb($f, invertir(bn($imagen))));

var mask = ops($imagen);
// brillos
var im = enmascarar(mask, $imagen);

$z *= 255;
var pestañas = diferencias($z, im);
pestañas;
mask;

$y *= 255;
var mask2 = separarRGB($y, im);

diferencias($z, enmascarar(mask2, im));
mask2;

var canalesRGB = separarCanales($imagen);

$p *= 2;
var pupila = sig($q, exp($p, canalesRGB[0]));

var maskRGB = diff(pestañas, diff(mask2, mask));

var canales = separarCanales(convertirHSL($imagen));
$o *= 2;

$t = $t + 1.0;
var S = exp($t, canales[1]);

$w += 1.0;
print($x);
print($y);

var maskHSL = umbralizacionHSLPorS($imagen, $x, $w);
maskHSL;
maskRGB;
interseccion(maskHSL, maskRGB);

var sinBrillos = enmascarar(inv(brillos), $imagen);
sinBrillos;

var ModoBN = Java.type("imagenes.ModoBN");
var negros = blancoNegro(ModoBN.PROMEDIO, $imagen);
$j *= 255;
$i += 1;
umb($j, exp($i, separarCanales($imagen)[2]));
var finalMask = interseccion(maskHSL, maskRGB);

enmascarar(finalMask, $imagen);
