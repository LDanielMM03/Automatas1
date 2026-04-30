package ast;

import java.util.List;

/** Nodo raiz: representa el programa completo. */
public class NodoPrograma extends Nodo {

    private final List<Nodo> sentencias;

    public NodoPrograma(List<Nodo> sentencias, int linea, int columna) {
        super(linea, columna);
        this.sentencias = sentencias;
    }

    public List<Nodo> getSentencias() { return sentencias; }
}
