package lexico;

import excepciones.ErrorLexico;
import excepciones.ErrorCadenaNoTerminada;
import excepciones.ErrorCaracterDesconocido;
import excepciones.ErrorSecuenciaEscapeInvalida;
import excepciones.ErrorRangoNumero;
import excepciones.ErrorDecimalIncompleto;
import tokens.TipoToken;
import tokens.Token;
import java.util.ArrayList;
import java.util.List;

public class AnalizadorLexico {

    private final String fuente;
    private int pos;
    private int linea;
    private int columna;
    private final List<Token> tokensParciales = new ArrayList<>();

    public AnalizadorLexico(String fuente) {
        this.fuente  = fuente;
        this.pos     = 0;
        this.linea   = 1;
        this.columna = 1;
    }

    public List<Token> getTokensParciales() {
        return tokensParciales;
    }

    // =========================================================================
    //  Metodo principal
    // =========================================================================

    public List<Token> tokenizar() throws ErrorLexico {
        List<Token> lista = new ArrayList<>();

        while (hayMas()) {
            saltarEspaciosYComentarios();
            if (!hayMas()) break;

            char c = actual();

            Token tok;
            if (Character.isDigit(c))
                tok = leerNumero();
            else if (c == '"')
                tok = leerCadena();
            else if (Character.isLetter(c) || c == '_')
                tok = leerPalabra();
            else
                tok = leerSimbolo();
            tokensParciales.add(tok);
            lista.add(tok);
        }

        lista.add(new Token(TipoToken.EOF, "", linea, columna));
        return lista;
    }

    // =========================================================================
    //  Helpers de navegacion
    // =========================================================================

    private boolean hayMas() {
        return pos < fuente.length();
    }

    private char actual() {
        return hayMas() ? fuente.charAt(pos) : '\0';
    }

    private char siguiente() {
        return (pos + 1 < fuente.length()) ? fuente.charAt(pos + 1) : '\0';
    }

    private char consumir() {
        char c = actual();
        pos++;
        if (c == '\n') { linea++; columna = 1; }
        else           { columna++;            }
        return c;
    }

    // =========================================================================
    //  Saltar espacios en blanco y comentarios de linea (#)
    // =========================================================================

    private void saltarEspaciosYComentarios() {
        while (hayMas()) {
            if (Character.isWhitespace(actual())) {
                consumir();
            } else if (actual() == '#') {
                while (hayMas() && actual() != '\n') consumir();
            } else {
                break;
            }
        }
    }

    // =========================================================================
    //  Leer numero entero o decimal
    // =========================================================================

    private Token leerNumero() throws ErrorLexico {
        int lin = linea, col = columna;
        StringBuilder parteEntera = new StringBuilder();
        boolean esDecimal = false;

        while (hayMas() && Character.isDigit(actual()))
            parteEntera.append(consumir());

        if (parteEntera.length() > 10)
            throw new ErrorRangoNumero(
                "la parte entera tiene " + parteEntera.length() +
                " digitos (maximo 10)", lin, col);

        if (hayMas() && actual() == '.') {
            if (Character.isDigit(siguiente())) {
                esDecimal = true;
                consumir(); // consume el punto
                StringBuilder parteDecimal = new StringBuilder();
                while (hayMas() && Character.isDigit(actual()))
                    parteDecimal.append(consumir());

                if (parteDecimal.length() > 8)
                    throw new ErrorRangoNumero(
                        "la parte decimal tiene " + parteDecimal.length() +
                        " digitos (maximo 8)", lin, col);

                parteEntera.append('.').append(parteDecimal);
            } else {
                throw new ErrorDecimalIncompleto(lin, col);
            }
        }

        TipoToken tipo = esDecimal ? TipoToken.LIT_DECIMAL : TipoToken.LIT_ENTERO;
        StringBuilder sb = parteEntera;
        return new Token(tipo, sb.toString(), lin, col);
    }

    // =========================================================================
    //  Leer cadena de texto entre comillas dobles
    // =========================================================================

    private Token leerCadena() throws ErrorLexico {
        int lin = linea, col = columna;
        consumir(); // consume la comilla de apertura "
        StringBuilder sb = new StringBuilder();

        while (hayMas() && actual() != '"') {
            if (actual() == '\n')
                throw new ErrorCadenaNoTerminada(lin, col);

            if (actual() == '\\') {
                consumir(); // consume la barra
                switch (actual()) {
                    case 'n':  sb.append('\n'); consumir(); break;
                    case 't':  sb.append('\t'); consumir(); break;
                    case '"':  sb.append('"');  consumir(); break;
                    case '\\': sb.append('\\'); consumir(); break;
                    default:
                        throw new ErrorSecuenciaEscapeInvalida(actual(), linea, columna);
                }
            } else {
                sb.append(consumir());
            }
        }

        if (!hayMas())
            throw new ErrorCadenaNoTerminada(lin, col);

        consumir(); // consume la comilla de cierre "
        return new Token(TipoToken.LIT_CADENA, sb.toString(), lin, col);
    }

    // =========================================================================
    //  Leer palabra: puede ser palabra reservada o identificador
    // =========================================================================

    private Token leerPalabra() throws ErrorLexico {
        int lin = linea, col = columna;
        StringBuilder sb = new StringBuilder();

        while (hayMas() && (Character.isLetterOrDigit(actual()) || actual() == '_'))
            sb.append(consumir());

        String pal = sb.toString();

        switch (pal) {
            // Tipos de dato
            case "perro":   return new Token(TipoToken.ENTERO,       pal, lin, col);
            case "gato":    return new Token(TipoToken.DECIMAL,      pal, lin, col);
            case "pez":     return new Token(TipoToken.CADENA_TIPO,  pal, lin, col);
            case "boolean": return new Token(TipoToken.BOOLEANO,     pal, lin, col);

            // Control de flujo
            case "if":      return new Token(TipoToken.SI,           pal, lin, col);
            case "else":    return new Token(TipoToken.SINO,         pal, lin, col);
            case "while":   return new Token(TipoToken.MIENTRAS,     pal, lin, col);

            // Salida
            case "print":   return new Token(TipoToken.PRINT,   pal, lin, col);
            case "println": return new Token(TipoToken.PRINTLN, pal, lin, col);

            // Literales booleanos
            case "true":    return new Token(TipoToken.LIT_VERDADERO, pal, lin, col);
            case "false":   return new Token(TipoToken.LIT_FALSO,     pal, lin, col);

            // Operador de asignacion
            case "e":       return new Token(TipoToken.ASIGNACION,    pal, lin, col);

            // Cualquier otra palabra es un identificador
            default:        return new Token(TipoToken.IDENTIFICADOR,  pal, lin, col);
        }
    }

    // =========================================================================
    //  Leer simbolo u operador
    // =========================================================================

    private Token leerSimbolo() throws ErrorLexico {
        int lin = linea, col = columna;
        char c = consumir();

        switch (c) {
            case '+': return new Token(TipoToken.SUMA,            "+",  lin, col);
            case '-': return new Token(TipoToken.RESTA,           "-",  lin, col);
            case '*': return new Token(TipoToken.MULTIPLICACION,  "*",  lin, col);
            case '/': return new Token(TipoToken.DIVISION,        "/",  lin, col);
            case '%': return new Token(TipoToken.MODULO,          "%",  lin, col);
            case ';': return new Token(TipoToken.PUNTO_COMA,      ";",  lin, col);
            case ',': return new Token(TipoToken.COMA,            ",",  lin, col);
            case '(': return new Token(TipoToken.PARENTESIS_IZQ,  "(",  lin, col);
            case ')': return new Token(TipoToken.PARENTESIS_DER,  ")",  lin, col);
            case '{': return new Token(TipoToken.LLAVE_IZQ,       "{",  lin, col);
            case '}': return new Token(TipoToken.LLAVE_DER,       "}",  lin, col);

            case '=':
                if (actual() == '=') {
                    consumir();
                    return new Token(TipoToken.IGUAL_IGUAL, "==", lin, col);
                }
                throw new ErrorLexico(
                    "Caracter '=' no valido. Para comparar use '==', para asignar use 'e'", lin, col);

            case '!':
                if (actual() == '=') {
                    consumir();
                    return new Token(TipoToken.DIFERENTE, "!=", lin, col);
                }
                return new Token(TipoToken.NO, "!", lin, col);

            case '&':
                if (actual() == '&') {
                    consumir();
                    return new Token(TipoToken.Y, "&&", lin, col);
                }
                throw new ErrorLexico("Se esperaba '&&' despues de '&'", lin, col);

            case '|':
                if (actual() == '|') {
                    consumir();
                    return new Token(TipoToken.O, "||", lin, col);
                }
                throw new ErrorLexico("Se esperaba '||' despues de '|'", lin, col);

            case '>':
                if (actual() == '=') { consumir(); return new Token(TipoToken.MAYOR_IGUAL, ">=", lin, col); }
                return new Token(TipoToken.MAYOR, ">", lin, col);

            case '<':
                if (actual() == '=') { consumir(); return new Token(TipoToken.MENOR_IGUAL, "<=", lin, col); }
                return new Token(TipoToken.MENOR, "<", lin, col);

            default:
                throw new ErrorCaracterDesconocido(c, lin, col);
        }
    }
}
