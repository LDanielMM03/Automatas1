package tokens;

public enum TipoToken {

    // ── Tipos de dato del lenguaje personalizado ──────────────────────────────
    ENTERO,          // perro   (reemplaza a int)
    DECIMAL,         // gato    (reemplaza a double)
    CADENA_TIPO,     // Pez     (reemplaza a String)
    BOOLEANO,        // boolean

    // ── Literales ─────────────────────────────────────────────────────────────
    LIT_ENTERO,      // 42
    LIT_DECIMAL,     // 3.14
    LIT_CADENA,      // "hola mundo"
    LIT_VERDADERO,   // true
    LIT_FALSO,       // false

    // ── Identificador ─────────────────────────────────────────────────────────
    IDENTIFICADOR,   // nombre de variable

    // ── Operador de asignacion personalizado ──────────────────────────────────
    ASIGNACION,      // e   (reemplaza a =)

    // ── Operadores aritmeticos ────────────────────────────────────────────────
    SUMA,            // +
    RESTA,           // -
    MULTIPLICACION,  // *
    DIVISION,        // /
    MODULO,          // %

    // ── Operadores relacionales ───────────────────────────────────────────────
    IGUAL_IGUAL,     // ==
    DIFERENTE,       // !=
    MAYOR,           // >
    MENOR,           // <
    MAYOR_IGUAL,     // >=
    MENOR_IGUAL,     // <=

    // ── Operadores logicos ────────────────────────────────────────────────────
    Y,               // &&
    O,               // ||
    NO,              // !

    // ── Control de flujo ──────────────────────────────────────────────────────
    SI,              // if
    SINO,            // else
    MIENTRAS,        // while

    // ── Entrada / Salida ──────────────────────────────────────────────────────
    MOSTRAR,         // System.out.println

    // ── Puntuacion ────────────────────────────────────────────────────────────
    PUNTO_COMA,      // ;
    COMA,            // ,
    PARENTESIS_IZQ,  // (
    PARENTESIS_DER,  // )
    LLAVE_IZQ,       // {
    LLAVE_DER,       // }

    // ── Fin de archivo ────────────────────────────────────────────────────────
    EOF
}
