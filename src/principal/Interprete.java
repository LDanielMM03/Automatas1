package principal;

import ast.*;
import java.util.*;

public class Interprete {

    private final Deque<Map<String, Object>> ambitos = new ArrayDeque<>();
    private final StringBuilder salida = new StringBuilder();

    public String ejecutar(NodoPrograma programa) {
        salida.setLength(0);
        ambitos.clear();
        ambitos.push(new HashMap<>());
        try {
            for (Nodo s : programa.getSentencias()) {
                ejecutarNodo(s);
            }
        } catch (RuntimeException e) {
            salida.append("[Error en ejecucion] ").append(e.getMessage());
        }
        return salida.toString();
    }

    // =========================================================================
    //  Ejecutar sentencia
    // =========================================================================

    private void ejecutarNodo(Nodo nodo) {
        if (nodo instanceof NodoDeclaracion) {
            NodoDeclaracion d = (NodoDeclaracion) nodo;
            Object valor = d.tieneValor() ? evaluar(d.getExpresion()) : valorDefault(d.getTipo());
            ambitos.peek().put(d.getNombre(), valor);
            return;
        }
        if (nodo instanceof NodoAsignacion) {
            NodoAsignacion a = (NodoAsignacion) nodo;
            Object valor = evaluar(a.getExpresion());
            asignar(a.getNombre(), valor);
            return;
        }
        if (nodo instanceof NodoSi) {
            NodoSi si = (NodoSi) nodo;
            if ((Boolean) evaluar(si.getCondicion()))
                ejecutarBloque(si.getCuerpoVerdad());
            else if (si.tieneSino())
                ejecutarBloque(si.getCuerpoFalso());
            return;
        }
        if (nodo instanceof NodoMientras) {
            NodoMientras m = (NodoMientras) nodo;
            int limite = 100_000;
            while ((Boolean) evaluar(m.getCondicion())) {
                if (--limite < 0) throw new RuntimeException("Bucle infinito detectado");
                ejecutarBloque(m.getCuerpo());
            }
            return;
        }
        if (nodo instanceof NodoMostrar) {
            Object val = evaluar(((NodoMostrar) nodo).getExpresion());
            salida.append(formatear(val)).append("\n");
            return;
        }
        if (nodo instanceof NodoBloque) {
            ejecutarBloque((NodoBloque) nodo);
        }
    }

    private void ejecutarBloque(NodoBloque bloque) {
        ambitos.push(new HashMap<>());
        for (Nodo s : bloque.getSentencias()) ejecutarNodo(s);
        ambitos.pop();
    }

    // =========================================================================
    //  Evaluar expresion (devuelve valor)
    // =========================================================================

    private Object evaluar(Nodo nodo) {
        if (nodo instanceof NodoLiteral)
            return ((NodoLiteral) nodo).getValor();

        if (nodo instanceof NodoIdentificador)
            return buscar(((NodoIdentificador) nodo).getNombre());

        if (nodo instanceof NodoBinario)
            return evaluarBinario((NodoBinario) nodo);

        if (nodo instanceof NodoUnario)
            return evaluarUnario((NodoUnario) nodo);

        return null;
    }

    private Object evaluarBinario(NodoBinario nodo) {
        String op  = nodo.getOperador();
        Object izq = evaluar(nodo.getIzquierda());
        Object der = evaluar(nodo.getDerecha());

        // Logicos
        if (op.equals("&&")) return (Boolean) izq && (Boolean) der;
        if (op.equals("||")) return (Boolean) izq || (Boolean) der;

        // Concatenacion de cadenas con +
        if (op.equals("+") && (izq instanceof String || der instanceof String))
            return String.valueOf(izq) + String.valueOf(der);

        // Numericos
        if (izq instanceof Number && der instanceof Number) {
            boolean esDecimal = izq instanceof Double || der instanceof Double;
            double a = ((Number) izq).doubleValue();
            double b = ((Number) der).doubleValue();
            switch (op) {
                case "+":  if (esDecimal) return a + b; return (int)(a + b);
                case "-":  if (esDecimal) return a - b; return (int)(a - b);
                case "*":  if (esDecimal) return a * b; return (int)(a * b);
                case "/":
                    if (b == 0) throw new RuntimeException("Division por cero");
                    if (esDecimal) return a / b; return (int)(a / b);
                case "%":  if (esDecimal) return a % b; return (int)(a % b);
                case "==": return a == b;
                case "!=": return a != b;
                case ">":  return a > b;
                case "<":  return a < b;
                case ">=": return a >= b;
                case "<=": return a <= b;
            }
        }

        // Igualdad generica
        if (op.equals("==")) return Objects.equals(izq, der);
        if (op.equals("!=")) return !Objects.equals(izq, der);

        throw new RuntimeException("Operador desconocido: " + op);
    }

    private Object evaluarUnario(NodoUnario nodo) {
        Object val = evaluar(nodo.getOperando());
        switch (nodo.getOperador()) {
            case "!": return !(Boolean) val;
            case "-":
                if (val instanceof Double) return -(Double) val;
                return -(Integer) val;
        }
        return null;
    }

    // =========================================================================
    //  Manejo de ambitos
    // =========================================================================

    private Object buscar(String nombre) {
        for (Map<String, Object> scope : ambitos)
            if (scope.containsKey(nombre)) return scope.get(nombre);
        return null;
    }

    private void asignar(String nombre, Object valor) {
        for (Map<String, Object> scope : ambitos) {
            if (scope.containsKey(nombre)) {
                scope.put(nombre, valor);
                return;
            }
        }
        ambitos.peek().put(nombre, valor);
    }

    // =========================================================================
    //  Helpers
    // =========================================================================

    private Object valorDefault(String tipo) {
        switch (tipo) {
            case "perro":   return 0;
            case "gato":    return 0.0;
            case "Pez":     return "";
            case "boolean": return false;
            default:        return null;
        }
    }

    private String formatear(Object val) {
        if (val == null)            return "null";
        if (val instanceof Double) {
            double d = (Double) val;
            return d == Math.floor(d) && !Double.isInfinite(d)
                ? String.valueOf((long) d) + ".0"
                : String.valueOf(d);
        }
        return String.valueOf(val);
    }
}
