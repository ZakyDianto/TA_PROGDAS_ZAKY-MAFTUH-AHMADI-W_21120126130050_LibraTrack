package com.libratrack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import java.util.Optional;

public class BukuController {

    @FXML
    private TableView<Buku> tableBuku;

    @FXML
    private TextField txtCariBuku;

    // 1. TAMPIL DATA BUKU (READ)
    @FXML
    private void initialize() {

        // Mengunci lebar kolom tabel
        for (TableColumn<Buku, ?> kolom : tableBuku.getColumns()) {
            kolom.setResizable(false);
            kolom.setReorderable(false);
        }

        // Menghubungkan kolom tabel
        TableColumn<Buku, Integer> kolomNo =
                (TableColumn<Buku, Integer>) tableBuku.getColumns().get(0);

        TableColumn<Buku, String> kolomJudul =
                (TableColumn<Buku, String>) tableBuku.getColumns().get(1);

        TableColumn<Buku, String> kolomPenulis =
                (TableColumn<Buku, String>) tableBuku.getColumns().get(2);

        TableColumn<Buku, String> kolomKategori =
                (TableColumn<Buku, String>) tableBuku.getColumns().get(3);

        TableColumn<Buku, Integer> kolomStok =
                (TableColumn<Buku, Integer>) tableBuku.getColumns().get(4);

        TableColumn<Buku, Void> kolomAksi =
                (TableColumn<Buku, Void>) tableBuku.getColumns().get(5);

        // Nomor urut otomatis
        kolomNo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        kolomJudul.setCellValueFactory(new PropertyValueFactory<>("judul"));
        kolomPenulis.setCellValueFactory(new PropertyValueFactory<>("penulis"));
        kolomKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        kolomStok.setCellValueFactory(new PropertyValueFactory<>("stok"));

        // Menampilkan tombol Edit dan Hapus
        kolomAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnHapus = new Button("Hapus");
            private final HBox wadahTombol = new HBox(8, btnEdit, btnHapus);

            {
                btnEdit.getStyleClass().add("edit-button");
                btnHapus.getStyleClass().add("delete-button");
                wadahTombol.setAlignment(Pos.CENTER);

                btnEdit.setOnAction(event -> {
                    Buku buku = getTableView().getItems().get(getIndex());
                    editBuku(buku);
                });

                btnHapus.setOnAction(event -> {
                    Buku buku = getTableView().getItems().get(getIndex());
                    hapusBuku(buku);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : wadahTombol);
            }
        });

        // Deteksi otomatis setiap kali pengguna mengetik di kolom pencarian
        txtCariBuku.textProperty().addListener((observable, oldValue, newValue) -> {
            cariBuku(newValue);
        });

        // Memuat data buku yang tersimpan di DataStoresaat halaman dibuka
        cariBuku("");
    }

    // 2. TAMBAH BUKU (CREATE)
    @FXML
    private void tambahBuku() {
        if (DataStore.jumlahBuku >= DataStore.daftarBuku.length) {
            tampilkanPeringatan("Kapasitas penyimpanan buku sudah penuh (maksimal 100 buku)!");
            return;
        }

        Dialog<Buku> dialog = new Dialog<>();
        dialog.setTitle("Tambah Buku");
        dialog.setHeaderText("Masukkan informasi buku baru");

        ButtonType tombolSimpan = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(tombolSimpan, ButtonType.CANCEL);

        TextField txtJudul = new TextField();
        TextField txtPenulis = new TextField();
        TextField txtKategori = new TextField();
        TextField txtStok = new TextField();

        txtJudul.setPromptText("Masukkan judul buku");
        txtPenulis.setPromptText("Masukkan nama penulis");
        txtKategori.setPromptText("Masukkan kategori buku");
        txtStok.setPromptText("Masukkan jumlah stok");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Judul Buku"), 0, 0);
        grid.add(txtJudul, 1, 0);
        grid.add(new Label("Penulis"), 0, 1);
        grid.add(txtPenulis, 1, 1);
        grid.add(new Label("Kategori"), 0, 2);
        grid.add(txtKategori, 1, 2);
        grid.add(new Label("Stok"), 0, 3);
        grid.add(txtStok, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Button btnSimpan = (Button) dialog.getDialogPane().lookupButton(tombolSimpan);
        btnSimpan.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                String judul = txtJudul.getText().trim();
                String penulis = txtPenulis.getText().trim();
                String kategori = txtKategori.getText().trim();
                int stok = Integer.parseInt(txtStok.getText().trim());

                if (judul.isEmpty() || penulis.isEmpty() || kategori.isEmpty() || stok < 0) {
                    tampilkanPeringatan("Semua data harus diisi dan stok tidak boleh negatif!");
                    event.consume();
                }
            } catch (NumberFormatException e) {
                tampilkanPeringatan("Stok harus diisi menggunakan angka!");
                event.consume();
            }
        });

        dialog.setResultConverter(button -> {
            if (button == tombolSimpan) {
                int id = (DataStore.jumlahBuku == 0) ? 1 :
                        DataStore.daftarBuku[DataStore.jumlahBuku - 1].getId() + 1;
                return new Buku(
                        id,
                        txtJudul.getText().trim(),
                        txtPenulis.getText().trim(),
                        txtKategori.getText().trim(),
                        Integer.parseInt(txtStok.getText().trim())
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(buku -> {
            DataStore.daftarBuku[DataStore.jumlahBuku] = buku;
            DataStore.jumlahBuku++;
            cariBuku(txtCariBuku.getText());
        });
    }

    // 3. EDIT BUKU (UPDATE)
    private void editBuku(Buku bukuDipilih) {
        Dialog<Buku> dialog = new Dialog<>();
        dialog.setTitle("Edit Buku");
        dialog.setHeaderText("Ubah informasi buku");

        ButtonType tombolSimpan = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(tombolSimpan, ButtonType.CANCEL);

        TextField txtJudul = new TextField(bukuDipilih.getJudul());
        TextField txtPenulis = new TextField(bukuDipilih.getPenulis());
        TextField txtKategori = new TextField(bukuDipilih.getKategori());
        TextField txtStok = new TextField(String.valueOf(bukuDipilih.getStok()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Judul Buku"), 0, 0);
        grid.add(txtJudul, 1, 0);
        grid.add(new Label("Penulis"), 0, 1);
        grid.add(txtPenulis, 1, 1);
        grid.add(new Label("Kategori"), 0, 2);
        grid.add(txtKategori, 1, 2);
        grid.add(new Label("Stok"), 0, 3);
        grid.add(txtStok, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Button btnSimpan = (Button) dialog.getDialogPane().lookupButton(tombolSimpan);
        btnSimpan.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                String judul = txtJudul.getText().trim();
                String penulis = txtPenulis.getText().trim();
                String kategori = txtKategori.getText().trim();
                int stok = Integer.parseInt(txtStok.getText().trim());

                if (judul.isEmpty() || penulis.isEmpty() || kategori.isEmpty() || stok < 0) {
                    tampilkanPeringatan("Semua data harus diisi dan stok tidak boleh negatif!");
                    event.consume();
                }
            } catch (NumberFormatException e) {
                tampilkanPeringatan("Stok harus diisi menggunakan angka!");
                event.consume();
            }
        });

        dialog.setResultConverter(button -> {
            if (button == tombolSimpan) {
                return new Buku(
                        bukuDipilih.getId(),
                        txtJudul.getText().trim(),
                        txtPenulis.getText().trim(),
                        txtKategori.getText().trim(),
                        Integer.parseInt(txtStok.getText().trim())
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(bukuBaru -> {
            // Mencari posisi buku di dalam array lalu menggantinya
            for (int i = 0; i < DataStore.jumlahBuku; i++) {
                if (DataStore.daftarBuku[i] == bukuDipilih) {
                    DataStore.daftarBuku[i] = bukuBaru;
                    break;
                }
            }
            cariBuku(txtCariBuku.getText());
        });
    }

    // 4. HAPUS BUKU (DELETE)
    private void hapusBuku(Buku bukuDipilih) {
        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
        konfirmasi.setTitle("Konfirmasi Hapus");
        konfirmasi.setHeaderText("Hapus Data Buku");
        konfirmasi.setContentText("Apakah Anda yakin ingin menghapus buku \"" + bukuDipilih.getJudul() + "\"?");

        Optional<ButtonType> hasil = konfirmasi.showAndWait();
        if (hasil.isPresent() && hasil.get() == ButtonType.OK) {
            int indexHapus = -1;

            // Mencari indeks buku yang akan dihapus
            for (int i = 0; i < DataStore.jumlahBuku; i++) {
                if (DataStore.daftarBuku[i] == bukuDipilih) {
                    indexHapus = i;
                    break;
                }
            }

            // Menggeser elemen array ke kiri agar tidak ada slot kosong di tengah
            if (indexHapus != -1) {
                for (int i = indexHapus; i < DataStore.jumlahBuku - 1; i++) {
                    DataStore.daftarBuku[i] = DataStore.daftarBuku[i + 1];
                }
                DataStore.daftarBuku[DataStore.jumlahBuku - 1] = null;
                DataStore.jumlahBuku--;
            }

            cariBuku(txtCariBuku.getText());
        }
    }

    // 5. CARI BUKU (SEARCH)
    private void cariBuku(String kataKunci) {
        tableBuku.getItems().clear();
        String keyword = (kataKunci == null) ? "" : kataKunci.toLowerCase().trim();

        for (int i = 0; i < DataStore.jumlahBuku; i++) {
            Buku buku = DataStore.daftarBuku[i];

            if (keyword.isEmpty()) {
                tableBuku.getItems().add(buku);
            } else {
                boolean cocokJudul = buku.getJudul().toLowerCase().contains(keyword);
                boolean cocokPenulis = buku.getPenulis().toLowerCase().contains(keyword);
                boolean cocokKategori = buku.getKategori().toLowerCase().contains(keyword);

                if (cocokJudul || cocokPenulis || cocokKategori) {
                    tableBuku.getItems().add(buku);
                }
            }
        }
    }

    // 6. HELPER METHOD
    private void tampilkanPeringatan(String pesan) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText("Input Tidak Valid");
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    // 7. NAVIGASI SIDEBAR
    @FXML private void keDashboard(MouseEvent event) { SceneManager.pindahHalaman(event, "dashboard.fxml"); }
    @FXML private void keDataBuku(MouseEvent event) { SceneManager.pindahHalaman(event, "buku.fxml"); }
    @FXML private void keDataAnggota(MouseEvent event) { SceneManager.pindahHalaman(event, "anggota.fxml"); }
    @FXML private void kePeminjaman(MouseEvent event) { SceneManager.pindahHalaman(event, "peminjaman.fxml"); }
    @FXML private void keRiwayat(MouseEvent event) { SceneManager.pindahHalaman(event, "riwayat.fxml"); }
}