package excepciones;

public class ErrorVariableRedeclarada extends ErrorSemantico {

    public ErrorVariableRedeclarada(String nombre, int linea, int columna) {
        super("La variable '" + nombre + "' ya fue declarada en este ambito", linea, columna);
    }
}
