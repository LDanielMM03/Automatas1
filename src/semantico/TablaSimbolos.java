package semantico;

import java.util.HashMap;
import java.util.Map;

/**
 * Tabla de simbolos para el analizador semantico.
 * Soporta ambitos anidados mediante una referencia al ambito padre.
 */
public class TablaSimbolos {

    private final Map<String, Simbolo> tabla;
    private final TablaSimbolos        padre;

    public TablaSimbolos(TablaSimbolos padre) {
        this.tabla = new HashMap<>();
        this.padre = padre;
    }

    // ─── Declarar variable en el ambito actual ────────────────────────────────

    public void declarar(String nombre, String tipo) {
        tabla.put(nombre, new Simbolo(nombre, tipo));
    }

    // ─── Verificar si la variable existe en cualquier ambito ──────────────────

    public boolean estaDeclarada(String nombre) {
        if (tabla.containsKey(nombre)) return true;
        return padre != null && padre.estaDeclarada(nombre);
    }

    // ─── Verificar si la variable existe SOLO en el ambito actual ────────────

    public boolean estaDeclaradaLocalmente(String nombre) {
        return tabla.containsKey(nombre);
    }

    // ─── Obtener simbolo desde cualquier ambito ───────────────────────────────

    public Simbolo obtener(String nombre) {
        if (tabla.containsKey(nombre)) return tabla.get(nombre);
        if (padre != null)             return padre.obtener(nombre);
        return null;
    }

    // =========================================================================
    //  Clase interna: Simbolo
    // =========================================================================

    public static class Simbolo {
        private final String nombre;
        private final String tipo;

        public Simbolo(String nombre, String tipo) {
            this.nombre = nombre;
            this.tipo   = tipo;
        }

        public String getNombre() { return nombre; }
        public String getTipo()   { return tipo;   }

        @Override
        public String toString() {
            return String.format("Simbolo[%s : %s]", nombre, tipo);
        }
    }
}
