package ast;

/** Nodo de salida: mostrar(expresion); */
public class NodoMostrar extends Nodo {

    private final Nodo expresion;

    public NodoMostrar(Nodo expresion, int linea, int columna) {
        super(linea, columna);
        this.expresion = expresion;
    }

    public Nodo getExpresion() { return expresion; }
}
