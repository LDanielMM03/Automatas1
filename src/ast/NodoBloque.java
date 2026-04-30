package ast;

import java.util.List;

/** Nodo de bloque: { sentencias* } */
public class NodoBloque extends Nodo {

    private final List<Nodo> sentencias;

    public NodoBloque(List<Nodo> sentencias, int linea, int columna) {
        super(linea, columna);
        this.sentencias = sentencias;
    }

    public List<Nodo> getSentencias() { return sentencias; }
}
