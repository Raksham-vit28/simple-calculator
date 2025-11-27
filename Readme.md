Advanced Java Calculator (Console + GUI)

Problem Statement

Many users need a single calculator application that works both in a simple command-line environment and with a modern graphical interface, while still supporting more than just basic one-step operations. This project provides a Java-based calculator that can run in the terminal or as a Swing GUI, and can evaluate complete arithmetic expressions with brackets, operator precedence, percentages, and error handling.

Project Overview
This project is an advanced calculator written in Java with two modes:
Console mode: A menu-driven command-line calculator.
GUI mode: A dark-themed calculator with rounded buttons, built using Java Swing.
The core arithmetic logic is shared between both modes through a common Calculator class to keep the design modular and easy to maintain.
Features
Basic operations: addition, subtraction, multiplication, division.
Two interfaces:
Command Line Interface (CLI) via CalculatorApp.
Graphical User Interface (GUI) via CalculatorGUI.
Expression evaluation in GUI:
Operator precedence (× and ÷ before + and −).
Nested parentheses: (2+3)*4, 2(3+4), etc.
Percentage handling: 50% treated as 50/100.
Unary minus for negative numbers: -5+10, 10*-2.
Robust error handling:
Division by zero detection.
Invalid number format.
Mismatched parentheses or malformed expressions.
Modern UI:
Dark theme.
Custom circular buttons (RoundButton).
Clear (AC) and Backspace (⌫) controls.

├── Calculator.java        # Core arithmetic logic (add, subtract, multiply, divide)
├── CalculatorApp.java     # Console interface + main entry for CLI/GUI switch
├── CalculatorGUI.java     # Swing GUI, expression parser, custom buttons
├── README.md              # Project documentation
├── Calculator_Project_Report.*   # Project report (md/pdf/docx as required)
└── screenshots/
    ├── console_menu.png
    ├── console_result.png
    ├── gui_main_window.png
    ├── gui_complex_expression.png
    └── gui_error_dialog.png

How to Run the Project
Prerequisites
Java JDK 8 or newer.

Any editor/IDE (VS Code, IntelliJ IDEA, Eclipse) or plain terminal.

Compilation
From the project root folder, run:
javac Calculator.java CalculatorApp.java CalculatorGUI.java

This will generate .class files for all three Java source files.

Option 1: Run Console Mode (CLI)
java CalculatorApp
You will see a text-based menu:

Choose an operation (1–4).

Enter two numbers.

Get the result with two decimal places.

Division by zero and invalid inputs are handled gracefully.

Option 2: Run GUI Mode
You can start the GUI in either of these ways:

Using the main method in CalculatorGUI:

Using the CLI launcher with an argument:
java CalculatorApp gui

The GUI window will open with:
Number and operator buttons.

AC to clear all input.
⌫ to delete the last character.
() for parentheses.
% for percentages.
= to evaluate the entire expression shown in the display.

Class Descriptions
1. Calculator.java (Core Math Engine)
Holds the four main operations using float:
add(float a, float b)
subtract(float a, float b)
multiply(float a, float b)
divide(float a, float b) with an ArithmeticException if b == 0.
Used by both the console and GUI modes to avoid duplication of logic.
2. CalculatorApp.java (Console Interface + Launcher)
Contains the main(String[] args) method.

If run with gui argument, launches the Swing calculator.
Otherwise, runs the CLI calculator:
Displays a menu of operations.
Uses Scanner to read user input.
Validates menu choices and numeric input (getValidChoice, getValidFloat).
Catches and displays division-by-zero errors.
Waits for user to press Enter before returning to the menu.
3. CalculatorGUI.java (Swing GUI + Expression Engine)
Extends JFrame and builds a dark-themed calculator UI.

Uses:
JLabel for the display.
JPanel with GridLayout for the button grid.
Custom RoundButton class for circular buttons.

Maintains an input buffer (StringBuilder buffer) representing the current expression.
Handles button clicks via ActionListener:
Digits and . are appended to the buffer.
Operators, parentheses, AC, and backspace have context-aware actions.
evaluateBuffer():
Translates symbols (×, ÷, −, %) into standard operators.
Calls evaluateExpression(String expr) to compute the result.

evaluateExpression():
Tokenizes the string (tokenize method).
Uses two stacks (values and operators) to evaluate with correct precedence.
Supports unary minus, nested parentheses, and percent.

4. RoundButton (Custom Swing Component)
Extends JButton.
Overrides paintComponent to draw a filled circle with anti-aliased edges.
Overrides contains for correct circular hit detection.
Overrides getPreferredSize to keep buttons consistent in size.
Overrides getPreferredSize to keep buttons consistent in size.

