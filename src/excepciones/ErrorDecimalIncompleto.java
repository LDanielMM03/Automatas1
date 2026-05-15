package excepciones;

public class ErrorDecimalIncompleto extends ErrorLexico {

    public ErrorDecimalIncompleto(int linea, int columna) {
        super("Decimal incompleto: se esperaban digitos despues del punto decimal", linea, columna);
    }
}
