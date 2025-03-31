package Object;

/*
 * MacOSX specific related issue
 * this only works if you go to run config, under arguments tab, uncheck
 * "Use the XstartOnFirstThread argument when launching with SWT."
 * https://stackoverflow.com/questions/64493118/gui-not-showing-up-with-javafx-15-using-eclipse-on-mac
*/

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Calculator extends Application {
    private TextField display;
    private double num1 = 0;
    private String operator = "";
    private boolean startNewNumber = true;

    @Override
    public void start(Stage primaryStage) {
        display = new TextField();
        display.setEditable(false);
        display.setPrefHeight(50);

        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.setHgap(5);
        root.setVgap(5);

        root.add(display, 0, 0, 4, 1);

        String[] buttonLabels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", "C", "=", "+"
        };

        int index = 0;
        for (int row = 1; row <= 4; row++) {
            for (int col = 0; col < 4; col++) {
                String label = buttonLabels[index++];
                Button button = new Button(label);
                button.setPrefSize(50, 50);
                button.setOnAction(e -> buttonClicked(label));
                root.add(button, col, row);
            }
        }

        Scene scene = new Scene(root, 220, 250);
        primaryStage.setTitle("Calculator");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Ensure the window is centered and brought to the front
        primaryStage.centerOnScreen();
        primaryStage.toFront();
        primaryStage.requestFocus();
    }

    // Method to handle button clicks
    private void buttonClicked(String label) {
        if ("0123456789".contains(label)) {
            // If a number button is pressed, either start a new number or append to the current number
            if (startNewNumber) {
                display.setText(label);
                startNewNumber = false;
            } else {
                display.setText(display.getText() + label);
            }
        } else if ("/*-+".contains(label)) {
            // When an operator is pressed, store the current number and operator
            num1 = Double.parseDouble(display.getText());
            operator = label;
            startNewNumber = true;
        } else if ("=".equals(label)) {
            // When "=" is pressed, perform the calculation and display the result
            double num2 = Double.parseDouble(display.getText());
            double result = calculate(num1, num2, operator);
            display.setText(String.valueOf(result));
            startNewNumber = true;
        } else if ("C".equals(label)) {
            // Clear the display and reset the stored values
            display.setText("");
            num1 = 0;
            operator = "";
            startNewNumber = true;
        }
    }

    // Basic calculation based on the stored operator
    private double calculate(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b != 0) {
                    return a / b;
                } else {
                    // Simple error handling: division by zero returns 0
                    return 0;
                }
            default: return 0;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}