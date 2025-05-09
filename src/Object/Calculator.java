package Object;

/*
 * MacOSX specific related issue
 * this only works if you go to run config, under arguments tab, uncheck
 * "Use the XstartOnFirstThread argument when launching with SWT."
 * https://stackoverflow.com/questions/64493118/gui-not-showing-up-with-javafx-15-using-eclipse-on-mac
*/

import javafx.application.Application;
//Padding
import javafx.geometry.Insets;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

//for evaluating expressions containing more than two constants

public class Calculator extends Application {
	//GUI
    private TextField display;
    
    //Keeps track of the current expression (5+3)
    private StringBuilder expression = new StringBuilder(); 
    //checks if the last input was a result
    private boolean isResultDisplayed = false;

    @Override
    public void start(Stage primaryStage) {
    	//calculator screen
        display = new TextField();
        display.setEditable(false);
        display.setPrefHeight(50);
        
        //font & css
        display.setFont(Font.font("Arial", 18));
        display.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-border-color: black; -fx-border-width: 2px;");
        
        //grid layout for buttons
        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.setHgap(5);
        root.setVgap(5);

        root.setStyle("-fx-background-color: #562aca;");
        
        root.add(display, 0, 0, 4, 1);
        
        //calculator button layout 
        String[] buttonLabels = {
        	"SIN", "COS", "TAN", "SQRT",
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "^", "+",
            "%","=", "C"
        };
        
      //Loops through rows and columns (4 buttons per row) and creates grid
      //Iterates through each button, can work for infinite buttons
        double buttonWidth = 65;
        double buttonHeight = 50;
        int index = 0; 
        for (int row = 1; index < buttonLabels.length; row++) {
            for (int col = 0; col < 4 && index < buttonLabels.length; col++) {
            	/*String variable holds text on each button, determines what
            	 * the button does when clicked
            	*/
                String label = buttonLabels[index++];
                Button button = new Button(label);
                button.setPrefSize(buttonWidth, buttonHeight);
                
                //if case specific for Trig functions to fit inside button
                if (label.equals("SIN") || label.equals("COS") || label.equals("TAN") || label.equals("SQRT")) {
                	button.setFont(Font.font("Rubik", 14));
                } else {
                	button.setFont(Font.font("Rubik", 18));
                }
                button.setOnAction(e -> buttonClicked(label));
                root.add(button, col, row);
            }
        }
        

        Scene scene = new Scene(root, 300, 320);
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
    	//if a number is pressed, utilize number for next expression or you can clear it
    	if ("0123456789".contains(label) || label.equals(".")) {
            if (isResultDisplayed) {
                display.setText(label);
                expression.setLength(0);
                expression.append(label);
                isResultDisplayed = false;
            } else {
                display.setText(display.getText() + label);
                expression.append(label);
            }
        //takes number and applies operation 
        } else if ("+-*/%^".contains(label)) {
            if (expression.length() > 0 && "+-*/%^".contains("" + expression.charAt(expression.length() - 1))) {
                expression.setCharAt(expression.length() - 1, label.charAt(0));
            } else {
                expression.append(label);
            }
            display.setText(display.getText() + label);
            isResultDisplayed = false;
        //Calls evaluateWithCalculate() and computes result
        } else if ("=".equals(label)) {
        //try exceptions and shows "Error" if result doesn't compute
            try {
                double result = evaluateWithCalculate(expression.toString());
                display.setText(String.valueOf(result));
                expression.setLength(0);
                expression.append(result);
                isResultDisplayed = true;
            } catch (Exception e) {
                display.setText("Error");
                expression.setLength(0);
                isResultDisplayed = true;
            }
        //Clears everything
        } else if ("C".equals(label)) {
            display.setText("");
            expression.setLength(0);
            isResultDisplayed = false;
            
        //Applies Trig functions using calculate()
        } else if ("SIN".equals(label) || "COS".equals(label) || "TAN".equals(label)) {
        	String currentText = display.getText();
        	if (currentText.isEmpty()) {
        		display.setText("0");
        		currentText = "0";
        	}
        //Catches Error in Trig function
            try {
                double value = Double.parseDouble(currentText);
                double result = calculate(value, 0, label);
                display.setText(String.valueOf(result));
                expression.setLength(0);
                expression.append(result);
                isResultDisplayed = true;
            } catch (Exception e) {
                display.setText("Error");
            } 
        //Square Root function
         } else if ("SQRT".equals(label)) {
                String currentText = display.getText();
                if (currentText.isEmpty()) {
                    display.setText("0");
                    currentText = "0";
                }
                try {
                    double value = Double.parseDouble(currentText);
                    double result = Math.sqrt(value);
                    display.setText(String.valueOf(result));
                    expression.setLength(0);
                    expression.append(result);
                    isResultDisplayed = true;
                } catch (Exception e) {
                    display.setText("Error");
                }
            
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
            case "%": return a % b;
            case "^": return Math.pow(a, b);
            case "SIN": return Math.sin(Math.toRadians(a));
            case "COS": return Math.cos(Math.toRadians(a));
            case "TAN": return Math.tan(Math.toRadians(a));
            
                default: return 0;
        }
    }
    
    //listens for when the equal sign is pressed and splits the expression into bits and processes
    private double evaluateWithCalculate(String expr) throws Exception {
    	/*splits the expression string into numbers and operators while
    	 * keeping the operators
    	 * example: 
    	 * input = 2+3*4
    	 * output = "2", "+", "3", "*", "4"
    	*/
    	String[] tokens = expr.split("(?<=[-+*/%^])|(?=[-+*/%^])"); 
    	
    	//iterates through result to the next result
    	double result = Double.parseDouble(tokens[0]);
    	for (int i = 1; i < tokens.length - 1; i += 2) {
    		String op = tokens[i];
    		double next = Double.parseDouble(tokens[i + 1]);
    		
    		//uses calculate method to compute, waits for the next result
   
    		result = calculate(result, next, op);
    	}
    	return result;
    }

    public static void main(String[] args) {
    	/*for (String fontName : Font.getFamilies()) {
    	    System.out.println(fontName);
    }*/
        launch(args);
    }
}