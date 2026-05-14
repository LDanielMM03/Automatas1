package ast;

/** Nodo de salida: print(expresion); o println(expresion); */
public class NodoMostrar extends Nodo {

    private final Nodo    expresion;
    private final boolean conSaltoDeLinea;

    public NodoMostrar(Nodo expresion, boolean conSaltoDeLinea, int linea, int columna) {
        super(linea, columna);
        this.expresion       = expresion;
        this.conSaltoDeLinea = conSaltoDeLinea;
    }

    public Nodo    getExpresion()       { return expresion;       }
    public boolean tienesSaltoDeLinea() { return conSaltoDeLinea; }
}
