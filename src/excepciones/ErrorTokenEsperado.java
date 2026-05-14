package excepciones;

public class ErrorTokenEsperado extends ErrorSintactico {

    public ErrorTokenEsperado(String esperado, String encontrado, int linea, int columna) {
        super("Se esperaba " + esperado + " pero se encontro '" + encontrado + "'", linea, columna);
    }
}
