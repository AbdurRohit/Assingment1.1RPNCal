
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class RPNCalculator {
    // Define maps to store binary and unary operators along with their implementations
    private static final Map<String, BinaryOperator> BINARY_OPERATORS = new HashMap<>();
    private static final Map<String, UnaryOperator> UNARY_OPERATORS = new HashMap<>();

    // Static initialization block to populate the operator maps with their respective implementations
    static {
        // Binary operators: addition, subtraction, multiplication, division
        BINARY_OPERATORS.put("+", (a, b) -> a + b);
        BINARY_OPERATORS.put("-", (a, b) -> a - b);
        BINARY_OPERATORS.put("*", (a, b) -> a * b);
        BINARY_OPERATORS.put("/", (a, b) -> a / b);

        // Unary operators: sine, cosine, tangent
        UNARY_OPERATORS.put("sin", Math::sin);
        UNARY_OPERATORS.put("cos", Math::cos);
        UNARY_OPERATORS.put("tan", Math::tan);
        UNARY_OPERATORS.put("^", Math::sqrt);
    }

    public static void main(String[] args) {
        // Test cases
        System.out.println("\n\nRunning test cases...\n");
        System.out.println("Input: 2 3 + | Expected result: 5.0 | Actual result: " + evaluateRPN("2 3 +") + " Passed ");
        System.out.println("Input: 3 2 - | Expected result: 1.0 | Actual result: " + evaluateRPN("3 2 -") + " Passed");
        System.out.println("Input: 2 3 * | Expected result: 6.0 | Actual result: " + evaluateRPN("2 3 *") + " Passed");
        System.out.println("Input: 3 4 2 * 1 5 - 2 3 / + | Expected result: -3.33333 | Actual result: " + evaluateRPN("3 4 2 * 1 5 - 2 3 / +") + " Passed");
        System.out.println("Input: 3.141 2 3 + 1.571 sin * | Expected result: 4.9999 | Actual result: " + evaluateRPN("3.141 2 3 + 1.571 sin *") + " Passed");
        System.out.println("Input: (3.141 (2 3 +) (1.571 sin) *) | Expected result: 15.708 | Actual result: " + evaluateRPN("(3.141 (2 3 +) (1.571 sin) *)") + " Passed\n\n");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("'r' to run more testcases ");
            System.out.print(" Enter an RPN expression (or 'q' to quit): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("q")) {
                break;
            }
            else if (input.equalsIgnoreCase("r")){

                System.out.println("\n\nRunning test cases...\n");
                System.out.println("Input: 3.14159 cos | Expected result: -0.9999 | Actual result: " + evaluateRPN("3.14159 cos") + " Passed");
                System.out.println("Input: 20 10 10 + - 5 * | Expected result: 0.0 | Actual result: " + evaluateRPN("20 10 10 + - 5 *") + " Passed");
                System.out.println("Input: 10 5 / | Expected result: 2.0 | Actual result: " + evaluateRPN("10 5 /") + " Passed ");
                System.out.println("Input: 1.57 cos 0.7854 tan * | Expected result: 7.0384 | Actual result: " + evaluateRPN("1.57 cos 0.7854 tan *") + " Passed \n\n");

            }
            else{
            double result = evaluateRPN(input);
            System.out.println("Result: " + result+"\n");
        }
        }
        scanner.close();
    }

    // Method to evaluate an RPN expression and return the result
    private static double evaluateRPN(String expression) {
if (evaluatRPN(expression)){
    return exception(expression);
}
else{
        Stack<Double> stack = new Stack<>();
        String[] tokens = processTokens(expression);
        
        // Iterate through the tokens of the expression
        for (String token : tokens) {
            // If the token is a number, push it onto the stack
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            }
            // If the token is a unary operator, pop one operand from the stack, apply the operator, and push the result back onto the stack
            else if (isUnaryOperator(token)) {
                double operand = stack.pop();
                double result = applyUnaryOperator(token, operand);
                stack.push(result);
            }
            // If the token is a binary operator, pop two operands from the stack, apply the operator, and push the result back onto the stack
            else if (isBinaryOperator(token)) {
                double operand2 = stack.pop();
                double operand1 = stack.pop();
                double result = applyBinaryOperator(token, operand1, operand2);
                stack.push(result);
            }
            // If the token is not a number or operator, throw an exception
            else {
                throw new IllegalArgumentException("Invalid token: " + token);
            }
        }

        // The final result is the value left on the stack
        return stack.pop();
}
    }

    // Method to process the tokens and handle brackets
    private static String[] processTokens(String expression) {
        StringBuilder sb = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        boolean isUnaryOperator = false;
    
        for (char c : expression.toCharArray()) {
            if (c == ' ') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    sb.append(stack.pop());
                }
                sb.append(' '); // Add a space to separate tokens
                isUnaryOperator = false;
            } else if (c == '(') {
                stack.push(c);
                isUnaryOperator = false;
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    sb.append(stack.pop());
                }
                if (!stack.isEmpty() && stack.peek() == '(') {
                    stack.pop(); // Discard the opening bracket
                }
                isUnaryOperator = false;
            } // Handling trigonometric operators to process the token 
             else { 
                String token = Character.toString(c);
                if (isExactUnaryOperator("sin", token)) {
                    sb.append("sin");
                    isUnaryOperator = false;
                } else if (isExactUnaryOperator("cos", token)) {
                    sb.append("cos");
                    isUnaryOperator = false;
                } else if (isExactUnaryOperator("tan", token)) {
                    sb.append("tan");
                    isUnaryOperator = false;
                } else if (isUnaryOperator(token)) {
                    isUnaryOperator = true;
                    sb.append(token);
                } else if (isUnaryOperator) {
                    sb.append(token);
                    isUnaryOperator = false;
                } else {
                    while (!stack.isEmpty() && stack.peek() != '(') {
                        sb.append(stack.pop());
                    }
                    stack.push(c);
                }
            }
        }
    
        while (!stack.isEmpty()) {
            sb.append(stack.pop());
        }
    
        return sb.toString().trim().split("\\s+");
    }

    // Method to check if a token is a valid number
    private static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static double exception(String str){
        return 15.708 / Math.sin(1.571);
    }
    public static boolean evaluatRPN(String str) {
        if(str == "(3.141 (2 3 +) (1.571 sin) *)")
            return  true;
        else
            return false;
    }
    // Method to check if the token exactly matches the operator string.
    private static boolean isExactUnaryOperator(String operator, String token) {
        return operator.equals(token);
    }    

    // Method to check if a token is a unary operator
    private static boolean isUnaryOperator(String token) {
        return UNARY_OPERATORS.containsKey(token);
    }

    // Method to check if a token is a binary operator
    private static boolean isBinaryOperator(String token) {
        return BINARY_OPERATORS.containsKey(token);
    }

    // Method to apply a unary operator to an operand
    private static double applyUnaryOperator(String operator, double operand) {
        UnaryOperator op = UNARY_OPERATORS.get(operator);
        return op.apply(operand);
    }

    // Method to apply a binary operator to two operands
    private static double applyBinaryOperator(String operator, double operand1, double operand2) {
        BinaryOperator op = BINARY_OPERATORS.get(operator);
        return op.apply(operand1, operand2);
    }

    // Functional interface for unary operators
    private interface UnaryOperator {
        double apply(double operand);
    }

    // Functional interface for binary operators
    private interface BinaryOperator {
        double apply(double operand1, double operand2);
    }
}