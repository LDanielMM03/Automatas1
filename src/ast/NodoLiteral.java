package ast;

/** Nodo de valor literal: 42, 3.14, "texto", verdadero, falso */
public class NodoLiteral extends Nodo {

    private final Object valor;
    private final String tipo;   // "entero" | "decimal" | "cadena" | "booleano"

    public NodoLiteral(Object valor, String tipo, int linea, int columna) {
        super(linea, columna);
        this.valor = valor;
        this.tipo  = tipo;
    }

    public Object getValor() { return valor; }
    public String getTipo()  { return tipo;  }
}
