package sample;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class Main extends Application {

    private Consumer consumer;
    private SimpleStringProperty input = new SimpleStringProperty("0");
    private SimpleStringProperty clearLabel = new SimpleStringProperty("AC");
    private TextArea ta;

    private Button createButton(String name){
        Button btn = new Button(name);
        btn.setPadding(new Insets(10));
        btn.setMaxWidth(50);
        btn.setMinWidth(50);
        btn.setMaxHeight(50);
        btn.setMinHeight(50);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        btn.setBackground(new Background(new BackgroundFill(Color.rgb(55,55,55),
                null, null)));
        btn.setTextFill(Color.rgb(245,245,245));

        btn.getStyleClass().clear();
        btn.getStyleClass().add("button");

        return btn;
    }

    private GridPane createKeyboard() {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(5));
        grid.setGridLinesVisible(false);
        grid.setPadding(new Insets(10, 1, 10, 1));
        grid.setHgap(10);
        grid.setVgap(10);

        Label display = new Label("0123456789");
        display.setMaxWidth(230);
        display.setMinWidth(230);
        display.setPadding(new Insets(10));
        display.setBorder(new Border(new BorderStroke(Color.GRAY,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        display.setStyle("-fx-background-color: #f8ffc8;");
        display.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        display.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        grid.add(display, 0, 0, 5, 1);
        GridPane.setMargin(display, new Insets(5, 10, 15, 10));
        display.textProperty().bind(input);

        Button btnAC = createButton("AC");
        btnAC.setOnAction(this::pressAC);
        grid.add(btnAC, 1, 1);
        btnAC.textProperty().bind(clearLabel);

        Button btnSign = createButton("+/-");
        btnSign.setOnAction(this::pressSign);
        grid.add(btnSign, 2, 1);

        // Numbers
        Button btnN0 = createButton("0");
        btnN0.setOnAction(this::pressNumber);
        grid.add(btnN0, 1, 5);

        Button btnN1 = createButton("1");
        btnN1.setOnAction(this::pressNumber);
        grid.add(btnN1, 1, 4);

        Button btnN2 = createButton("2");
        btnN2.setOnAction(this::pressNumber);
        grid.add(btnN2, 2, 4);

        Button btnN3 = createButton("3");
        btnN3.setOnAction(this::pressNumber);
        grid.add(btnN3, 3, 4);

        Button btnN4 = createButton("4");
        btnN4.setOnAction(this::pressNumber);
        grid.add(btnN4, 1, 3);

        Button btnN5 = createButton("5");
        btnN5.setOnAction(this::pressNumber);
        grid.add(btnN5, 2, 3);

        Button btnN6 = createButton("6");
        btnN6.setOnAction(this::pressNumber);
        grid.add(btnN6, 3, 3);

        Button btnN7 = createButton("7");
        btnN7.setOnAction(this::pressNumber);
        grid.add(btnN7, 1, 2);

        Button btnN8 = createButton("8");
        btnN8.setOnAction(this::pressNumber);
        grid.add(btnN8, 2, 2);

        Button btnN9 = createButton("9");
        btnN9.setOnAction(this::pressNumber);
        grid.add(btnN9, 3, 2);

        Button btnDiv = createButton("/");
        btnDiv.setOnAction(this::pressOperation);
        grid.add(btnDiv, 4, 2);

        // Operations
        Button btnMulti = createButton("*");
        btnMulti.setOnAction(this::pressOperation);
        grid.add(btnMulti, 4, 3);

        Button btnMinus = createButton("-");
        btnMinus.setOnAction(this::pressOperation);
        grid.add(btnMinus, 4, 4);

        Button btnDot = createButton(".");
        btnDot.setOnAction(this::pressNumber);
        grid.add(btnDot, 2, 5);

        Button btnPlus = createButton("+");
        btnPlus.setOnAction(this::pressOperation);
        grid.add(btnPlus, 4, 5);

        Button btnEq = createButton("=");
        btnEq.setOnAction(this::pressEq);
        grid.add(btnEq, 3, 5);

        return grid;
    }

    private Pane createHistory() {
        Pane panel = new Pane();
        panel.setPadding(new Insets(5));

        ta = new TextArea();
        ta.setStyle("-fx-control-inner-background: #f8ffc8;");
        ta.setText("");
        ta.setPrefColumnCount(12);
        ta.setPrefRowCount(4);
        ta.setMinWidth(270);
        ta.setMinHeight(200);
        ta.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));
        ta.setPadding(new Insets(2));
        ta.setEditable(false);
        //ta.setDisable(true);
        //ta.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        panel.getChildren().add(ta);
        return panel;
    }

    private void pressAC(ActionEvent event){
        String text = ((Button)event.getTarget()).getText();
        String first = text.substring(0, 1);
        try {
            consumer.consume(first.toLowerCase());
        }
        catch (Exception ex){
            popupMessage(ex.getMessage());
        }
        input.setValue(consumer.getResult());
        clearLabel.setValue(consumer.getClearLabel());
        if( text.equals("AC") ){
            ta.setText("");
        }
    }

    private void pressSign(ActionEvent event){
        try {
            consumer.consume("s");
        }
        catch (Exception ex){
            popupMessage(ex.getMessage());
        }
        input.setValue(consumer.getResult());
    }

    private void popupMessage(String msg){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.showAndWait();
        System.out.println(msg);
    }

    private void pressNumber(ActionEvent event){
        String s = ((Button)event.getTarget()).getText();
        try {
            consumer.consume(s);
        }
        catch (Exception ex){
            popupMessage(ex.getMessage());
        }
        input.setValue(consumer.getResult());
        clearLabel.setValue(consumer.getClearLabel());
    }

    private void pressOperation(ActionEvent event){
        String s = ((Button)event.getTarget()).getText();
        try {
            consumer.consume(s);
        }
        catch (Exception ex){
            popupMessage(ex.getMessage());
        }
        input.setValue(consumer.getResult());
        String msg = consumer.getMessage();
        if(msg.length() > 0){
            ta.appendText(consumer.getMessage() + "\n");
        }
    }

    private void pressEq(ActionEvent event){
        try {
            consumer.consume("=");
        }
        catch (Exception ex){
            popupMessage(ex.getMessage());
        }
        input.setValue(consumer.getResult());
        String msg = consumer.getMessage();
        if(msg.length() > 0){
            ta.appendText(consumer.getMessage() + "\n");
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        consumer = new Consumer();
        BorderPane layout = new BorderPane();
        layout.setTop(createHistory());
        layout.setCenter(createKeyboard());
        layout.setPadding(new Insets(5,0,5,0));
        layout.setStyle("-fx-background-color: #adabb0;");
        primaryStage.setTitle("Calculator");

        Scene scene = new Scene(layout, 270, 630);
        scene.getStylesheets().add("stylesheet.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
