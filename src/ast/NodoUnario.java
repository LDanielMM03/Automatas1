package ast;

/** Nodo de operacion unaria: operador operando  (ej: no verdadero, -5) */
public class NodoUnario extends Nodo {

    private final String operador;
    private final Nodo   operando;

    public NodoUnario(String operador, Nodo operando, int linea, int columna) {
        super(linea, columna);
        this.operador = operador;
        this.operando = operando;
    }

    public String getOperador() { return operador; }
    public Nodo   getOperando() { return operando; }
}
