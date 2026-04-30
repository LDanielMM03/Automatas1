package excepciones;

public class ErrorLexico extends Exception {

    private final int linea;
    private final int columna;

    public ErrorLexico(String mensaje, int linea, int columna) {
        super(String.format("[Error Lexico] Linea %d, Columna %d: %s", linea, columna, mensaje));
        this.linea   = linea;
        this.columna = columna;
    }

    public int getLinea()   { return linea;   }
    public int getColumna() { return columna; }
}
