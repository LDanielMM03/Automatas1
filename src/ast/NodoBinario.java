package ast;

/** Nodo de operacion binaria: izquierda operador derecha */
public class NodoBinario extends Nodo {

    private final String operador;
    private final Nodo   izquierda;
    private final Nodo   derecha;

    public NodoBinario(String operador, Nodo izquierda, Nodo derecha, int linea, int columna) {
        super(linea, columna);
        this.operador  = operador;
        this.izquierda = izquierda;
        this.derecha   = derecha;
    }

    public String getOperador()  { return operador;  }
    public Nodo   getIzquierda() { return izquierda; }
    public Nodo   getDerecha()   { return derecha;   }
}
