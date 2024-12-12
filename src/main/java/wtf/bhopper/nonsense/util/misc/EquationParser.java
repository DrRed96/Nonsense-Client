package wtf.bhopper.nonsense.util.misc;

// Stack Overflow copy paste xd
public class EquationParser {

    private final String equation;
    private int pos = -1;
    private int ch;

    private EquationParser(String equation) {
        this.equation = equation;
    }

    private void nextChar() {
        this.ch = (++this.pos < this.equation.length()) ? this.equation.charAt(this.pos) : -1;
    }

    private boolean eat(int charToEat) {
        while (ch == ' ') {
            this.nextChar();
        }
        if (ch == charToEat) {
            this.nextChar();
            return true;
        }
        return false;
    }

    private double parse() {
        this.nextChar();
        double x = this.parseExpression();
        if (this.pos < this.equation.length()) {
            throw new RuntimeException("Unexpected: " + (char) ch);
        }
        return x;
    }

    private double parseExpression() {
        double x = this.parseTerm();
        for (; ; ) {
            if (this.eat('+')) {
                x += this.parseTerm(); // addition
            } else if (eat('-')) {
                x -= this.parseTerm(); // subtraction
            } else {
                return x;
            }
        }
    }

    private double parseTerm() {
        double x = this.parseFactor();
        for (; ; ) {
            if (this.eat('*')) {
                x *= this.parseFactor(); // multiplication
            } else if (eat('/')) {
                x /= this.parseFactor(); // division
            } else {
                return x;
            }
        }
    }

    private double parseFactor() {
        if (this.eat('+')) {
            return this.parseFactor(); // unary plus
        }
        if (this.eat('-')) {
            return -this.parseFactor(); // unary minus
        }

        double x;
        int startPos = this.pos;
        if (this.eat('(')) { // parentheses
            x = this.parseExpression();
            this.eat(')');
        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
            while ((ch >= '0' && ch <= '9') || ch == '.') {
                this.nextChar();
            }
            x = Double.parseDouble(this.equation.substring(startPos, this.pos));
        } else if (ch >= 'a' && ch <= 'z') { // functions
            while (ch >= 'a' && ch <= 'z') {
                this.nextChar();
            }
            String func = this.equation.substring(startPos, this.pos);
            x = this.parseFactor();
            x = switch (func) {
                case "sqrt" -> Math.sqrt(x);
                case "sin" -> Math.sin(Math.toRadians(x));
                case "cos" -> Math.cos(Math.toRadians(x));
                case "tan" -> Math.tan(Math.toRadians(x));
                default -> throw new RuntimeException("Unknown function: " + func);
            };
        } else {
            throw new RuntimeException("Unexpected: " + (char) ch);
        }

        if (this.eat('^')) {
            x = Math.pow(x, this.parseFactor()); // exponentiation
        }

        return x;
    }

    public static double parseEquation(String equation) {
        return new EquationParser(equation).parse();
    }

}
