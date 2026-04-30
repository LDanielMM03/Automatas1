package semantico;

import ast.*;
import excepciones.ErrorSemantico;

/**
 * Analizador Semantico.
 *
 * Verifica:
 *   - Variables no declaradas
 *   - Variables redeclaradas en el mismo ambito
 *   - Incompatibilidad de tipos en asignaciones
 *   - Tipos invalidos en operaciones aritmeticas, relacionales y logicas
 *   - Condicion no boolean en if / while
 */
public class AnalizadorSemantico {

    private TablaSimbolos ambito;

    public AnalizadorSemantico() {
        this.ambito = new TablaSimbolos(null);  // ambito global
    }

    // =========================================================================
    //  Punto de entrada
    // =========================================================================

    public void analizar(NodoPrograma programa) throws ErrorSemantico {
        for (Nodo s : programa.getSentencias()) {
            analizarNodo(s);
        }
    }

    // =========================================================================
    //  Dispatcher central
    //  Retorna el tipo resultante ("perro","gato","Pez","boolean") o "void".
    // =========================================================================

    private String analizarNodo(Nodo nodo) throws ErrorSemantico {
        if (nodo instanceof NodoDeclaracion)
            return analizarDeclaracion((NodoDeclaracion) nodo);

        if (nodo instanceof NodoAsignacion)
            return analizarAsignacion((NodoAsignacion) nodo);

        if (nodo instanceof NodoSi) {
            analizarSi((NodoSi) nodo);
            return "void";
        }

        if (nodo instanceof NodoMientras) {
            analizarMientras((NodoMientras) nodo);
            return "void";
        }

        if (nodo instanceof NodoBloque) {
            analizarBloque((NodoBloque) nodo);
            return "void";
        }

        if (nodo instanceof NodoMostrar) {
            analizarMostrar((NodoMostrar) nodo);
            return "void";
        }

        if (nodo instanceof NodoBinario)
            return analizarBinario((NodoBinario) nodo);

        if (nodo instanceof NodoUnario)
            return analizarUnario((NodoUnario) nodo);

        if (nodo instanceof NodoLiteral)
            return ((NodoLiteral) nodo).getTipo();

        if (nodo instanceof NodoIdentificador)
            return analizarIdentificador((NodoIdentificador) nodo);

        throw new ErrorSemantico(
            "Nodo desconocido en analisis semantico", nodo.getLinea(), nodo.getColumna());
    }

    // =========================================================================
    //  Declaracion
    // =========================================================================

    private String analizarDeclaracion(NodoDeclaracion nodo) throws ErrorSemantico {
        String nombre = nodo.getNombre();
        String tipo   = nodo.getTipo();

        if (ambito.estaDeclaradaLocalmente(nombre))
            throw new ErrorSemantico(
                "La variable '" + nombre + "' ya fue declarada en este ambito",
                nodo.getLinea(), nodo.getColumna());

        if (nodo.tieneValor()) {
            String tipoExpr = analizarNodo(nodo.getExpresion());
            if (!sonCompatibles(tipo, tipoExpr))
                throw new ErrorSemantico(
                    "Tipos incompatibles: no se puede asignar '" + tipoExpr +
                    "' a una variable de tipo '" + tipo + "'",
                    nodo.getLinea(), nodo.getColumna());
        }

        ambito.declarar(nombre, tipo);
        return "void";
    }

    // =========================================================================
    //  Asignacion
    // =========================================================================

    private String analizarAsignacion(NodoAsignacion nodo) throws ErrorSemantico {
        String nombre = nodo.getNombre();

        if (!ambito.estaDeclarada(nombre))
            throw new ErrorSemantico(
                "La variable '" + nombre + "' no ha sido declarada",
                nodo.getLinea(), nodo.getColumna());

        String tipoVar  = ambito.obtener(nombre).getTipo();
        String tipoExpr = analizarNodo(nodo.getExpresion());

        if (!sonCompatibles(tipoVar, tipoExpr))
            throw new ErrorSemantico(
                "Tipos incompatibles: no se puede asignar '" + tipoExpr +
                "' a la variable '" + nombre + "' de tipo '" + tipoVar + "'",
                nodo.getLinea(), nodo.getColumna());

        return "void";
    }

    // =========================================================================
    //  If / Else
    // =========================================================================

    private void analizarSi(NodoSi nodo) throws ErrorSemantico {
        String tipoCond = analizarNodo(nodo.getCondicion());

        if (!tipoCond.equals("boolean"))
            throw new ErrorSemantico(
                "La condicion del 'if' debe ser de tipo boolean, pero es '" + tipoCond + "'",
                nodo.getLinea(), nodo.getColumna());

        analizarBloque(nodo.getCuerpoVerdad());
        if (nodo.tieneSino()) analizarBloque(nodo.getCuerpoFalso());
    }

    // =========================================================================
    //  While
    // =========================================================================

    private void analizarMientras(NodoMientras nodo) throws ErrorSemantico {
        String tipoCond = analizarNodo(nodo.getCondicion());

        if (!tipoCond.equals("boolean"))
            throw new ErrorSemantico(
                "La condicion del 'while' debe ser de tipo boolean, pero es '" + tipoCond + "'",
                nodo.getLinea(), nodo.getColumna());

        analizarBloque(nodo.getCuerpo());
    }

    // =========================================================================
    //  Bloque (crea un nuevo ambito)
    // =========================================================================

    private void analizarBloque(NodoBloque nodo) throws ErrorSemantico {
        TablaSimbolos ambitoAnterior = ambito;
        ambito = new TablaSimbolos(ambito);

        for (Nodo s : nodo.getSentencias()) {
            analizarNodo(s);
        }

        ambito = ambitoAnterior;
    }

    // =========================================================================
    //  System.out.println
    // =========================================================================

    private void analizarMostrar(NodoMostrar nodo) throws ErrorSemantico {
        analizarNodo(nodo.getExpresion());  // cualquier tipo es valido
    }

    // =========================================================================
    //  Identificador (uso de variable)
    // =========================================================================

    private String analizarIdentificador(NodoIdentificador nodo) throws ErrorSemantico {
        String nombre = nodo.getNombre();

        if (!ambito.estaDeclarada(nombre))
            throw new ErrorSemantico(
                "La variable '" + nombre + "' no ha sido declarada",
                nodo.getLinea(), nodo.getColumna());

        return ambito.obtener(nombre).getTipo();
    }

    // =========================================================================
    //  Operacion binaria
    // =========================================================================

    private String analizarBinario(NodoBinario nodo) throws ErrorSemantico {
        String tipoIzq = analizarNodo(nodo.getIzquierda());
        String tipoDer = analizarNodo(nodo.getDerecha());
        String op      = nodo.getOperador();

        switch (op) {
            case "+":
                if (tipoIzq.equals("Pez") && tipoDer.equals("Pez"))
                    return "Pez";
                if (esNumerico(tipoIzq) && esNumerico(tipoDer))
                    return tipoResultanteNumerico(tipoIzq, tipoDer);
                throw new ErrorSemantico(
                    "Operador '+' no es aplicable entre '" + tipoIzq + "' y '" + tipoDer + "'",
                    nodo.getLinea(), nodo.getColumna());

            case "-": case "*": case "/": case "%":
                if (!esNumerico(tipoIzq))
                    throw new ErrorSemantico(
                        "Operador '" + op + "' requiere tipo numerico, pero el lado izquierdo es '" + tipoIzq + "'",
                        nodo.getLinea(), nodo.getColumna());
                if (!esNumerico(tipoDer))
                    throw new ErrorSemantico(
                        "Operador '" + op + "' requiere tipo numerico, pero el lado derecho es '" + tipoDer + "'",
                        nodo.getLinea(), nodo.getColumna());
                return tipoResultanteNumerico(tipoIzq, tipoDer);

            case "==": case "!=":
                if (!tipoIzq.equals(tipoDer) && !(esNumerico(tipoIzq) && esNumerico(tipoDer)))
                    throw new ErrorSemantico(
                        "No se pueden comparar tipos '" + tipoIzq + "' y '" + tipoDer + "' con '" + op + "'",
                        nodo.getLinea(), nodo.getColumna());
                return "boolean";

            case ">": case "<": case ">=": case "<=":
                if (!esNumerico(tipoIzq) || !esNumerico(tipoDer))
                    throw new ErrorSemantico(
                        "Operador relacional '" + op + "' solo aplica a tipos numericos",
                        nodo.getLinea(), nodo.getColumna());
                return "boolean";

            case "&&": case "||":
                if (!tipoIzq.equals("boolean"))
                    throw new ErrorSemantico(
                        "Operador '" + op + "' requiere boolean, lado izquierdo es '" + tipoIzq + "'",
                        nodo.getLinea(), nodo.getColumna());
                if (!tipoDer.equals("boolean"))
                    throw new ErrorSemantico(
                        "Operador '" + op + "' requiere boolean, lado derecho es '" + tipoDer + "'",
                        nodo.getLinea(), nodo.getColumna());
                return "boolean";

            default:
                throw new ErrorSemantico(
                    "Operador binario desconocido: '" + op + "'",
                    nodo.getLinea(), nodo.getColumna());
        }
    }

    // =========================================================================
    //  Operacion unaria
    // =========================================================================

    private String analizarUnario(NodoUnario nodo) throws ErrorSemantico {
        String tipoOp = analizarNodo(nodo.getOperando());

        switch (nodo.getOperador()) {
            case "!":
                if (!tipoOp.equals("boolean"))
                    throw new ErrorSemantico(
                        "Operador '!' solo aplica a boolean, se encontro '" + tipoOp + "'",
                        nodo.getLinea(), nodo.getColumna());
                return "boolean";

            case "-":
                if (!esNumerico(tipoOp))
                    throw new ErrorSemantico(
                        "Operador negacion '-' solo aplica a numeros, se encontro '" + tipoOp + "'",
                        nodo.getLinea(), nodo.getColumna());
                return tipoOp;

            default:
                throw new ErrorSemantico(
                    "Operador unario desconocido: '" + nodo.getOperador() + "'",
                    nodo.getLinea(), nodo.getColumna());
        }
    }

    // =========================================================================
    //  Helpers de tipos
    // =========================================================================

    private boolean esNumerico(String tipo) {
        return tipo.equals("perro") || tipo.equals("gato");
    }

    private String tipoResultanteNumerico(String a, String b) {
        return (a.equals("gato") || b.equals("gato")) ? "gato" : "perro";
    }

    private boolean sonCompatibles(String tipoVar, String tipoExpr) {
        if (tipoVar.equals(tipoExpr))                          return true;
        if (tipoVar.equals("gato") && tipoExpr.equals("perro")) return true;
        return false;
    }
}
