package com.libratrack;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneManager {

    public static void pindahHalaman(MouseEvent event, String namaFileFxml) {
        try {
            URL fxmlUrl = SceneManager.class.getResource("/com/libratrack/" + namaFileFxml);

            if (fxmlUrl == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informasi");
                alert.setHeaderText("Halaman Belum Tersedia");
                alert.setContentText("Halaman \"" + namaFileFxml + "\" akan dibuat pada tahap berikutnya.");
                alert.showAndWait();
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}