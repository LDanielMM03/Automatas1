package excepciones;

public class ErrorSecuenciaEscapeInvalida extends ErrorLexico {

    public ErrorSecuenciaEscapeInvalida(char secuencia, int linea, int columna) {
        super("Secuencia de escape desconocida: \\" + secuencia, linea, columna);
    }
}
