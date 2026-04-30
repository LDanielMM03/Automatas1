package ast;

/** Nodo de asignacion: nombre <- expresion; */
public class NodoAsignacion extends Nodo {

    private final String nombre;
    private final Nodo   expresion;

    public NodoAsignacion(String nombre, Nodo expresion, int linea, int columna) {
        super(linea, columna);
        this.nombre    = nombre;
        this.expresion = expresion;
    }

    public String getNombre()    { return nombre;    }
    public Nodo   getExpresion() { return expresion; }
}
