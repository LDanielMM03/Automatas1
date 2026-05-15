package excepciones;

public class ErrorVariableNoDeclarada extends ErrorSemantico {

    public ErrorVariableNoDeclarada(String nombre, int linea, int columna) {
        super(buildMessage(nombre), linea, columna);
    }

    private static String buildMessage(String nombre) {
        String base = "La variable '" + nombre + "' no ha sido declarada";
        if (nombre.matches("[a-zA-Z]\\d+")) {
            return base + ". Si intentabas escribir el numero " + nombre.substring(1) +
                   ", los literales numericos no pueden comenzar con letras";
        }
        return base + ". Declara la variable antes de usarla (ejemplo: perro " + nombre + ";)";
    }
}
