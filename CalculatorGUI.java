import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// CalculatorGUI.java
public class CalculatorGUI extends JFrame {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new CalculatorGUI());
    }

    private final JLabel display = new JLabel("", SwingConstants.RIGHT);
    private final Calculator calculator = new Calculator();

    public CalculatorGUI() {
        super("Calculator");
        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 720);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        Color background = Color.decode("#0f0f12");
        Color buttonDark = Color.decode("#2f2f34");
        Color operatorColor = Color.decode("#55607a");
        Color acColor = Color.decode("#3b5aa4");
        Color equalsColor = Color.decode("#e9bfe0");

        getContentPane().setBackground(background);
        getContentPane().setLayout(new BorderLayout());

        // Top display
        display.setOpaque(true);
        display.setBackground(background);
        display.setForeground(Color.WHITE);
        display.setFont(new Font("SansSerif", Font.PLAIN, 48));
        display.setBorder(BorderFactory.createEmptyBorder(24, 18, 24, 18));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(background);
        top.add(display, BorderLayout.SOUTH);
        getContentPane().add(top, BorderLayout.NORTH);

        // Buttons panel: 5 rows x 4 columns
        JPanel buttons = new JPanel(new GridLayout(5, 4, 16, 16));
        buttons.setBackground(background);
        buttons.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // Row 1
        buttons.add(makeButton("AC", acColor, Color.WHITE));
        buttons.add(makeButton("()", buttonDark, Color.WHITE));
        buttons.add(makeButton("%", buttonDark, Color.WHITE));
        buttons.add(makeButton("÷", operatorColor, Color.WHITE));

        // Row 2
        buttons.add(makeButton("7", buttonDark, Color.WHITE));
        buttons.add(makeButton("8", buttonDark, Color.WHITE));
        buttons.add(makeButton("9", buttonDark, Color.WHITE));
        buttons.add(makeButton("×", operatorColor, Color.WHITE));

        // Row 3
        buttons.add(makeButton("4", buttonDark, Color.WHITE));
        buttons.add(makeButton("5", buttonDark, Color.WHITE));
        buttons.add(makeButton("6", buttonDark, Color.WHITE));
        buttons.add(makeButton("−", operatorColor, Color.WHITE));

        // Row 4
        buttons.add(makeButton("1", buttonDark, Color.WHITE));
        buttons.add(makeButton("2", buttonDark, Color.WHITE));
        buttons.add(makeButton("3", buttonDark, Color.WHITE));
        buttons.add(makeButton("+", operatorColor, Color.WHITE));

        // Row 5
        buttons.add(makeButton("0", buttonDark, Color.WHITE));
        buttons.add(makeButton(".", buttonDark, Color.WHITE));
        buttons.add(makeButton("⌫", buttonDark, Color.WHITE));
        buttons.add(makeButton("=", equalsColor, Color.BLACK));

        getContentPane().add(buttons, BorderLayout.CENTER);

        // Wire up basic actions
        for (Component c : buttons.getComponents()) {
            if (c instanceof RoundButton) {
                RoundButton b = (RoundButton) c;
                b.addActionListener(new ButtonHandler());
            }
        }
    }

    private RoundButton makeButton(String text, Color bg, Color fg) {
        RoundButton b = new RoundButton(text);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("SansSerif", Font.PLAIN, 24));
        return b;
    }

    // Simple input buffer and evaluation
    private StringBuilder buffer = new StringBuilder();

    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = ((RoundButton) e.getSource()).getText();
            switch (cmd) {
                case "AC":
                    buffer.setLength(0);
                    display.setText("");
                    break;
                case "⌫":
                    if (buffer.length() > 0) buffer.deleteCharAt(buffer.length() - 1);
                    display.setText(buffer.toString());
                    break;
                case "=":
                    evaluateBuffer();
                    break;
                case "×":
                    buffer.append('*');
                    display.setText(buffer.toString());
                    break;
                case "÷":
                    buffer.append('/');
                    display.setText(buffer.toString());
                    break;
                case "−":
                    buffer.append('-');
                    display.setText(buffer.toString());
                    break;
                case "+":
                    buffer.append('+');
                    display.setText(buffer.toString());
                    break;
                case "%":
                    buffer.append('%');
                    display.setText(buffer.toString());
                    break;
                case "()":
                    // toggle parentheses smartly: close if there is an unmatched '(',
                    // otherwise open a new '('. If opening after a number or ')',
                    // insert implicit multiplication before '('.
                    int opens = 0, closes = 0;
                    for (int i = 0; i < buffer.length(); i++) {
                        char ch = buffer.charAt(i);
                        if (ch == '(') opens++;
                        else if (ch == ')') closes++;
                    }
                    if (opens > closes) {
                        buffer.append(')');
                    } else {
                        // if last char is digit or ')' or '.', add '*' before '('
                        if (buffer.length() > 0) {
                            char last = buffer.charAt(buffer.length() - 1);
                            if (Character.isDigit(last) || last == ')' || last == '.') {
                                buffer.append('*');
                            }
                        }
                        buffer.append('(');
                    }
                    display.setText(buffer.toString());
                    break;
                default:
                    // digits and dot
                    buffer.append(cmd);
                    display.setText(buffer.toString());
                    break;
            }
        }
    }

    private void evaluateBuffer() {
        String expr = buffer.toString();
        try {
            // Replace unicode operator symbols with ASCII equivalents
            String replaced = expr.replace('×', '*').replace('÷', '/').replace('−', '-');
            // Treat percent as division by 100 (e.g. "50%" -> "50/100")
            replaced = replaced.replace("%", "/100");
            double value = evaluateExpression(replaced);
            // Trim trailing .0 if integer
            String out;
            if (Math.abs(value - Math.rint(value)) < 1e-12) out = String.format("%.0f", value);
            else out = String.valueOf(value);
            display.setText(out);
            buffer.setLength(0);
            buffer.append(out);
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg == null) msg = "Error";
            display.setText("Error");
            buffer.setLength(0);
        }
    }

    // Evaluate a mathematical expression containing + - * / and parentheses.
    private double evaluateExpression(String expr) throws Exception {
        java.util.List<String> tokens = tokenize(expr);
        java.util.Stack<Double> values = new java.util.Stack<>();
        java.util.Stack<String> ops = new java.util.Stack<>();

        for (int i = 0; i < tokens.size(); i++) {
            String tok = tokens.get(i);
            if (tok.isEmpty()) continue;
            if (tok.equals("(")) {
                ops.push(tok);
            } else if (tok.equals(")")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) {
                    applyOp(values, ops.pop());
                }
                if (!ops.isEmpty() && ops.peek().equals("(")) ops.pop();
                else throw new Exception("Mismatched parentheses");
            } else if (isOperator(tok)) {
                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(tok)) {
                    applyOp(values, ops.pop());
                }
                ops.push(tok);
            } else {
                // number
                values.push(Double.parseDouble(tok));
            }
        }

        while (!ops.isEmpty()) {
            String op = ops.pop();
            if (op.equals("(") || op.equals(")")) throw new Exception("Mismatched parentheses");
            applyOp(values, op);
        }

        if (values.isEmpty()) return 0;
        return values.pop();
    }

    private void applyOp(java.util.Stack<Double> values, String op) throws Exception {
        if (values.size() < 1) throw new Exception("Invalid expression");
        double b = values.pop();
        double a = 0;
        if (!values.isEmpty()) a = values.pop();
        switch (op) {
            case "+": values.push(a + b); break;
            case "-": values.push(a - b); break;
            case "*": values.push(a * b); break;
            case "/":
                if (b == 0) throw new ArithmeticException("Error: Division by zero is not allowed.");
                values.push(a / b);
                break;
            default:
                throw new Exception("Unknown operator: " + op);
        }
    }

    private boolean isOperator(String s) {
        return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
    }

    private int precedence(String op) {
        if (op.equals("+") || op.equals("-")) return 1;
        if (op.equals("*") || op.equals("/")) return 2;
        return 0;
    }

    // Tokenizer: handles numbers (with optional unary minus) and operators/parentheses
    private java.util.List<String> tokenize(String s) {
        java.util.List<String> out = new java.util.ArrayList<>();
        int i = 0;
        int n = s.length();
        while (i < n) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) { i++; continue; }
            if (c == '(' || c == ')' || c == '+' || c == '*' || c == '/') {
                out.add(String.valueOf(c));
                i++; continue;
            }
            // handle minus which could be unary
            if (c == '-') {
                // unary if at start or previous is operator or '('
                if (out.isEmpty() || isOperator(out.get(out.size()-1)) || out.get(out.size()-1).equals("(")) {
                    // parse number with leading -
                    int j = i+1;
                    StringBuilder sb = new StringBuilder(); sb.append('-');
                    boolean dotSeen = false;
                    while (j < n) {
                        char d = s.charAt(j);
                        if (d == '.') {
                            if (dotSeen) break;
                            dotSeen = true; sb.append(d); j++; continue;
                        }
                        if ((d >= '0' && d <= '9')) { sb.append(d); j++; continue; }
                        break;
                    }
                    out.add(sb.toString());
                    i = j; continue;
                } else {
                    out.add("-"); i++; continue;
                }
            }
            // numbers
            if ((c >= '0' && c <= '9') || c == '.') {
                int j = i;
                StringBuilder sb = new StringBuilder();
                boolean dotSeen = false;
                while (j < n) {
                    char d = s.charAt(j);
                    if (d == '.') {
                        if (dotSeen) break;
                        dotSeen = true; sb.append(d); j++; continue;
                    }
                    if ((d >= '0' && d <= '9')) { sb.append(d); j++; continue; }
                    break;
                }
                out.add(sb.toString());
                i = j; continue;
            }
            // any other char - skip
            i++;
        }
        return out;
    }

    // Rounded circular button
    private static class RoundButton extends JButton {
        public RoundButton(String label) {
            super(label);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            g2.fillOval(x, y, size, size);
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int tx = getWidth() / 2 - fm.stringWidth(getText()) / 2;
            int ty = getHeight() / 2 + fm.getAscent() / 2 - 2;
            g2.drawString(getText(), tx, ty);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public boolean contains(int x, int y) {
            int size = Math.min(getWidth(), getHeight());
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int dx = x - cx;
            int dy = y - cy;
            return dx * dx + dy * dy <= (size / 2) * (size / 2);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(96, 96);
        }
    }
}
