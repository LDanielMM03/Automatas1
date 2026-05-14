package excepciones;

public class ErrorRangoNumero extends ErrorLexico {

    public ErrorRangoNumero(String detalle, int linea, int columna) {
        super("Numero fuera de rango: " + detalle, linea, columna);
    }
}
