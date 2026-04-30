package ast;

/** Nodo de ciclo: mientras (condicion) { } */
public class NodoMientras extends Nodo {

    private final Nodo       condicion;
    private final NodoBloque cuerpo;

    public NodoMientras(Nodo condicion, NodoBloque cuerpo, int linea, int columna) {
        super(linea, columna);
        this.condicion = condicion;
        this.cuerpo    = cuerpo;
    }

    public Nodo       getCondicion() { return condicion; }
    public NodoBloque getCuerpo()    { return cuerpo;    }
}
