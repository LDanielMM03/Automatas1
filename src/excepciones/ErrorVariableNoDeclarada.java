package excepciones;

public class ErrorVariableNoDeclarada extends ErrorSemantico {

    public ErrorVariableNoDeclarada(String nombre, int linea, int columna) {
        super("La variable '" + nombre + "' no ha sido declarada", linea, columna);
    }
}
