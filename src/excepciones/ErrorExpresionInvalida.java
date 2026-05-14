package excepciones;

public class ErrorExpresionInvalida extends ErrorSintactico {

    public ErrorExpresionInvalida(String tokenEncontrado, int linea, int columna) {
        super("Expresion no valida: token inesperado '" + tokenEncontrado + "'", linea, columna);
    }
}
