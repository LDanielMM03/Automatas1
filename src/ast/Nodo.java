package ast;

/**
 * Clase base de todos los nodos del Arbol de Sintaxis Abstracta (AST).
 */
public abstract class Nodo {

    private final int linea;
    private final int columna;

    protected Nodo(int linea, int columna) {
        this.linea   = linea;
        this.columna = columna;
    }

    public int getLinea()   { return linea;   }
    public int getColumna() { return columna; }
}
