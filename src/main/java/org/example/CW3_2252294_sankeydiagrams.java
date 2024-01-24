package org.example;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Formatter;
import java.util.Optional;

public class CW3_2252294_sankeydiagrams extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Pane fileSelect = new SelectFile();
        Scene scene = new Scene(fileSelect, 400, 400);

        stage.setTitle("Select the Source File");
        stage.setScene(scene);
        stage.show();
    }
}

// ========== Selecting File ==========
class SelectFile extends Pane {

    static String URLPrompt = "Source URL";
    static String URL;

    private void paint() {
        double width  = getWidth();
        double height = getHeight();

        getChildren().clear();

        Text setUrl = new Text("Click the button or drag and drop the file below");
        setUrl.setFont(Font.font(Font.getDefault().getFamily(), Math.min(width / 30, height / 30)));
        setUrl.setFill(Color.BLACK);
        setUrl.setX(width * 0.05);
        setUrl.setY(height * 0.06);

        TextField sourceUrl = new TextField(URL);
        sourceUrl.setPromptText(URLPrompt);
        sourceUrl.setStyle("-fx-prompt-text-fill: GRAY;");
        sourceUrl.setFont(Font.font(Font.getDefault().getFamily(), Math.min(width / 30, height / 30)));
        sourceUrl.setPrefSize(width * 0.7, height / 20);
        sourceUrl.setLayoutX(width * 0.05);
        sourceUrl.setLayoutY(height * 0.95 - sourceUrl.getPrefHeight() / 2);

        StringProperty sourceUrlProperty = new SimpleStringProperty();
        sourceUrlProperty.bind(sourceUrl.textProperty());
        sourceUrlProperty.addListener((observable, oldValue, newValue) -> URL = newValue);

        Button selectButton = new Button("Select");
        selectButton.setPrefSize(width * 0.15, height / 20);
        selectButton.setLayoutX(width * 0.95 - selectButton.getPrefWidth());
        selectButton.setLayoutY(height * 0.05 - selectButton.getPrefHeight() / 2);
        selectButton.setFont(Font.font(Math.min(selectButton.getPrefWidth() / 2, selectButton.getPrefHeight() / 2)));
        selectButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select the Source File");
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                if (!file.getName().endsWith(".txt")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid File Type");
                    alert.setContentText("Only .txt files are allowed");
                    alert.showAndWait();
                    return;
                }
                URL = file.getAbsolutePath();
                sourceUrl.setText(file.getAbsolutePath());
            }
        });

        Rectangle dragAndDropArea = getRectangle(width, height, sourceUrl);

        Text dragAndDropText = new Text("Drag and Drop the Source File Here");
        dragAndDropText.setFont(Font.font("Arial", Math.min(width / 30, height / 30)));
        dragAndDropText.setFill(Color.BLACK);
        dragAndDropText.setX(
                dragAndDropArea.getX() +
                dragAndDropArea.getWidth() / 2 -
                dragAndDropText.getLayoutBounds().getWidth() / 2
        );
        dragAndDropText.setY(
                dragAndDropArea.getY() +
                dragAndDropArea.getHeight() / 2 +
                dragAndDropText.getLayoutBounds().getHeight() / 2
        );

        Button confirmButton = new Button("Confirm");
        confirmButton.setPrefSize(width * 0.15, height / 20);
        confirmButton.setLayoutX(width * 0.95 - confirmButton.getPrefWidth());
        confirmButton.setLayoutY(height * 0.955 - confirmButton.getPrefHeight() / 2);
        confirmButton.setFont(Font.font(Math.min(confirmButton.getPrefWidth() / 2, confirmButton.getPrefHeight() / 2)));
        confirmButton.setOnAction(event -> {
            String filePath = URL;
            try {
                if (!filePath.endsWith(".txt")   ||
                    !new File(filePath).exists() ||
                    !new File(filePath).isFile() ||
                    !new File(filePath).canRead())
                    throw new Exception();

                SankeyDiagram.dataList.clear();
                SankeyDiagram.count = 0;
                SankeyDiagram.sum = 0;

                SankeyDiagram.filePath = filePath;
                SankeyDiagram.fetchFile(SankeyDiagram.filePath);
                Pane sankey = new SankeyDiagram();
                Scene scene = new Scene(sankey, 1000, 800);
                sankey.requestFocus();

                Stage stage = (Stage) getScene().getWindow();
                stage.setTitle("Sankey Diagram");
                stage.setScene(scene);
                stage.show();

            } catch (Exception e) {
                Alert alert = getAlert(filePath);
                alert.showAndWait();
            }
        });

        getChildren().addAll(setUrl, sourceUrl, selectButton, dragAndDropArea, dragAndDropText, confirmButton);
    }

    private static Alert getAlert(String filePath) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");

        if      ( filePath.isEmpty()          ) alert.setHeaderText("No File Path"    );
        else if (!filePath.endsWith(".txt")   ) alert.setHeaderText("Not a .txt File" );
        else if (!new File(filePath).exists() ) alert.setHeaderText("File Not Found"  );
        else if (!new File(filePath).isFile() ) alert.setHeaderText("Not a File"      );
        else if (!new File(filePath).canRead()) alert.setHeaderText("Cannot Read File");
        else                                    alert.setHeaderText("Error"           );

        alert.setContentText("Please input a valid file path or select a valid file");
        return alert;
    }

    private static Rectangle getRectangle(double width, double height, TextField sourceUrl) {
        Rectangle dragAndDropArea = new Rectangle(width * 0.05, height * 0.1, width * 0.9, height * 0.8);
        dragAndDropArea.setFill(Color.WHITE);
        dragAndDropArea.setStroke(Color.BLACK);
        dragAndDropArea.setStrokeWidth(1);
        dragAndDropArea.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        dragAndDropArea.setOnDragDropped(event -> {
            List<File> files = event.getDragboard().getFiles();
            if (!files.isEmpty()) {
                if (!files.get(0).getName().endsWith(".txt")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid File Type");
                    alert.setContentText("Only .txt files are allowed");
                    alert.showAndWait();
                    return;
                }
                URL = files.get(0).getAbsolutePath();
                sourceUrl.setText(files.get(0).getAbsolutePath());
            }
            event.consume();
        });
        return dragAndDropArea;
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

// ========== Drawing Sankey Diagram ==========
class SankeyDiagram extends Pane {

    static List<String> dataList    = new ArrayList<>();
    static int          count       = 0;
    static double       sum         = 0;
    static String       filePath    = SelectFile.URL;
           int[]        color       = {65, 195, 225, 65, 225, 105};
           TextField    b2          = new TextField(String.valueOf(color[5]));
           TextField    g2          = new TextField(String.valueOf(color[4]));
           TextField    r2          = new TextField(String.valueOf(color[3]));
           TextField    b1          = new TextField(String.valueOf(color[2]));
           TextField    g1          = new TextField(String.valueOf(color[1]));
           TextField    r1          = new TextField(String.valueOf(color[0]));
           Text         warn        = new Text     ("Set the RGB Empty to Randomize.");
           Tooltip      R           = new Tooltip  ("Red"  );
           Tooltip      G           = new Tooltip  ("Green");
           Tooltip      B           = new Tooltip  ("Blue" );
           String       INVALID_RGB = "Please input valid RGB values!";
           String       VALID_RGB   = "Color changed!"                ;

    private boolean isNotNumber(String string) {
        try {
            Double.parseDouble(string);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private double min(double a, double b) {
        return Math.min(a, b);
    }

//    ========== Receiving and storing data ==========
    public static void fetchFile(String filename) {
        final String NOT_FOUND = "File Not Found!";
        try {
            Scanner scanner = new Scanner(new File(filename));
            while (scanner.hasNext()) {
                if (dataList.size() < 2)
                    dataList.add(scanner.nextLine());
                else {
                    String line = scanner.nextLine();
                    int index = line.lastIndexOf(" ");

                    dataList.add(line.substring(0, index));
                    dataList.add(line.substring(index + 1));
                }
            }
            scanner.close();

            count = (dataList.size() - 2) / 2;
            for (int i = 2; i < dataList.size(); i += 2)
                sum += Double.parseDouble(dataList.get(i + 1));

        } catch (IOException e) {
            System.out.println(NOT_FOUND);
        }
    }

//    ========== Drawing main part of the Sankey Diagram ==========
    private void paint() {
        double paneWidth  = getWidth();
        double paneHeight = getHeight();
        double r1Height   = paneHeight * 3 / 5;
        double rWidth     = paneWidth / 30;
        double r1XPos     = paneWidth / 20;
        double r1YPos     = paneHeight / 7;
        double yPos       = paneHeight / 20;
        double lineY      = r1YPos;
        double distance   = (paneHeight - 4 * yPos - r1Height) / (count - 1);
        double rXPos      = paneWidth * 19 / 20;

        getChildren().clear();

        Button newItemButton = new Button("Add New Item");
        newItemButton.setOnAction(e -> addNew());
        newItemButton.layoutXProperty().bind(
                widthProperty()
                        .subtract(newItemButton.widthProperty())
                        .subtract(paneWidth - (rXPos + rWidth))
        );
        newItemButton.layoutYProperty().bind(
                heightProperty()
                        .subtract(newItemButton.prefHeight(-1))
                        .divide(100)
        );

        Button changeItemButton = new Button("Update Item");
        changeItemButton.setOnAction(e -> updateItem());
        changeItemButton.layoutXProperty().bind(
                widthProperty()
                        .subtract(paneWidth - r1XPos)
                        .subtract(rWidth)
        );
        changeItemButton.layoutYProperty().bind(
                heightProperty()
                        .subtract(changeItemButton.prefHeight(-1))
                        .divide(100)
        );

        warn.setFont(Font.font(min(getHeight(), getWidth()) / 50));
        warn.setLayoutX(paneWidth / 2 - warn.getBoundsInLocal().getWidth() / 2);
        warn.setLayoutY(paneHeight * 0.99);

        colorChange();

        warn.setFill(Color.rgb(128, 128, 128, 0.8));
        if (warn.getText().equals(  VALID_RGB)) warn.setFill(Color.GREEN);
        if (warn.getText().equals(INVALID_RGB)) warn.setFill(Color.RED  );
        getChildren().add(warn);

        Text text = new Text(dataList.get(0));
        text.setFont(Font.font(min(paneHeight, paneWidth) / 20));
        text.layoutXProperty().bind(
                widthProperty()
                    .subtract(text.prefWidth(-1))
                    .divide(2)
        );
        text.layoutYProperty().bind(
                heightProperty()
                    .subtract(text.prefHeight(-1))
                    .divide(20)
        );

        Rectangle r1 = new Rectangle(r1XPos - rWidth, r1YPos, rWidth, r1Height);
        r1.setFill(Color.rgb(color[0], color[1], color[2]));
        r1.setStroke(Color.rgb(color[0], color[1], color[2]));
        r1.setStrokeWidth(0.05 * count);

        Text text1 = new Text(dataList.get(1) + ": " + sum);
        text1.setFont(Font.font(min(r1Height / 25 , rWidth * 4 / 5)));
        text1.setLayoutX(r1XPos + paneWidth / 200);
        text1.layoutYProperty().bind(
                r1.heightProperty()
                        .subtract(text1.prefHeight(-1))
                        .divide(2)
                        .add(r1YPos)
        );

        for (int i = 2; i < dataList.size(); i += 2) {

            double weight = Double.parseDouble(dataList.get(i + 1)) / sum;

            Rectangle r = new Rectangle(rXPos, yPos, rWidth, r1Height * weight);
            r.setFill(Color.rgb(color[3], color[4], color[5]));
            r.setStroke(Color.rgb(color[3], color[4], color[5]));

            Path path = new Path();
            path.getElements().addAll(
                    new MoveTo(r1XPos, lineY),
                    new LineTo(r1XPos, lineY + r1Height * weight),
                    new CubicCurveTo(
                            ((r1XPos + rXPos) * 4 / 11), (lineY + r1Height * weight),
                            ((r1XPos + rXPos) * 7 / 11), (yPos +  r1Height * weight),
                                       rXPos           , (yPos +  r1Height * weight)),
                    new LineTo(rXPos, yPos),
                    new CubicCurveTo(
                            ((r1XPos + rXPos) * 7 / 11), yPos ,
                            ((r1XPos + rXPos) * 4 / 11), lineY,
                              r1XPos                   , lineY)
            );
            Stop[] stops = new Stop[]{
                    new Stop(0, Color.rgb(color[0], color[1], color[2], 0.55)),
                    new Stop(1, Color.rgb(color[3], color[4], color[5], 0.55))
            };
            LinearGradient linearGradient = new LinearGradient(
                    0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops
            );
            path.setFill(linearGradient);
            path.setStroke(linearGradient);
            path.setStrokeWidth(0.5);

            getChildren().addAll(r, path);

            yPos  += r1Height * weight + distance;
            lineY += r1Height * weight;
        }

        yPos  = paneHeight / 20;
        lineY = r1YPos;

        for (int i = 2; i < dataList.size(); i += 2) {

            double weight = Double.parseDouble(dataList.get(i + 1)) / sum;

            Text text2 = new Text(dataList.get(i) + ": " + dataList.get(i + 1));
            text2.setFont(Font.font(min(r1Height / 25 , rWidth * 4 / 5)));
            text2.setLayoutX(rXPos - text2.boundsInLocalProperty().get().getWidth() - paneWidth / 200);
            text2.setLayoutY(yPos + r1Height * weight / 2 + r1Height / 80);

            getChildren().add(text2);

            yPos  += r1Height * weight + distance;
            lineY += r1Height * weight;
        }

        Text showUrl = new Text("Current source: " + filePath);
        showUrl.setFont(Font.font(min(getHeight(), getWidth()) / 50));
        showUrl.setLayoutX(r1XPos - rWidth);
        showUrl.setLayoutY(yPos - distance + paneHeight / 40);
        showUrl.setWrappingWidth(paneWidth * 0.7);

        Button switchButton = new Button("Switch Source");
        switchButton.setPrefSize(paneWidth * 0.2, paneHeight * 0.05);
        switchButton.setOnAction(e -> {
            Pane fileSelect = new SelectFile();
            Scene scene = new Scene(fileSelect, 400, 400);

            Stage stage = (Stage) getScene().getWindow();
            stage.setTitle("Select the Source File");
            stage.setScene(scene);
            stage.show();
        });
        switchButton.setLayoutX(rXPos + rWidth - switchButton.getPrefWidth());
        switchButton.setLayoutY(yPos - distance + paneHeight / 40);

        getChildren().addAll(r1, text1, text, newItemButton, changeItemButton, showUrl, switchButton);
    }

//    ========== Changing the color of columns ==========
    void colorChange() {

        r1.setPrefSize(40, 20);
        r1.setTooltip(R);
        r1.layoutXProperty().bind(
                widthProperty()
                        .subtract(getWidth())
        );
        r1.layoutYProperty().bind(
                heightProperty()
                        .subtract(r1.heightProperty())
        );

        g1.setPrefSize(40, 20);
        g1.setTooltip(G);
        g1.layoutXProperty().bind(
                widthProperty()
                        .subtract(getWidth())
                        .add(r1.widthProperty())
        );
        g1.layoutYProperty().bind(
                heightProperty()
                        .subtract(g1.heightProperty())
        );

        b1.setPrefSize(40, 20);
        b1.setTooltip(B);
        b1.layoutXProperty().bind(
                widthProperty()
                        .subtract(getWidth())
                        .add(g1.widthProperty())
                        .add(r1.widthProperty())
        );
        b1.layoutYProperty().bind(
                heightProperty()
                        .subtract(b1.heightProperty())
        );

        b2.setPrefSize(40, 20);
        b2.setTooltip(B);
        b2.layoutXProperty().bind(
                widthProperty()
                        .subtract(b2.widthProperty())
        );
        b2.layoutYProperty().bind(
                heightProperty()
                        .subtract(b2.heightProperty())
        );

        g2.setPrefSize(40, 20);
        g2.setTooltip(G);
        g2.layoutXProperty().bind(
                widthProperty()
                        .subtract(g2.widthProperty())
                        .subtract(b2.widthProperty())
        );
        g2.layoutYProperty().bind(
                heightProperty()
                        .subtract(g2.heightProperty())
        );

        r2.setPrefSize(40, 20);
        r2.setTooltip(R);
        r2.layoutXProperty().bind(
                widthProperty()
                        .subtract(r2.widthProperty())
                        .subtract(g2.widthProperty())
                        .subtract(b2.widthProperty())
        );
        r2.layoutYProperty().bind(
                heightProperty()
                        .subtract(r2.heightProperty())
        );

        Button button = colorButton();
        button.setPrefSize(getWidth() * 0.2, getHeight() * 0.05);
        button.setLayoutX(getWidth() * 0.5 - button.getPrefWidth() / 2);
        button.setLayoutY(getHeight() * 0.945 - button.getPrefHeight() / 2);

        Label label1 = new Label("RGB for left column: ");
        label1.setFont(Font.font(min(getHeight(), getWidth()) / 30));
        label1.layoutXProperty().bind(
                widthProperty()
                        .subtract(getWidth())
        );
        label1.layoutYProperty().bind(
                heightProperty()
                        .subtract(label1.heightProperty())
                        .subtract(r1.heightProperty())
        );

        Label label2 = new Label("RGB for right column: ");
        label2.setFont(Font.font(min(getHeight(), getWidth()) / 30));
        label2.layoutXProperty().bind(
                widthProperty()
                        .subtract(label2.widthProperty())
        );
        label2.layoutYProperty().bind(
                heightProperty()
                        .subtract(label2.heightProperty())
                        .subtract(r2.heightProperty())
        );

        getChildren().addAll(label1, label2, r1, g1, b1, r2, g2, b2, button);
    }

    private Button colorButton() {
        Button button = new Button("Click to Change Color!");
        button.setOnAction(e -> {
            setColor();
            paint();
        });
        return button;
    }

    int getRGB(TextField textField) {
        return textField.getText().isEmpty() ? (int) (Math.random() * 256) : Integer.parseInt(textField.getText());
    }

    private void setColor() {

        int r1Color = getRGB(r1), g1Color = getRGB(g1), b1Color = getRGB(b1);
        int r2Color = getRGB(r2), g2Color = getRGB(g2), b2Color = getRGB(b2);

        if (r1Color > 255 || g1Color > 255 || b1Color > 255 ||
            r1Color < 0   || g1Color < 0   || b1Color < 0   ||
            r2Color > 255 || g2Color > 255 || b2Color > 255 ||
            r2Color < 0   || g2Color < 0   || b2Color < 0   ) {

            System.out.println(INVALID_RGB);
            warn = new Text(INVALID_RGB);
        } else {
            color[0] = r1Color;
            color[1] = g1Color;
            color[2] = b1Color;
            color[3] = r2Color;
            color[4] = g2Color;
            color[5] = b2Color;
            System.out.println(VALID_RGB);
            warn = new Text(VALID_RGB);
        }
    }

//    ========== Adding a new item ==========
    private void addNew() {

        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Add New Item");
        dialog.setHeaderText("Enter Values");

        TextField textField1 = new TextField();
        TextField textField2 = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Item Title: "), 0, 0);
        grid.add(textField1, 1, 0);
        grid.add(new Label("Item Value: "), 0, 1);
        grid.add(textField2, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeCancel) {
                return null;
            }
            if (dialogButton == buttonTypeOk) {
                return new String[]{textField1.getText(), textField2.getText()};
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();

        result.ifPresent(strings -> {

            if (strings[0].isEmpty()) {
                System.out.println("Please input a valid title!");
                return;
            }
            if (strings[1].isEmpty() || isNotNumber(strings[1])) {
                System.out.println("Please input a valid value!");
                return;
            }

            dataList.add(strings[0]);
            dataList.add(strings[1]);
            sum += Double.parseDouble(strings[1]);
            count++;

            overwriteFile();
            paint();
        });
    }

//    ========== Updating the chosen Item ==========
    private void updateItem() {

        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Update Item");
        dialog.setHeaderText("Enter Values");

        TextField title = new TextField();
        title.setEditable(false);
        title.setStyle("-fx-background-color: LIGHTGRAY;");

        TextField value = new TextField();
        value.setEditable(false);
        value.setStyle("-fx-background-color: LIGHTGRAY;");

        ChoiceBox<String> choiceBox = new ChoiceBox<>();

        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            title.setText(newValue);
            title.setEditable(true);
            title.setStyle(null);
            if (title.getText().equals(dataList.get(1))) {
                value.setText(String.valueOf(sum));
                value.setEditable(false);
                value.setStyle("-fx-background-color: LIGHTGRAY;");
            } else {
                value.setText(dataList.get(dataList.indexOf(title.getText()) + 1));
                value.setEditable(true);
                value.setStyle(null);
            }
        });

        GridPane grid = new GridPane();
        grid.add(new Label("Select Item: "), 0, 0);
        grid.add(choiceBox, 1, 0);
        grid.add(new Label("New Title: "), 0, 1);
        grid.add(title, 1, 1);
        grid.add(new Label("New Value: "), 0, 2);
        grid.add(value, 1, 2);

        dialog.getDialogPane().setContent(grid);

        choiceBox.getItems().add(dataList.get(1));
        for (int i = 2; i < dataList.size(); i += 2)
            choiceBox.getItems().add(dataList.get(i));

        ButtonType buttonTypeOk = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeDelete = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeDelete);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        dialog.setResultConverter(dialogButton -> {

            if (dialogButton == buttonTypeCancel) return null;

            if (dialogButton == buttonTypeDelete) {

                isValidSelection(choiceBox.getValue());
                if (choiceBox.getValue() == null || choiceBox.getValue().isEmpty()) return null;

                try {
                    if (choiceBox.getValue().equals(dataList.get(1))) throw new Exception();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Illegal Operation");
                    alert.setContentText("Cannot delete the first item!");
                    alert.showAndWait();
                    return null;
                }

                sum -= Double.parseDouble(dataList.get(dataList.indexOf(choiceBox.getValue()) + 1));
                dataList.remove(dataList.indexOf(choiceBox.getValue()) + 1);
                dataList.remove(choiceBox.getValue());
                count--;

                overwriteFile();
                paint();

                return null;
            }

            if (dialogButton == buttonTypeOk) {

                isValidSelection(choiceBox.getValue());
                if (choiceBox.getValue() == null || choiceBox.getValue().isEmpty()) return null;

                try {
                    if (title.getText().isEmpty()) throw new Exception();
                } catch (Exception e) {
                    Alert alert = getAlert("title");
                    alert.showAndWait();
                    return null;
                }

                try {
                    if (value.getText().isEmpty() || isNotNumber(value.getText())) throw new Exception();
                } catch (Exception e) {
                    Alert alert = getAlert("value");
                    alert.showAndWait();
                    return null;
                }

                if (!choiceBox.getValue().equals(dataList.get(1))) {
                    sum -= Double.parseDouble(dataList.get(dataList.indexOf(choiceBox.getValue()) + 1));
                    dataList.set(dataList.indexOf(choiceBox.getValue()) + 1, value.getText());
                    sum += Double.parseDouble(dataList.get(dataList.indexOf(choiceBox.getValue()) + 1));
                }

                dataList.set(dataList.indexOf(choiceBox.getValue()), title.getText());
                return new String[]{choiceBox.getValue(), title.getText(), value.getText()};
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();

        result.ifPresent(strings -> {
            if (strings[0].equals(dataList.get(1)))
                dataList.set(1, strings[1]);

            overwriteFile();
            paint();
        });
    }

    private void isValidSelection(String selection) {
        try {
            if (selection == null || selection.isEmpty()) throw new Exception();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid selection");
            alert.setContentText("Please select a valid item!");
            alert.showAndWait();
        }
    }

    private Alert getAlert(String type) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid " + type);
        alert.setContentText("Please input a valid " + type + "!");
        return alert;
    }

//    ========== Overwriting original file ==========
    private void overwriteFile() {
        try {
            Formatter formatter = new Formatter(filePath);

            formatter.format("%s%n", dataList.get(0));
            formatter.format("%s%n", dataList.get(1));
            for (int i = 2; i < dataList.size(); i += 2)
                formatter.format("%s %s%n", dataList.get(i), dataList.get(i + 1));

            formatter.close();

        } catch (Exception e) {
            System.out.println("Overwriting Error!");
        }
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
