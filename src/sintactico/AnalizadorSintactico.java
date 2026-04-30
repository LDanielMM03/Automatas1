package sintactico;

import ast.*;
import excepciones.ErrorSintactico;
import tokens.TipoToken;
import tokens.Token;
import java.util.ArrayList;
import java.util.List;

/**
 * Analizador Sintactico (Parser) de descenso recursivo.
 *
 * Gramatica del lenguaje:
 *
 *   programa     -> sentencia* EOF
 *   sentencia    -> declaracion | asignacion | if_stmt | while_stmt | println_stmt
 *   declaracion  -> tipo IDENTIFICADOR ('e' expresion)? ';'
 *   tipo         -> 'perro' | 'gato' | 'Pez' | 'boolean'
 *   asignacion   -> IDENTIFICADOR 'e' expresion ';'
 *   if_stmt      -> 'if' '(' expresion ')' bloque ('else' bloque)?
 *   while_stmt   -> 'while' '(' expresion ')' bloque
 *   println_stmt -> 'System.out.println' '(' expresion ')' ';'
 *   bloque       -> '{' sentencia* '}'
 *   expresion    -> logica
 *   logica       -> igualdad (('&&' | '||') igualdad)*
 *   igualdad     -> comparacion (('==' | '!=') comparacion)*
 *   comparacion  -> termino (('>' | '>=' | '<' | '<=') termino)*
 *   termino      -> factor (('+' | '-') factor)*
 *   factor       -> unario (('*' | '/' | '%') unario)*
 *   unario       -> '!' unario | '-' unario | primario
 *   primario     -> LIT_ENTERO | LIT_DECIMAL | LIT_CADENA
 *                 | 'true' | 'false' | IDENTIFICADOR | '(' expresion ')'
 */
public class AnalizadorSintactico {

    private final List<Token> tokens;
    private int posicion;

    public AnalizadorSintactico(List<Token> tokens) {
        this.tokens   = tokens;
        this.posicion = 0;
    }

    // =========================================================================
    //  Helpers
    // =========================================================================

    private Token actual() {
        return tokens.get(posicion);
    }

    private Token consumir() {
        Token t = actual();
        posicion++;
        return t;
    }

    private Token consumir(TipoToken esperado, String descripcion) throws ErrorSintactico {
        Token t = actual();
        if (t.getTipo() != esperado)
            throw new ErrorSintactico(
                "Se esperaba " + descripcion + " pero se encontro '" + t.getValor() + "'",
                t.getLinea(), t.getColumna()
            );
        return consumir();
    }

    private boolean verifica(TipoToken tipo) {
        return actual().getTipo() == tipo;
    }

    private boolean esTipoDeVariable() {
        TipoToken t = actual().getTipo();
        return t == TipoToken.ENTERO  || t == TipoToken.DECIMAL ||
               t == TipoToken.CADENA_TIPO || t == TipoToken.BOOLEANO;
    }

    // =========================================================================
    //  Punto de entrada
    // =========================================================================

    public NodoPrograma parsear() throws ErrorSintactico {
        int lin = actual().getLinea(), col = actual().getColumna();
        List<Nodo> sentencias = new ArrayList<>();

        while (!verifica(TipoToken.EOF)) {
            sentencias.add(parsearSentencia());
        }

        consumir(TipoToken.EOF, "fin de archivo");
        return new NodoPrograma(sentencias, lin, col);
    }

    // =========================================================================
    //  Sentencias
    // =========================================================================

    private Nodo parsearSentencia() throws ErrorSintactico {
        if (esTipoDeVariable())              return parsearDeclaracion();
        if (verifica(TipoToken.IDENTIFICADOR)) return parsearAsignacion();
        if (verifica(TipoToken.SI))            return parsearSi();
        if (verifica(TipoToken.MIENTRAS))      return parsearMientras();
        if (verifica(TipoToken.MOSTRAR))       return parsearMostrar();

        Token t = actual();
        throw new ErrorSintactico(
            "Sentencia no valida: token inesperado '" + t.getValor() + "'",
            t.getLinea(), t.getColumna()
        );
    }

    // ─── Declaracion ─────────────────────────────────────────────────────────

    private NodoDeclaracion parsearDeclaracion() throws ErrorSintactico {
        Token tipoTok = consumir();                      // consume tipo
        int lin = tipoTok.getLinea(), col = tipoTok.getColumna();
        String tipo = tipoTok.getValor();

        Token nombreTok = consumir(TipoToken.IDENTIFICADOR, "nombre de variable");
        String nombre   = nombreTok.getValor();

        Nodo expresion = null;
        if (verifica(TipoToken.ASIGNACION)) {
            consumir();                                  // consume 'e'
            expresion = parsearExpresion();
        }

        consumir(TipoToken.PUNTO_COMA, "';' al final de la declaracion");
        return new NodoDeclaracion(tipo, nombre, expresion, lin, col);
    }

    // ─── Asignacion ──────────────────────────────────────────────────────────

    private NodoAsignacion parsearAsignacion() throws ErrorSintactico {
        Token nombreTok = consumir(TipoToken.IDENTIFICADOR, "identificador");
        int lin = nombreTok.getLinea(), col = nombreTok.getColumna();

        consumir(TipoToken.ASIGNACION, "operador de asignacion 'e'");
        Nodo expresion = parsearExpresion();
        consumir(TipoToken.PUNTO_COMA, "';' al final de la asignacion");

        return new NodoAsignacion(nombreTok.getValor(), expresion, lin, col);
    }

    // ─── If / Else ───────────────────────────────────────────────────────────

    private NodoSi parsearSi() throws ErrorSintactico {
        Token siTok = consumir(TipoToken.SI, "'if'");
        int lin = siTok.getLinea(), col = siTok.getColumna();

        consumir(TipoToken.PARENTESIS_IZQ, "'(' despues de 'if'");
        Nodo condicion = parsearExpresion();
        consumir(TipoToken.PARENTESIS_DER, "')' despues de condicion");

        NodoBloque cuerpoVerdad = parsearBloque();
        NodoBloque cuerpoFalso  = null;

        if (verifica(TipoToken.SINO)) {
            consumir();                                  // consume 'else'
            cuerpoFalso = parsearBloque();
        }

        return new NodoSi(condicion, cuerpoVerdad, cuerpoFalso, lin, col);
    }

    // ─── While ───────────────────────────────────────────────────────────────

    private NodoMientras parsearMientras() throws ErrorSintactico {
        Token mTok = consumir(TipoToken.MIENTRAS, "'while'");
        int lin = mTok.getLinea(), col = mTok.getColumna();

        consumir(TipoToken.PARENTESIS_IZQ, "'(' despues de 'while'");
        Nodo condicion = parsearExpresion();
        consumir(TipoToken.PARENTESIS_DER, "')' despues de condicion");

        NodoBloque cuerpo = parsearBloque();
        return new NodoMientras(condicion, cuerpo, lin, col);
    }

    // ─── System.out.println ──────────────────────────────────────────────────

    private NodoMostrar parsearMostrar() throws ErrorSintactico {
        Token mTok = consumir(TipoToken.MOSTRAR, "'System.out.println'");
        int lin = mTok.getLinea(), col = mTok.getColumna();

        consumir(TipoToken.PARENTESIS_IZQ, "'(' despues de 'System.out.println'");
        Nodo expresion = parsearExpresion();
        consumir(TipoToken.PARENTESIS_DER, "')' en System.out.println");
        consumir(TipoToken.PUNTO_COMA,     "';' al final de System.out.println");

        return new NodoMostrar(expresion, lin, col);
    }

    // ─── Bloque ──────────────────────────────────────────────────────────────

    private NodoBloque parsearBloque() throws ErrorSintactico {
        Token llave = consumir(TipoToken.LLAVE_IZQ, "'{'");
        int lin = llave.getLinea(), col = llave.getColumna();
        List<Nodo> sentencias = new ArrayList<>();

        while (!verifica(TipoToken.LLAVE_DER) && !verifica(TipoToken.EOF)) {
            sentencias.add(parsearSentencia());
        }

        consumir(TipoToken.LLAVE_DER, "'}' para cerrar el bloque");
        return new NodoBloque(sentencias, lin, col);
    }

    // =========================================================================
    //  Expresiones (jerarquia de precedencia ascendente)
    // =========================================================================

    private Nodo parsearExpresion() throws ErrorSintactico {
        return parsearLogica();
    }

    // Nivel 1: operadores logicos && ||
    private Nodo parsearLogica() throws ErrorSintactico {
        Nodo izq = parsearIgualdad();

        while (verifica(TipoToken.Y) || verifica(TipoToken.O)) {
            Token op = consumir();
            Nodo der = parsearIgualdad();
            izq = new NodoBinario(op.getValor(), izq, der, op.getLinea(), op.getColumna());
        }
        return izq;
    }

    // Nivel 2: == !=
    private Nodo parsearIgualdad() throws ErrorSintactico {
        Nodo izq = parsearComparacion();

        while (verifica(TipoToken.IGUAL_IGUAL) || verifica(TipoToken.DIFERENTE)) {
            Token op = consumir();
            Nodo der = parsearComparacion();
            izq = new NodoBinario(op.getValor(), izq, der, op.getLinea(), op.getColumna());
        }
        return izq;
    }

    // Nivel 3: > < >= <=
    private Nodo parsearComparacion() throws ErrorSintactico {
        Nodo izq = parsearTermino();

        while (verifica(TipoToken.MAYOR) || verifica(TipoToken.MENOR) ||
               verifica(TipoToken.MAYOR_IGUAL) || verifica(TipoToken.MENOR_IGUAL)) {
            Token op = consumir();
            Nodo der = parsearTermino();
            izq = new NodoBinario(op.getValor(), izq, der, op.getLinea(), op.getColumna());
        }
        return izq;
    }

    // Nivel 4: + -
    private Nodo parsearTermino() throws ErrorSintactico {
        Nodo izq = parsearFactor();

        while (verifica(TipoToken.SUMA) || verifica(TipoToken.RESTA)) {
            Token op = consumir();
            Nodo der = parsearFactor();
            izq = new NodoBinario(op.getValor(), izq, der, op.getLinea(), op.getColumna());
        }
        return izq;
    }

    // Nivel 5: * / %
    private Nodo parsearFactor() throws ErrorSintactico {
        Nodo izq = parsearUnario();

        while (verifica(TipoToken.MULTIPLICACION) || verifica(TipoToken.DIVISION) ||
               verifica(TipoToken.MODULO)) {
            Token op = consumir();
            Nodo der = parsearUnario();
            izq = new NodoBinario(op.getValor(), izq, der, op.getLinea(), op.getColumna());
        }
        return izq;
    }

    // Nivel 6: unarios (!, -)
    private Nodo parsearUnario() throws ErrorSintactico {
        if (verifica(TipoToken.NO)) {
            Token op = consumir();
            Nodo operando = parsearUnario();
            return new NodoUnario("!", operando, op.getLinea(), op.getColumna());
        }
        if (verifica(TipoToken.RESTA)) {
            Token op = consumir();
            Nodo operando = parsearUnario();
            return new NodoUnario("-", operando, op.getLinea(), op.getColumna());
        }
        return parsearPrimario();
    }

    // Nivel 7: valores atomicos
    private Nodo parsearPrimario() throws ErrorSintactico {
        Token t = actual();

        switch (t.getTipo()) {
            case LIT_ENTERO:
                consumir();
                return new NodoLiteral(Integer.parseInt(t.getValor()), "perro",
                                       t.getLinea(), t.getColumna());

            case LIT_DECIMAL:
                consumir();
                return new NodoLiteral(Double.parseDouble(t.getValor()), "gato",
                                       t.getLinea(), t.getColumna());

            case LIT_CADENA:
                consumir();
                return new NodoLiteral(t.getValor(), "Pez",
                                       t.getLinea(), t.getColumna());

            case LIT_VERDADERO:
                consumir();
                return new NodoLiteral(true, "boolean", t.getLinea(), t.getColumna());

            case LIT_FALSO:
                consumir();
                return new NodoLiteral(false, "boolean", t.getLinea(), t.getColumna());

            case IDENTIFICADOR:
                consumir();
                return new NodoIdentificador(t.getValor(), t.getLinea(), t.getColumna());

            case PARENTESIS_IZQ:
                consumir();
                Nodo expr = parsearExpresion();
                consumir(TipoToken.PARENTESIS_DER, "')' para cerrar la expresion");
                return expr;

            default:
                throw new ErrorSintactico(
                    "Expresion no valida: token inesperado '" + t.getValor() + "'",
                    t.getLinea(), t.getColumna()
                );
        }
    }
}
