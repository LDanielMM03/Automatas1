package principal;

import ast.*;
import java.util.List;

/**
 * Genera el desglose gramatical del AST en formato:
 * (<expresion> : <identificador> : <tipo de dato> : <asignacion> : <expresion> : <numero> : 8 : <break>)
 */
public class GeneradorGramatica {

    public String generar(NodoPrograma programa) {
        StringBuilder sb = new StringBuilder();
        List<Nodo> sentencias = programa.getSentencias();
        for (int i = 0; i < sentencias.size(); i++) {
            sb.append(generarSentencia(sentencias.get(i), 0));
            if (i < sentencias.size() - 1) sb.append("\n");
        }
        return sb.toString();
    }

    // =========================================================================
    //  Sentencias
    // =========================================================================

    private String generarSentencia(Nodo nodo, int nivel) {
        if (nodo instanceof NodoDeclaracion)
            return generarDeclaracion((NodoDeclaracion) nodo, nivel);

        if (nodo instanceof NodoAsignacion)
            return generarAsignacion((NodoAsignacion) nodo, nivel);

        if (nodo instanceof NodoMostrar)
            return generarMostrar((NodoMostrar) nodo, nivel);

        if (nodo instanceof NodoSi)
            return generarSi((NodoSi) nodo, nivel);

        return indent(nivel) + "(<expresion>)";
    }

    private String generarDeclaracion(NodoDeclaracion nodo, int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(nivel)).append("(<expresion> : <identificador> : <tipo de dato>");
        if (nodo.tieneValor()) {
            sb.append(" : <asignacion> : ").append(exprCompleta(nodo.getExpresion()));
        }
        sb.append(" : <break>)");
        return sb.toString();
    }

    private String generarAsignacion(NodoAsignacion nodo, int nivel) {
        return indent(nivel) + "(<expresion> : <identificador> : <asignacion> : " +
               exprCompleta(nodo.getExpresion()) + " : <break>)";
    }

    private String generarMostrar(NodoMostrar nodo, int nivel) {
        String tipo = nodo.tienesSaltoDeLinea() ? "<println>" : "<print>";
        return indent(nivel) + "(<expresion> : " + tipo + " : <parentesis> : " +
               exprCompleta(nodo.getExpresion()) + " : <parentesis> : <break>)";
    }

    private String generarSi(NodoSi nodo, int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(nivel))
          .append("(<bloque> : <aguila> : <parentesis> : ")
          .append(exprCondicion(nodo.getCondicion()))
          .append(" : <parentesis> : <corchete> :\n");

        for (Nodo s : nodo.getCuerpoVerdad().getSentencias()) {
            sb.append(generarSentencia(s, nivel + 1)).append("\n");
        }

        if (nodo.tieneSino()) {
            sb.append(indent(nivel)).append("<corchete> : <bloque> : <topo> : <corchete> :\n");
            for (Nodo s : nodo.getCuerpoFalso().getSentencias()) {
                sb.append(generarSentencia(s, nivel + 1)).append("\n");
            }
        }

        sb.append(indent(nivel)).append("<corchete>)");
        return sb.toString();
    }

    private String exprCondicion(Nodo nodo) {
        if (nodo instanceof NodoBinario) {
            NodoBinario bin = (NodoBinario) nodo;
            return "<expresion> : " + atomoCondicion(bin.getIzquierda()) +
                   " : <operador> : " + atomoCondicion(bin.getDerecha());
        }
        return "<expresion> : " + atomoCondicion(nodo);
    }

    private String atomoCondicion(Nodo nodo) {
        if (nodo instanceof NodoIdentificador) return "<identificador>";
        if (nodo instanceof NodoLiteral) {
            NodoLiteral lit = (NodoLiteral) nodo;
            switch (lit.getTipo()) {
                case "perro": return "<numeros>";
                case "gato":  return "<decimal>";
                case "pez":   return "<cadena>";
            }
        }
        if (nodo instanceof NodoBinario) {
            NodoBinario bin = (NodoBinario) nodo;
            return atomoCondicion(bin.getIzquierda()) + " : <operador> : " +
                   atomoCondicion(bin.getDerecha());
        }
        return "<expresion>";
    }

    // =========================================================================
    //  Expresiones
    // =========================================================================

    private String exprCompleta(Nodo nodo) {
        if (nodo instanceof NodoLiteral && ((NodoLiteral) nodo).getTipo().equals("pez")) {
            NodoLiteral lit = (NodoLiteral) nodo;
            return "<comillas> : <expresion> : <cadena> : " + lit.getValor() + " : <comillas>";
        }
        return "<expresion> : " + parteExpresion(nodo);
    }

    private String parteExpresion(Nodo nodo) {
        if (nodo instanceof NodoLiteral) {
            NodoLiteral lit = (NodoLiteral) nodo;
            switch (lit.getTipo()) {
                case "perro":   return "<numeros> : " + lit.getValor();
                case "gato":    return "<decimal> : " + lit.getValor();
                case "pez":     return "<cadena> : " + lit.getValor();
            }
        }

        if (nodo instanceof NodoIdentificador) {
            return "<identificador>";
        }

        if (nodo instanceof NodoBinario) {
            NodoBinario bin = (NodoBinario) nodo;
            String op = bin.getOperador();
            return tipoOperador(op) + " : " +
                   parteExpresion(bin.getIzquierda()) + " : " + op + " : " +
                   parteExpresion(bin.getDerecha());
        }

        if (nodo instanceof NodoUnario) {
            NodoUnario un = (NodoUnario) nodo;
            return "<operacion unaria> : " + un.getOperador() + " : " +
                   parteExpresion(un.getOperando());
        }

        return "<expresion>";
    }

    private String tipoOperador(String op) {
        switch (op) {
            case "+": case "-": case "*": case "/": case "%":
                return "<operador aritmetico>";
            case "==": case "!=": case ">": case "<": case ">=": case "<=":
                return "<operador relacional>";
            case "&&": case "||":
                return "<operador logico>";
            default:
                return "<operador>";
        }
    }

    // =========================================================================
    //  Helper
    // =========================================================================

    private String indent(int nivel) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nivel; i++) sb.append("    ");
        return sb.toString();
    }
}
