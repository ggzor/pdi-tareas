"use strict";

function _slicedToArray(arr, i) { return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _unsupportedIterableToArray(arr, i) || _nonIterableRest(); }

function _nonIterableRest() { throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method."); }

function _iterableToArrayLimit(arr, i) { if (typeof Symbol === "undefined" || !(Symbol.iterator in Object(arr))) return; var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"] != null) _i["return"](); } finally { if (_d) throw _e; } } return _arr; }

function _arrayWithHoles(arr) { if (Array.isArray(arr)) return arr; }

function _createForOfIteratorHelper(o, allowArrayLike) { var it; if (typeof Symbol === "undefined" || o[Symbol.iterator] == null) { if (Array.isArray(o) || (it = _unsupportedIterableToArray(o)) || allowArrayLike && o && typeof o.length === "number") { if (it) o = it; var i = 0; var F = function F() {}; return { s: F, n: function n() { if (i >= o.length) return { done: true }; return { done: false, value: o[i++] }; }, e: function e(_e2) { throw _e2; }, f: F }; } throw new TypeError("Invalid attempt to iterate non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method."); } var normalCompletion = true, didErr = false, err; return { s: function s() { it = o[Symbol.iterator](); }, n: function n() { var step = it.next(); normalCompletion = step.done; return step; }, e: function e(_e3) { didErr = true; err = _e3; }, f: function f() { try { if (!normalCompletion && it["return"] != null) it["return"](); } finally { if (didErr) throw err; } } }; }

function _unsupportedIterableToArray(o, minLen) { if (!o) return; if (typeof o === "string") return _arrayLikeToArray(o, minLen); var n = Object.prototype.toString.call(o).slice(8, -1); if (n === "Object" && o.constructor) n = o.constructor.name; if (n === "Map" || n === "Set") return Array.from(o); if (n === "Arguments" || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)) return _arrayLikeToArray(o, minLen); }

function _arrayLikeToArray(arr, len) { if (len == null || len > arr.length) len = arr.length; for (var i = 0, arr2 = new Array(len); i < len; i++) { arr2[i] = arr[i]; } return arr2; }

function range(x) {
  var result = [];

  for (var i = 0; i < x; i++) {
    result.push(i);
  }

  return result;
}

function firstToLast(func, len) {
  if (len === undefined) len = func.length;
  if (len == 1) return func;
  var params = range(len).map(function (x) {
    return "arg".concat(x);
  });
  return eval("function(".concat(params.join(","), ") {\n    return func(").concat(params.slice(1).join(","), ", arg0);\n  }"));
}

function normalize(func, len) {
  if (len === undefined) len = func.length;
  var params = range(len).map(function (x) {
    return "arg".concat(x);
  });
  return eval("function(".concat(params.join(","), ") {\n    return func(").concat(params.join(","), ");\n  }"));
}

function curry(func) {
  return function curried() {
    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    if (args.length >= func.length) {
      return func.apply(this, args);
    } else {
      return function () {
        for (var _len2 = arguments.length, args2 = new Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
          args2[_key2] = arguments[_key2];
        }

        return curried.apply(this, args.concat(args2));
      };
    }
  };
}

var id = function id(x) {
  return x;
};

var pipe = function pipe() {
  for (var _len3 = arguments.length, fns = new Array(_len3), _key3 = 0; _key3 < _len3; _key3++) {
    fns[_key3] = arguments[_key3];
  }

  return function (x) {
    return fns.reduce(function (y, f) {
      return f(y);
    }, x);
  };
};

function methodSignature(m) {
  return [m.getName(), m.getParameterTypes().length];
}

function bindOperators() {
  var OperadoresPunto = Java.type("imagenes.OperadoresPunto");
  var ModoBN = Java.type("imagenes.ModoBN");
  var operators = Java.from(OperadoresPunto["class"].getDeclaredMethods()).filter(function (m) {
    var name = m.getName();
    var params = m.getParameterTypes();
    return params.length > 0 && params[0].getName() == "java.awt.image.BufferedImage" && !name.startsWith("aplicar") && !name.startsWith("lambda");
  }).map(methodSignature);

  var _iterator = _createForOfIteratorHelper(operators),
      _step;

  try {
    for (_iterator.s(); !(_step = _iterator.n()).done;) {
      var _step$value = _slicedToArray(_step.value, 2),
          name = _step$value[0],
          len = _step$value[1];

      var fn = len == 1 ? OperadoresPunto[name] : curry(firstToLast(OperadoresPunto[name], len));
      applyBinding(name, fn);
      applyBinding(name.slice(0, 3), fn);
    }
  } catch (err) {
    _iterator.e(err);
  } finally {
    _iterator.f();
  }

  var alias = {
    bn: function bn(img) {
      return blancoNegro(ModoBN.TV_GAMMA, img);
    }
  };

  for (var k in alias) {
    applyBinding(k, alias[k]);
  }

  var Canales = Java.type("imagenes.Canales");
  var methodsCanales = Java.from(Canales["class"].getDeclaredMethods()).map(methodSignature);

  var _iterator2 = _createForOfIteratorHelper(methodsCanales),
      _step2;

  try {
    for (_iterator2.s(); !(_step2 = _iterator2.n()).done;) {
      var _step2$value = _slicedToArray(_step2.value, 2),
          _name = _step2$value[0],
          _len4 = _step2$value[1];

      applyBinding(_name, curry(normalize(Canales[_name], _len4)));
    }
  } catch (err) {
    _iterator2.e(err);
  } finally {
    _iterator2.f();
  }
}

bindOperators();
