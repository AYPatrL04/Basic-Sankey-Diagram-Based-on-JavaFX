package org.example;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class JavaFXApp extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Pane pane = new My3DBoxPane();
        Scene scene = new Scene(pane, 300, 300);
        stage.setTitle("3D Box");
        stage.setScene(scene);
        stage.show();

    }

}
class My3DBoxPane extends Pane {

    private void paint() {
        double paneWidth = getWidth();
        double paneHeight = getHeight();
        double radius = Math.min(paneWidth, paneHeight) / 2;
        double centerX = paneWidth / 2;
        double centerY = paneHeight / 2;

        Circle circle = new Circle(centerX, centerY, radius);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);


        Polygon hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = 2.0 * Math.PI / 6 * i;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            hexagon.getPoints().addAll(x, y);
        }

        hexagon.setFill(Color.TRANSPARENT); // 设置填充颜色为透明
        hexagon.setStroke(Color.BLACK); // 设置边框颜色为黑色

        getChildren().clear();
        getChildren().addAll(circle, hexagon);


        double width = 100;
        double height = 100;
        double diff = 20;

        //draw front rectangle
        Rectangle r1 = new Rectangle(10, 60, width, height);
        r1.setFill(new Color(1, 1, 1, 0));
        r1.setStroke(Color.BLACK);


        Rectangle r2 = new Rectangle(30, 60 - diff, width, height);
        r2.setFill(new Color(1, 1, 1, 0));
        r2.setStroke(Color.BLACK);

        Line line1 = new Line(10, 60, 30, 60 - diff);
        Line line2 = new Line(10, 60 + height, 30, 60 - diff + height);
        Line line3 = new Line(10 + width, 60, 30 + width, 60 - diff);
        Line line4 = new Line(10 + width, 60 + height, 30 + width, 60 - diff + height);

        getChildren().clear();
        getChildren().addAll(r1, r2, line1, line2, line3, line4);
    }

    @Override
    public void setWidth(double width) {
        super.setWidth(width);
        paint();
    }

    @Override
    public void setHeight(double height) {
        super.setHeight(height);
        paint();
    }
}
