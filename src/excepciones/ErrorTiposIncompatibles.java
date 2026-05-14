package excepciones;

public class ErrorTiposIncompatibles extends ErrorSemantico {

    public ErrorTiposIncompatibles(String tipoOrigen, String tipoDestino, int linea, int columna) {
        super("Tipos incompatibles: no se puede asignar '" + tipoOrigen +
              "' a una variable de tipo '" + tipoDestino + "'", linea, columna);
    }

    public ErrorTiposIncompatibles(String operador, String tipoIzq, String tipoDer, int linea, int columna) {
        super("Operador '" + operador + "' no es aplicable entre '" + tipoIzq + "' y '" + tipoDer + "'",
              linea, columna);
    }
}
