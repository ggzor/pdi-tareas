function range(x) {
  const result = [];
  for (var i = 0; i < x; i++) result.push(i);
  return result;
}

function firstToLast(func, len) {
  if (len === undefined) len = func.length;
  if (len == 1) return func;

  const params = range(len).map((x) => `arg${x}`);

  return eval(`function(${params.join(",")}) {
    return func(${params.slice(1).join(",")}, arg0);
  }`);
}

function normalize(func, len) {
  if (len === undefined) len = func.length;

  const params = range(len).map((x) => `arg${x}`);

  return eval(`function(${params.join(",")}) {
    return func(${params.join(",")});
  }`);
}

function curry(func) {
  return function curried(...args) {
    if (args.length >= func.length) {
      return func.apply(this, args);
    } else {
      return function (...args2) {
        return curried.apply(this, args.concat(args2));
      };
    }
  };
}

const id = (x) => x;

const pipe = (...fns) => (x) => fns.reduce((y, f) => f(y), x);

function methodSignature(m) {
  return [m.getName(), m.getParameterTypes().length];
}

function bindOperators() {
  const OperadoresPunto = Java.type("imagenes.OperadoresPunto");
  const ModoBN = Java.type("imagenes.ModoBN");

  const operators = Java.from(OperadoresPunto.class.getDeclaredMethods())
    .filter((m) => {
      const name = m.getName();
      const params = m.getParameterTypes();

      return (
        params.length > 0 &&
        params[0].getName() == "java.awt.image.BufferedImage" &&
        !name.startsWith("aplicar") &&
        !name.startsWith("lambda")
      );
    })
    .map(methodSignature);

  for (const [name, len] of operators) {
    const fn =
      len == 1
        ? OperadoresPunto[name]
        : curry(firstToLast(OperadoresPunto[name], len));
    applyBinding(name, fn);
    applyBinding(name.slice(0, 3), fn);
  }

  const alias = {
    bn: (img) => blancoNegro(ModoBN.TV_GAMMA, img),
  };
  for (const k in alias) applyBinding(k, alias[k]);

  const Canales = Java.type("imagenes.Canales");
  const methodsCanales = Java.from(Canales.class.getDeclaredMethods()).map(
    methodSignature
  );
  for (const [name, len] of methodsCanales) {
    applyBinding(name, curry(normalize(Canales[name], len)));
  }
}
bindOperators();
