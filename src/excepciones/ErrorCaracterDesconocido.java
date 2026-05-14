package excepciones;

public class ErrorCaracterDesconocido extends ErrorLexico {

    public ErrorCaracterDesconocido(char caracter, int linea, int columna) {
        super("Caracter desconocido: '" + caracter + "'", linea, columna);
    }
}
