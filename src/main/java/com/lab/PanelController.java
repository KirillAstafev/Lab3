package com.lab;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PanelController implements Initializable {
    @FXML
    TableView<FileInfo> filesTable;

    @FXML
    ComboBox<String> disksBox;

    @FXML
    TextField pathField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileType().getName()));
        fileTypeColumn.setPrefWidth(24);

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Имя");
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
        fileNameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setPrefWidth(120);

        fileSizeColumn.setCellFactory(column -> new TableCell<FileInfo, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    String text = String.format("%,d байт", item);
                    if (item == -1L)
                        text = "Папка с файлами";
                    setText(text);
                }
            }
        });

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Дата изменения");
        fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDateColumn.setPrefWidth(120);

        filesTable.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn);
        filesTable.getSortOrder().add(fileTypeColumn);

        disksBox.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            disksBox.getItems().add(p.toString());
        }

        disksBox.getSelectionModel().select(0);
        updateList(Paths.get(disksBox.getSelectionModel().getSelectedItem()));
    }

    public void updateList(Path path) {
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());

            filesTable.getItems().clear();
            filesTable.getItems().addAll(Files.list(path.toAbsolutePath()).map(FileInfo::new).collect(Collectors.toList()));
            filesTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось обновить список файлов.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void selChangedAction(ActionEvent actionEvent) {
        updateList(Paths.get(disksBox.getSelectionModel().getSelectedItem()));
    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null)
            updateList(upperPath);
    }

    public void tableMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() != 2)
            return;

        FileInfo newInfo = filesTable.getSelectionModel().getSelectedItem();
        if (newInfo.getFileType() != FileInfo.FileType.DIRECTORY)
            return;

        Path newPath = Paths.get(pathField.getText(), newInfo.getFileName());
        updateList(newPath);
    }

    public String getSelectedFileName() {
        return filesTable.getSelectionModel().getSelectedItem() == null ? null :
                filesTable.getSelectionModel().getSelectedItem().getFileName();
    }

    public Path getAbsolutePathForCurrentFile() {
        return filesTable.getSelectionModel().getSelectedItem() == null ? null :
                Paths.get(pathField.getText());
    }

}
