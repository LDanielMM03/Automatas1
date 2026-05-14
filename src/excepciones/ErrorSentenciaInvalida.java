package excepciones;

public class ErrorSentenciaInvalida extends ErrorSintactico {

    public ErrorSentenciaInvalida(String tokenEncontrado, int linea, int columna) {
        super("Sentencia no valida: token inesperado '" + tokenEncontrado + "'", linea, columna);
    }
}
