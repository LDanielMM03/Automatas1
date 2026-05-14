package excepciones;

public class ErrorCondicionNoBoolena extends ErrorSemantico {

    public ErrorCondicionNoBoolena(String estructura, String tipoEncontrado, int linea, int columna) {
        super("La condicion del '" + estructura + "' debe ser de tipo boolean, pero es '" +
              tipoEncontrado + "'", linea, columna);
    }
}
