package ast;

/** Nodo de declaracion de variable: tipo nombre <- expresion; */
public class NodoDeclaracion extends Nodo {

    private final String tipo;
    private final String nombre;
    private final Nodo   expresion;  // null si no hay valor inicial

    public NodoDeclaracion(String tipo, String nombre, Nodo expresion, int linea, int columna) {
        super(linea, columna);
        this.tipo      = tipo;
        this.nombre    = nombre;
        this.expresion = expresion;
    }

    public String getTipo()      { return tipo;            }
    public String getNombre()    { return nombre;          }
    public Nodo   getExpresion() { return expresion;       }
    public boolean tieneValor()  { return expresion != null; }
}
