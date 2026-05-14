package excepciones;

public class ErrorOperadorInvalido extends ErrorSemantico {

    public ErrorOperadorInvalido(String operador, String lado, String tipo, int linea, int columna) {
        super("Operador '" + operador + "' requiere tipo numerico, pero el lado " +
              lado + " es '" + tipo + "'", linea, columna);
    }

    public ErrorOperadorInvalido(String operador, String tipoEncontrado, int linea, int columna) {
        super("Operador '" + operador + "' no aplicable al tipo '" + tipoEncontrado + "'",
              linea, columna);
    }
}
