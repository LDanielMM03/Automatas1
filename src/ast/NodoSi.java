package ast;

/** Nodo de condicional: si (condicion) { } sino { } */
public class NodoSi extends Nodo {

    private final Nodo      condicion;
    private final NodoBloque cuerpoVerdad;
    private final NodoBloque cuerpoFalso;   // null si no hay sino

    public NodoSi(Nodo condicion, NodoBloque cuerpoVerdad, NodoBloque cuerpoFalso,
                  int linea, int columna) {
        super(linea, columna);
        this.condicion    = condicion;
        this.cuerpoVerdad = cuerpoVerdad;
        this.cuerpoFalso  = cuerpoFalso;
    }

    public Nodo       getCondicion()    { return condicion;    }
    public NodoBloque getCuerpoVerdad() { return cuerpoVerdad; }
    public NodoBloque getCuerpoFalso()  { return cuerpoFalso;  }
    public boolean    tieneSino()       { return cuerpoFalso != null; }
}
