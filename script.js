var conv = Java.type("imagenes.OperadoresConvolucionales");

// prettier-ignore
var m1 = [
    [  1,   0,   0,   1,   0,   0,   1],
    [  0,   0,   0,   0,   0,   0,   0],
    [  0,   0, 0.5,   0, 0.5,   0,   0],
    [0.5,   0,   0,-9.5,   0,   0, 0.5],
    [  0,   0, 0.5,   0, 0.5,   0,   0],
    [  0,   0,   0,   0,   0,   0,   0],
    [  1,   0,   0,   1,   0,   0,   1],
  ]

// prettier-ignore
var m2 =
  [
    [  1,   0,   0,   1,   0,   0,   1],
    [  0,   0,   0,   0,   0,   0,   0],
    [  0,   0, 0.5,   0, 0.5,   0,   0],
    [  0,   0,   0,-8.5,   0,   0,   0],
    [  0,   0, 0.5,   0, 0.5,   0,   0],
    [  0,   0,   0,   0,   0,   0,   0],
    [  1,   0,   0,   1,   0,   0,   1],
  ]

// prettier-ignore
var kernel = Java.to(
  m2,
  "double[][]"
);

conv.aplicar($imagen, kernel);
