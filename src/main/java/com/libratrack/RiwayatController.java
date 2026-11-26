package com.libratrack;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.util.Optional;

public class RiwayatController {

    @FXML
    private TableView<Transaksi> tableRiwayat;

    @FXML
    private TextField txtCariRiwayat;

    // 1. TAMPIL RIWAYAT DARI STACK (URUTAN LIFO: ATAS KE BAWAH)
    @FXML
    private void initialize() {

        for (TableColumn<Transaksi, ?> kolom : tableRiwayat.getColumns()) {
            kolom.setResizable(false);
            kolom.setReorderable(false);
        }

        TableColumn<Transaksi, Integer> kolomNo =
                (TableColumn<Transaksi, Integer>) tableRiwayat.getColumns().get(0);

        TableColumn<Transaksi, String> kolomPeminjam =
                (TableColumn<Transaksi, String>) tableRiwayat.getColumns().get(1);

        TableColumn<Transaksi, String> kolomBuku =
                (TableColumn<Transaksi, String>) tableRiwayat.getColumns().get(2);

        TableColumn<Transaksi, String> kolomTanggal =
                (TableColumn<Transaksi, String>) tableRiwayat.getColumns().get(3);

        TableColumn<Transaksi, String> kolomLama =
                (TableColumn<Transaksi, String>) tableRiwayat.getColumns().get(4);

        TableColumn<Transaksi, String> kolomDenda =
                (TableColumn<Transaksi, String>) tableRiwayat.getColumns().get(5);

        TableColumn<Transaksi, String> kolomStatus =
                (TableColumn<Transaksi, String>) tableRiwayat.getColumns().get(6);

        // Nomor urut otomatis
        kolomNo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        kolomPeminjam.setCellValueFactory(cell -> {
            Anggota a = cell.getValue().getAnggota();
            return new SimpleStringProperty(a.getNama() + " (" + a.getKategori() + ")");
        });

        kolomBuku.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getBuku().getJudul())
        );

        kolomTanggal.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTanggalPinjam())
        );

        kolomLama.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getLamaPinjamAktual() + " Hari")
        );

        kolomDenda.setCellValueFactory(cell ->
                new SimpleStringProperty("Rp" + String.format("%,d", cell.getValue().getDenda()).replace(',', '.'))
        );

        kolomStatus.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStatus())
        );

        txtCariRiwayat.textProperty().addListener((observable, oldValue, newValue) -> {
            cariRiwayat(newValue);
        });

        cariRiwayat("");
    }

    // 2. OPERASI POP PADA STACK (MENGHAPUS DATA PUNCAK / TERBARU)
    @FXML
    private void popRiwayat() {
        if (DataStore.riwayatStack.isEmpty()) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Stack Kosong");
            warning.setHeaderText("Tidak Ada Riwayat");
            warning.setContentText("Stack riwayat transaksi saat ini masih kosong.");
            warning.showAndWait();
            return;
        }

        Transaksi teratas = DataStore.riwayatStack.peek();

        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
        konfirmasi.setTitle("Konfirmasi Pop Stack");
        konfirmasi.setHeaderText("Hapus Riwayat Paling Atas (LIFO)");
        konfirmasi.setContentText(
                "Hapus riwayat terbaru milik \"" + teratas.getAnggota().getNama() +
                "\" (Buku: " + teratas.getBuku().getJudul() + ") dari puncak Stack?"
        );

        Optional<ButtonType> hasil = konfirmasi.showAndWait();
        if (hasil.isPresent() && hasil.get() == ButtonType.OK) {
            Transaksi dihapus = DataStore.riwayatStack.pop();
            DataStore.totalDendaTerkumpul = Math.max(0, DataStore.totalDendaTerkumpul - dihapus.getDenda());
            cariRiwayat(txtCariRiwayat.getText());
        }
    }

    // 3. CARI RIWAYAT (ITERASI MUNDUR DARI TOP STACK KE BAWAH)
    private void cariRiwayat(String kataKunci) {
        tableRiwayat.getItems().clear();
        String keyword = (kataKunci == null) ? "" : kataKunci.toLowerCase().trim();

        for (int i = DataStore.riwayatStack.size() - 1; i >= 0; i--) {
            Transaksi t = DataStore.riwayatStack.getAt(i);

            if (keyword.isEmpty()) {
                tableRiwayat.getItems().add(t);
            } else {
                boolean cocokNama = t.getAnggota().getNama().toLowerCase().contains(keyword);
                boolean cocokBuku = t.getBuku().getJudul().toLowerCase().contains(keyword);

                if (cocokNama || cocokBuku) {
                    tableRiwayat.getItems().add(t);
                }
            }
        }
    }

    // 4. NAVIGASI SIDEBAR
    @FXML private void keDashboard(MouseEvent event) { SceneManager.pindahHalaman(event, "dashboard.fxml"); }
    @FXML private void keDataBuku(MouseEvent event) { SceneManager.pindahHalaman(event, "buku.fxml"); }
    @FXML private void keDataAnggota(MouseEvent event) { SceneManager.pindahHalaman(event, "anggota.fxml"); }
    @FXML private void kePeminjaman(MouseEvent event) { SceneManager.pindahHalaman(event, "peminjaman.fxml"); }
    @FXML private void keRiwayat(MouseEvent event) { SceneManager.pindahHalaman(event, "riwayat.fxml"); }
}