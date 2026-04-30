package principal;

import lexico.AnalizadorLexico;
import sintactico.AnalizadorSintactico;
import semantico.AnalizadorSemantico;
import tokens.Token;
import tokens.TipoToken;
import ast.NodoPrograma;
import excepciones.ErrorLexico;
import excepciones.ErrorSintactico;
import excepciones.ErrorSemantico;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class InterfazGrafica extends JFrame {

    private JTextArea editorCodigo;
    private JTextArea lineNumbers;
    private JTable tablaTokens;
    private DefaultTableModel modeloTabla;
    private JTextArea consoleOutput;
    private JTextArea resultadosOutput;

    private static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 13);
    private static final Color COLOR_LINEAS = new Color(230, 230, 230);
    private static final Color COLOR_EDITOR  = Color.WHITE;
    private static final Color ROW_ALT       = new Color(245, 248, 255);

    public InterfazGrafica() {
        setTitle("Interprete - IDE Lexico/Sintactico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }

    // =========================================================================
    //  Construccion de la interfaz
    // =========================================================================

    private void initComponents() {
        setLayout(new BorderLayout());

        // ─── Panel izquierdo: editor + boton ────────────────────────────────
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Editor de texto"));

        editorCodigo = new JTextArea();
        editorCodigo.setFont(MONO);
        editorCodigo.setTabSize(4);
        editorCodigo.setBackground(COLOR_EDITOR);
        editorCodigo.setMargin(new Insets(2, 4, 2, 4));

        lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(COLOR_LINEAS);
        lineNumbers.setForeground(new Color(120, 120, 120));
        lineNumbers.setFont(MONO);
        lineNumbers.setEditable(false);
        lineNumbers.setFocusable(false);
        lineNumbers.setMargin(new Insets(2, 6, 2, 6));
        lineNumbers.setHighlighter(null);

        editorCodigo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { actualizarNumeros(); }
            public void removeUpdate(DocumentEvent e)  { actualizarNumeros(); }
            public void changedUpdate(DocumentEvent e) { actualizarNumeros(); }
        });

        JScrollPane editorScroll = new JScrollPane(editorCodigo);
        editorScroll.setRowHeaderView(lineNumbers);
        editorScroll.setBorder(BorderFactory.createEmptyBorder());

        JButton btnAnalizar = new JButton("Analizar");
        btnAnalizar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnAnalizar.setPreferredSize(new Dimension(0, 36));
        btnAnalizar.addActionListener(this::analizar);

        leftPanel.add(editorScroll, BorderLayout.CENTER);
        leftPanel.add(btnAnalizar, BorderLayout.SOUTH);

        // ─── Panel derecho: tabla de tokens ─────────────────────────────────
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Tabla de Tokens"));

        String[] columnas = {"Token", "Lexema", "Patron", "Palabra reservada"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaTokens = new JTable(modeloTabla);
        tablaTokens.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        tablaTokens.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        tablaTokens.setRowHeight(22);
        tablaTokens.setFillsViewportHeight(true);
        tablaTokens.setShowGrid(true);
        tablaTokens.setGridColor(new Color(210, 210, 210));

        // Colores alternos en filas
        tablaTokens.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                }
                return this;
            }
        });

        tablaTokens.getColumnModel().getColumn(0).setPreferredWidth(160);
        tablaTokens.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaTokens.getColumnModel().getColumn(2).setPreferredWidth(180);
        tablaTokens.getColumnModel().getColumn(3).setPreferredWidth(120);

        rightPanel.add(new JScrollPane(tablaTokens), BorderLayout.CENTER);

        // ─── Split horizontal superior ───────────────────────────────────────
        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        topSplit.setResizeWeight(0.4);
        topSplit.setDividerSize(5);

        // ─── Paneles inferiores ──────────────────────────────────────────────
        JPanel consolePanel = new JPanel(new BorderLayout());
        consolePanel.setBorder(BorderFactory.createTitledBorder("Consola / Output (propio IDE)"));
        consoleOutput = makeOutputArea(new Color(248, 248, 248));
        consolePanel.add(new JScrollPane(consoleOutput), BorderLayout.CENTER);

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Resultados del programa"));
        resultadosOutput = makeOutputArea(Color.WHITE);
        resultPanel.add(new JScrollPane(resultadosOutput), BorderLayout.CENTER);

        JSplitPane bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, consolePanel, resultPanel);
        bottomSplit.setResizeWeight(0.5);
        bottomSplit.setDividerSize(5);

        // ─── Split vertical principal ────────────────────────────────────────
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, bottomSplit);
        mainSplit.setResizeWeight(0.62);
        mainSplit.setDividerSize(5);

        add(mainSplit, BorderLayout.CENTER);
    }

    private JTextArea makeOutputArea(Color bg) {
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        ta.setBackground(bg);
        ta.setMargin(new Insets(4, 6, 4, 6));
        return ta;
    }

    // =========================================================================
    //  Numeros de linea
    // =========================================================================

    private void actualizarNumeros() {
        int lineas = editorCodigo.getLineCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lineas; i++) {
            sb.append(i);
            if (i < lineas) sb.append("\n");
        }
        lineNumbers.setText(sb.toString());
    }

    // =========================================================================
    //  Accion Analizar
    // =========================================================================

    private void analizar(ActionEvent e) {
        String codigo = editorCodigo.getText().trim();
        modeloTabla.setRowCount(0);
        consoleOutput.setText("");
        resultadosOutput.setText("");

        if (codigo.isEmpty()) {
            resultadosOutput.setText("Editor vacio. Escribe codigo primero.");
            return;
        }

        try {
            // Fase 1: Lexico
            AnalizadorLexico lexer = new AnalizadorLexico(codigo);
            List<Token> tokens = lexer.tokenizar();

            StringBuilder console = new StringBuilder();
            for (Token t : tokens) {
                if (t.getTipo() == TipoToken.EOF) continue;
                modeloTabla.addRow(new Object[]{
                    t.getValor(),
                    t.getValor(),
                    obtenerPatron(t),
                    esPalabraReservada(t.getTipo()) ? "Si" : "No"
                });
                console.append(describir(t)).append("\n");
            }
            consoleOutput.setText(console.toString());

            // Fase 2: Sintactico
            AnalizadorSintactico parser = new AnalizadorSintactico(tokens);
            NodoPrograma ast = parser.parsear();

            // Fase 3: Semantico
            AnalizadorSemantico semantico = new AnalizadorSemantico();
            semantico.analizar(ast);

            // Fase 4: Ejecucion
            Interprete interprete = new Interprete();
            String salidaPrograma = interprete.ejecutar(ast);

            StringBuilder resultado = new StringBuilder();
            resultado.append(">>> Compilacion exitosa.\n");
            resultado.append("[Lexico]     OK  -  ").append(tokens.size() - 1).append(" tokens\n");
            resultado.append("[Sintactico] OK  -  AST construido\n");
            resultado.append("[Semantico]  OK  -  Sin errores de tipos\n");
            if (!salidaPrograma.isEmpty()) {
                resultado.append("\n--- Salida del programa ---\n");
                resultado.append(salidaPrograma);
            }
            resultadosOutput.setText(resultado.toString());

        } catch (ErrorLexico ex) {
            resultadosOutput.setText("[ERROR LEXICO]\n\n" + ex.getMessage());
        } catch (ErrorSintactico ex) {
            resultadosOutput.setText("[ERROR SINTACTICO]\n\n" + ex.getMessage());
        } catch (ErrorSemantico ex) {
            resultadosOutput.setText("[ERROR SEMANTICO]\n\n" + ex.getMessage());
        }
    }

    // =========================================================================
    //  Descripcion humana de cada token (panel inferior izquierdo)
    // =========================================================================

    private String describir(Token t) {
        String v = t.getValor();
        switch (t.getTipo()) {
            case ENTERO:         return "'" + v + "' es palabra reservada  →  tipo perro (int)";
            case DECIMAL:        return "'" + v + "' es palabra reservada  →  tipo gato (double)";
            case CADENA_TIPO:    return "'" + v + "' es palabra reservada  →  tipo Pez (String)";
            case BOOLEANO:       return "'" + v + "' es palabra reservada  →  tipo boolean";
            case SI:             return "'" + v + "' es palabra reservada  →  condicional if";
            case SINO:           return "'" + v + "' es palabra reservada  →  condicional else";
            case MIENTRAS:       return "'" + v + "' es palabra reservada  →  bucle while";
            case MOSTRAR:        return "'" + v + "' es palabra reservada  →  salida System.out.println";
            case LIT_VERDADERO:  return "'" + v + "' es literal booleano verdadero";
            case LIT_FALSO:      return "'" + v + "' es literal booleano falso";
            case ASIGNACION:     return "'" + v + "' es operador de asignacion";
            case IDENTIFICADOR:  return "'" + v + "' es identificador (variable)";
            case LIT_ENTERO:     return "'" + v + "' es literal entero";
            case LIT_DECIMAL:    return "'" + v + "' es literal decimal";
            case LIT_CADENA:     return "\"" + v + "\" es literal cadena";
            case SUMA:           return "'" + v + "' es operador aritmetico  →  suma";
            case RESTA:          return "'" + v + "' es operador aritmetico  →  resta";
            case MULTIPLICACION: return "'" + v + "' es operador aritmetico  →  multiplicacion";
            case DIVISION:       return "'" + v + "' es operador aritmetico  →  division";
            case MODULO:         return "'" + v + "' es operador aritmetico  →  modulo";
            case IGUAL_IGUAL:    return "'" + v + "' es operador relacional  →  igual a";
            case DIFERENTE:      return "'" + v + "' es operador relacional  →  diferente de";
            case MAYOR:          return "'" + v + "' es operador relacional  →  mayor que";
            case MENOR:          return "'" + v + "' es operador relacional  →  menor que";
            case MAYOR_IGUAL:    return "'" + v + "' es operador relacional  →  mayor o igual";
            case MENOR_IGUAL:    return "'" + v + "' es operador relacional  →  menor o igual";
            case Y:              return "'" + v + "' es operador logico  →  AND";
            case O:              return "'" + v + "' es operador logico  →  OR";
            case NO:             return "'" + v + "' es operador logico  →  NOT";
            case PUNTO_COMA:     return "'" + v + "' es break";
            case COMA:           return "'" + v + "' es separador";
            case PARENTESIS_IZQ: return "'" + v + "' es agrupador  →  apertura";
            case PARENTESIS_DER: return "'" + v + "' es agrupador  →  cierre";
            case LLAVE_IZQ:      return "'" + v + "' es inicio de bloque";
            case LLAVE_DER:      return "'" + v + "' es fin de bloque";
            default:             return "'" + v + "' token desconocido";
        }
    }

    // =========================================================================
    //  Patron del token (columna Patron de la tabla)
    // =========================================================================

    private String obtenerPatron(Token t) {
        switch (t.getTipo()) {
            case ENTERO:
            case DECIMAL:
            case CADENA_TIPO:
            case BOOLEANO:
            case SI:
            case SINO:
            case MIENTRAS:
            case MOSTRAR:
            case LIT_VERDADERO:
            case LIT_FALSO:
            case ASIGNACION:     return "palabra reservada";
            case IDENTIFICADOR:  return "[a-zA-Z_][a-zA-Z0-9_]*";
            case LIT_ENTERO:     return "[0-9]+";
            case LIT_DECIMAL:    return "[0-9]+\\.[0-9]+";
            case LIT_CADENA:     return "\"[^\"]*\"";
            case SUMA:           return "\\+";
            case RESTA:          return "-";
            case MULTIPLICACION: return "\\*";
            case DIVISION:       return "/";
            case MODULO:         return "%";
            case IGUAL_IGUAL:    return "==";
            case DIFERENTE:      return "!=";
            case MAYOR:          return ">";
            case MENOR:          return "<";
            case MAYOR_IGUAL:    return ">=";
            case MENOR_IGUAL:    return "<=";
            case Y:              return "&&";
            case O:              return "\\|\\|";
            case NO:             return "!";
            case PUNTO_COMA:     return ";";
            case COMA:           return ",";
            case PARENTESIS_IZQ: return "\\(";
            case PARENTESIS_DER: return "\\)";
            case LLAVE_IZQ:      return "\\{";
            case LLAVE_DER:      return "\\}";
            default:             return "";
        }
    }

    // =========================================================================
    //  Es palabra reservada?
    // =========================================================================

    private boolean esPalabraReservada(TipoToken tipo) {
        // Solo identificadores y literales de valor NO son palabras reservadas
        switch (tipo) {
            case IDENTIFICADOR:
            case LIT_ENTERO:
            case LIT_DECIMAL:
            case LIT_CADENA:
                return false;
            default:
                return true;  // keywords, operadores, simbolos = Si
        }
    }
}
