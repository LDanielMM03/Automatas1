package principal;

import excepciones.ErrorLexico;
import excepciones.ErrorSintactico;
import excepciones.ErrorSemantico;
import lexico.AnalizadorLexico;
import semantico.AnalizadorSemantico;
import sintactico.AnalizadorSintactico;
import ast.NodoPrograma;
import tokens.Token;

import javax.swing.SwingUtilities;
import java.util.List;
import java.util.Scanner;

public class Main {

    // =========================================================================
    //  Codigo de ejemplo correcto
    // =========================================================================
    private static final String CODIGO_CORRECTO =
        "# Programa de ejemplo\n" +
        "perro   contador e 5;\n" +
        "gato    precio   e 19.99;\n" +
        "Pez     nombre   e \"Luis\";\n" +
        "boolean activo   e true;\n" +
        "\n" +
        "# Condicional\n" +
        "if (contador > 3) {\n" +
        "    System.out.println(\"Contador mayor que 3\");\n" +
        "} else {\n" +
        "    System.out.println(\"Contador menor o igual a 3\");\n" +
        "}\n" +
        "\n" +
        "# Ciclo while\n" +
        "while (contador > 0) {\n" +
        "    contador e contador - 1;\n" +
        "}\n" +
        "\n" +
        "# Expresion aritmetica\n" +
        "gato resultado e precio * 2;\n" +
        "System.out.println(resultado);\n" +
        "System.out.println(nombre);\n";

    // =========================================================================
    //  Ejemplos con errores
    // =========================================================================
    private static final String CODIGO_ERROR_LEXICO =
        "perro x e 10;\n" +
        "perro y e @;\n";          // '@' no es un caracter valido

    private static final String CODIGO_ERROR_SINTACTICO =
        "perro x e 10\n" +         // falta el ';'
        "perro y e 5;\n";

    private static final String CODIGO_ERROR_SEMANTICO_TIPO =
        "perro numero e 3.14;\n";  // no se puede asignar gato a perro

    private static final String CODIGO_ERROR_SEMANTICO_NODECLARADA =
        "x e 100;\n";              // 'x' no fue declarada

    private static final String CODIGO_ERROR_SEMANTICO_REDECLARACION =
        "perro x e 1;\n" +
        "perro x e 2;\n";          // 'x' ya existe en este ambito

    // =========================================================================
    //  Main
    // =========================================================================

    public static void main(String[] args) {
        // Lanzar interfaz grafica
        SwingUtilities.invokeLater(InterfazGrafica::new);
    }

    public static void mainConsola(String[] args) {
        System.out.println("=================================================");
        System.out.println("  LENGUAJE PETS");
        System.out.println("  Tipos: perro (int), gato (double), Pez (String), boolean");
        System.out.println("  Asignacion: e  (reemplaza a =)");
        System.out.println("  Comentarios: #");
        System.out.println("=================================================\n");

        System.out.println("--- TEST 1: Codigo correcto ---");
        System.out.println(CODIGO_CORRECTO);
        compilar(CODIGO_CORRECTO);

        System.out.println("\n--- TEST 2: Error Lexico ---");
        System.out.println(CODIGO_ERROR_LEXICO);
        compilar(CODIGO_ERROR_LEXICO);

        System.out.println("\n--- TEST 3: Error Sintactico ---");
        System.out.println(CODIGO_ERROR_SINTACTICO);
        compilar(CODIGO_ERROR_SINTACTICO);

        System.out.println("\n--- TEST 4: Error Semantico (tipos incompatibles) ---");
        System.out.println(CODIGO_ERROR_SEMANTICO_TIPO);
        compilar(CODIGO_ERROR_SEMANTICO_TIPO);

        System.out.println("\n--- TEST 5: Error Semantico (variable no declarada) ---");
        System.out.println(CODIGO_ERROR_SEMANTICO_NODECLARADA);
        compilar(CODIGO_ERROR_SEMANTICO_NODECLARADA);

        System.out.println("\n--- TEST 6: Error Semantico (redeclaracion) ---");
        System.out.println(CODIGO_ERROR_SEMANTICO_REDECLARACION);
        compilar(CODIGO_ERROR_SEMANTICO_REDECLARACION);

        System.out.println("\n=================================================");
        System.out.print("Deseas ingresar tu propio codigo? (s/n): ");
        Scanner sc = new Scanner(System.in);
        if (sc.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("Escribe tu codigo.");
            System.out.println("Escribe FIN en una linea nueva para terminar.\n");
            StringBuilder sb = new StringBuilder();
            String linea;
            while (!(linea = sc.nextLine()).equals("FIN")) {
                sb.append(linea).append("\n");
            }
            System.out.println("\n--- Analizando tu codigo ---");
            compilar(sb.toString());
        }
        sc.close();
    }

    // =========================================================================
    //  Pipeline: Lexico -> Sintactico -> Semantico
    // =========================================================================

    public static void compilar(String codigo) {
        try {
            AnalizadorLexico lexer = new AnalizadorLexico(codigo);
            List<Token> tokens = lexer.tokenizar();
            System.out.println("[Lexico]    OK - " + (tokens.size() - 1) + " tokens generados.");

            AnalizadorSintactico parser = new AnalizadorSintactico(tokens);
            NodoPrograma ast = parser.parsear();
            System.out.println("[Sintactico] OK - AST construido.");

            AnalizadorSemantico semantico = new AnalizadorSemantico();
            semantico.analizar(ast);
            System.out.println("[Semantico]  OK - Sin errores de tipos.\n");

            System.out.println(">>> Compilacion exitosa.");

        } catch (ErrorLexico e) {
            System.err.println(e.getMessage());
            System.err.println(">>> Compilacion fallida.");

        } catch (ErrorSintactico e) {
            System.err.println(e.getMessage());
            System.err.println(">>> Compilacion fallida.");

        } catch (ErrorSemantico e) {
            System.err.println(e.getMessage());
            System.err.println(">>> Compilacion fallida.");
        }
        System.out.println();
    }
}
