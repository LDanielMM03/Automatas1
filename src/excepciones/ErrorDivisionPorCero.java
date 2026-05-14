package excepciones;

public class ErrorDivisionPorCero extends RuntimeException {

    private final int linea;
    private final int columna;

    public ErrorDivisionPorCero(int linea, int columna) {
        super(String.format("[Error en Ejecucion] Linea %d, Columna %d: Division por cero",
              linea, columna));
        this.linea   = linea;
        this.columna = columna;
    }

    public int getLinea()   { return linea;   }
    public int getColumna() { return columna; }
}
