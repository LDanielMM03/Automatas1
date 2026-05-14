package excepciones;

public class ErrorCadenaNoTerminada extends ErrorLexico {

    public ErrorCadenaNoTerminada(int linea, int columna) {
        super("Cadena de texto no cerrada, se esperaba '\"'", linea, columna);
    }
}
