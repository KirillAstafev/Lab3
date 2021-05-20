package com.lab;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Controller {
    @FXML
    VBox leftPanel;

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void btnCopyAction(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(Paths.get("C:\\").toFile());
        chooser.setTitle("Выбор папки");

        Path newDirectory = chooser.showDialog(null).toPath();
        PanelController controller = (PanelController) leftPanel.getProperties().get("ctrl");

        try {
            Path path = controller.getAbsolutePathForCurrentFile();
            Files.copy(Paths.get(path.toString(), controller.getSelectedFileName()),
                    Paths.get(newDirectory.toString(), controller.getSelectedFileName()));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось скопировать файл");
            alert.showAndWait();
        }
    }

    public void btnMoveAction(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(Paths.get("C:\\").toFile());
        chooser.setTitle("Выбор папки");

        Path newDirectory = chooser.showDialog(null).toPath();
        PanelController controller = (PanelController) leftPanel.getProperties().get("ctrl");

        try {
            Path path = controller.getAbsolutePathForCurrentFile();
            Files.move(Paths.get(path.toString(), controller.getSelectedFileName()),
                    Paths.get(newDirectory.toString(), controller.getSelectedFileName()));

            controller.updateList(path);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось переместить файл");
            alert.showAndWait();
        }
    }

    public void btnDeleteAction(ActionEvent actionEvent) {
        PanelController controller = (PanelController) leftPanel.getProperties().get("ctrl");
        try {
            Path path = controller.getAbsolutePathForCurrentFile();
            Files.delete(Paths.get(path.toString(), controller.getSelectedFileName()));

            controller.updateList(path);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось удалить файл");
            alert.showAndWait();
        }
    }
}
