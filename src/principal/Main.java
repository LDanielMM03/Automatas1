package principal;

import excepciones.ErrorLexico;
import excepciones.ErrorSintactico;
import excepciones.ErrorSemantico;
import excepciones.ErrorDivisionPorCero;
import lexico.AnalizadorLexico;
import semantico.AnalizadorSemantico;
import sintactico.AnalizadorSintactico;
import ast.NodoPrograma;
import tokens.Token;

import javax.swing.SwingUtilities;
import java.util.List;

public class Main {

    
    

    public static void main(String[] args) {
        // Lanzar interfaz grafica
        SwingUtilities.invokeLater(InterfazGrafica::new);
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

        } catch (ErrorDivisionPorCero e) {
            System.err.println(e.getMessage());
            System.err.println(">>> Ejecucion fallida.");
        }
        System.out.println();
    }
}
