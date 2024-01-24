package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Test extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        StackPane layout = new StackPane();
        Button button = new Button("Click me!");
        layout.getChildren().add(button);
        Scene scene = new Scene(layout, 300, 300);
        stage.setScene(scene);
        stage.show();
    }
}
