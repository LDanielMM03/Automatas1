package ast;

/** Nodo de referencia a variable: nombre */
public class NodoIdentificador extends Nodo {

    private final String nombre;

    public NodoIdentificador(String nombre, int linea, int columna) {
        super(linea, columna);
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }
}
