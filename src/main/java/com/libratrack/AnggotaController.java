package com.libratrack;

import javafx.beans.property.SimpleStringProperty;
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

public class AnggotaController {

    @FXML
    private TableView<Anggota> tableAnggota;

    @FXML
    private TextField txtCariAnggota;

    // 1. TAMPIL DATA ANGGOTA (READ)
    @FXML
    private void initialize() {

        // Mengunci lebar kolom tabel
        for (TableColumn<Anggota, ?> kolom : tableAnggota.getColumns()) {
            kolom.setResizable(false);
            kolom.setReorderable(false);
        }

        // Menghubungkan kolom tabel
        TableColumn<Anggota, Integer> kolomNo =
                (TableColumn<Anggota, Integer>) tableAnggota.getColumns().get(0);

        TableColumn<Anggota, String> kolomIdentitas =
                (TableColumn<Anggota, String>) tableAnggota.getColumns().get(1);

        TableColumn<Anggota, String> kolomNama =
                (TableColumn<Anggota, String>) tableAnggota.getColumns().get(2);

        TableColumn<Anggota, KategoriAnggota> kolomKategori =
                (TableColumn<Anggota, KategoriAnggota>) tableAnggota.getColumns().get(3);

        TableColumn<Anggota, String> kolomBatasPinjam =
                (TableColumn<Anggota, String>) tableAnggota.getColumns().get(4);

        TableColumn<Anggota, String> kolomTarifDenda =
                (TableColumn<Anggota, String>) tableAnggota.getColumns().get(5);

        TableColumn<Anggota, Void> kolomAksi =
                (TableColumn<Anggota, Void>) tableAnggota.getColumns().get(6);

        // Nomor urut otomatis (1, 2, 3, ...)
        kolomNo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        kolomIdentitas.setCellValueFactory(new PropertyValueFactory<>("nomorIdentitas"));
        kolomNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        kolomKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));

        // Polymorphism: nilai berbeda otomatis sesuai subclass (Mahasiswa vs Dosen)
        kolomBatasPinjam.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getMaksimalHariPinjam() + " Hari")
        );

        kolomTarifDenda.setCellValueFactory(cell ->
                new SimpleStringProperty("Rp" + String.format("%,d", cell.getValue().getTarifDendaPerHari()).replace(',', '.'))
        );

        // Tombol Edit dan Hapus di kolom Aksi
        kolomAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnHapus = new Button("Hapus");
            private final HBox wadahTombol = new HBox(8, btnEdit, btnHapus);

            {
                btnEdit.getStyleClass().add("edit-button");
                btnHapus.getStyleClass().add("delete-button");
                wadahTombol.setAlignment(Pos.CENTER);

                btnEdit.setOnAction(event -> {
                    Anggota anggota = getTableView().getItems().get(getIndex());
                    editAnggota(anggota);
                });

                btnHapus.setOnAction(event -> {
                    Anggota anggota = getTableView().getItems().get(getIndex());
                    hapusAnggota(anggota);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : wadahTombol);
            }
        });

        // Pencarian real-time saat mengetik
        txtCariAnggota.textProperty().addListener((observable, oldValue, newValue) -> {
            cariAnggota(newValue);
        });

        // Memuat data awal & data terbaru dari DataStore saat halaman dibuka
        cariAnggota("");
    }

    // 2. TAMBAH ANGGOTA (CREATE)
    @FXML
    private void tambahAnggota() {
        if (DataStore.jumlahAnggota >= DataStore.daftarAnggota.length) {
            tampilkanPeringatan("Kapasitas penyimpanan anggota sudah penuh (maksimal 100)!");
            return;
        }

        Dialog<Anggota> dialog = new Dialog<>();
        dialog.setTitle("Tambah Anggota");
        dialog.setHeaderText("Masukkan informasi anggota baru");

        ButtonType tombolSimpan = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(tombolSimpan, ButtonType.CANCEL);

        TextField txtIdentitas = new TextField();
        TextField txtNama = new TextField();
        ComboBox<KategoriAnggota> cbKategori = new ComboBox<>();
        cbKategori.getItems().addAll(KategoriAnggota.MAHASISWA, KategoriAnggota.DOSEN);
        cbKategori.setValue(KategoriAnggota.MAHASISWA);
        cbKategori.setMaxWidth(Double.MAX_VALUE);

        txtIdentitas.setPromptText("Masukkan NIM atau NIP");
        txtNama.setPromptText("Masukkan nama lengkap");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.add(new Label("NIM / NIP"), 0, 0);
        grid.add(txtIdentitas, 1, 0);
        grid.add(new Label("Nama Lengkap"), 0, 1);
        grid.add(txtNama, 1, 1);
        grid.add(new Label("Kategori"), 0, 2);
        grid.add(cbKategori, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Button btnSimpan = (Button) dialog.getDialogPane().lookupButton(tombolSimpan);
        btnSimpan.addEventFilter(ActionEvent.ACTION, event -> {
            String identitas = txtIdentitas.getText().trim();
            String nama = txtNama.getText().trim();

            if (identitas.isEmpty() || nama.isEmpty() || cbKategori.getValue() == null) {
                tampilkanPeringatan("NIM/NIP, Nama Lengkap, dan Kategori wajib diisi!");
                event.consume();
                return;
            }

            // Cek duplikasi NIM / NIP
            for (int i = 0; i < DataStore.jumlahAnggota; i++) {
                if (DataStore.daftarAnggota[i].getNomorIdentitas().equalsIgnoreCase(identitas)) {
                    tampilkanPeringatan("NIM / NIP \"" + identitas + "\" sudah terdaftar!");
                    event.consume();
                    return;
                }
            }

            // Validasi NIM / NIP wajib berupa angka (0-9)
            if (!identitas.matches("\\d+")) {
                tampilkanPeringatan("NIM / NIP hanya boleh berisi angka!");
                event.consume();
                return;
            }
        });

        dialog.setResultConverter(button -> {
            if (button == tombolSimpan) {
                int id = (DataStore.jumlahAnggota == 0) ? 1 :
                        DataStore.daftarAnggota[DataStore.jumlahAnggota - 1].getId() + 1;
                String identitas = txtIdentitas.getText().trim();
                String nama = txtNama.getText().trim();
                KategoriAnggota kategori = cbKategori.getValue();

                // Instansiasi subclass sesuai kategori (Polymorphism)
                if (kategori == KategoriAnggota.MAHASISWA) {
                    return new Mahasiswa(id, identitas, nama);
                } else {
                    return new Dosen(id, identitas, nama);
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(anggota -> {
            DataStore.daftarAnggota[DataStore.jumlahAnggota] = anggota;
            DataStore.jumlahAnggota++;
            cariAnggota(txtCariAnggota.getText());
        });
    }

    // 3. EDIT ANGGOTA (UPDATE)
    private void editAnggota(Anggota anggotaDipilih) {
        Dialog<Anggota> dialog = new Dialog<>();
        dialog.setTitle("Edit Anggota");
        dialog.setHeaderText("Ubah informasi anggota");

        ButtonType tombolSimpan = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(tombolSimpan, ButtonType.CANCEL);

        TextField txtIdentitas = new TextField(anggotaDipilih.getNomorIdentitas());
        TextField txtNama = new TextField(anggotaDipilih.getNama());
        ComboBox<KategoriAnggota> cbKategori = new ComboBox<>();
        cbKategori.getItems().addAll(KategoriAnggota.MAHASISWA, KategoriAnggota.DOSEN);
        cbKategori.setValue(anggotaDipilih.getKategori());
        cbKategori.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.add(new Label("NIM / NIP"), 0, 0);
        grid.add(txtIdentitas, 1, 0);
        grid.add(new Label("Nama Lengkap"), 0, 1);
        grid.add(txtNama, 1, 1);
        grid.add(new Label("Kategori"), 0, 2);
        grid.add(cbKategori, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Button btnSimpan = (Button) dialog.getDialogPane().lookupButton(tombolSimpan);
        btnSimpan.addEventFilter(ActionEvent.ACTION, event -> {
            String identitas = txtIdentitas.getText().trim();
            String nama = txtNama.getText().trim();

            if (identitas.isEmpty() || nama.isEmpty() || cbKategori.getValue() == null) {
                tampilkanPeringatan("NIM/NIP, Nama Lengkap, dan Kategori wajib diisi!");
                event.consume();
                return;
            }

            // Cek duplikasi NIM / NIP (abaikan milik sendiri)
            for (int i = 0; i < DataStore.jumlahAnggota; i++) {
                if (DataStore.daftarAnggota[i] != anggotaDipilih &&
                        DataStore.daftarAnggota[i].getNomorIdentitas().equalsIgnoreCase(identitas)) {
                    tampilkanPeringatan("NIM / NIP \"" + identitas + "\" sudah digunakan anggota lain!");
                    event.consume();
                    return;
                }
            }
        });

        dialog.setResultConverter(button -> {
            if (button == tombolSimpan) {
                String identitas = txtIdentitas.getText().trim();
                String nama = txtNama.getText().trim();
                KategoriAnggota kategori = cbKategori.getValue();

                if (kategori == KategoriAnggota.MAHASISWA) {
                    return new Mahasiswa(anggotaDipilih.getId(), identitas, nama);
                } else {
                    return new Dosen(anggotaDipilih.getId(), identitas, nama);
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(anggotaBaru -> {
            for (int i = 0; i < DataStore.jumlahAnggota; i++) {
                if (DataStore.daftarAnggota[i] == anggotaDipilih) {
                    DataStore.daftarAnggota[i] = anggotaBaru;
                    break;
                }
            }
            cariAnggota(txtCariAnggota.getText());
        });
    }

    // 4. HAPUS ANGGOTA (DELETE)
    private void hapusAnggota(Anggota anggotaDipilih) {
        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
        konfirmasi.setTitle("Konfirmasi Hapus");
        konfirmasi.setHeaderText("Hapus Data Anggota");
        konfirmasi.setContentText("Apakah Anda yakin ingin menghapus anggota \"" + anggotaDipilih.getNama() + "\"?");

        Optional<ButtonType> hasil = konfirmasi.showAndWait();
        if (hasil.isPresent() && hasil.get() == ButtonType.OK) {
            int indexHapus = -1;

            for (int i = 0; i < DataStore.jumlahAnggota; i++) {
                if (DataStore.daftarAnggota[i] == anggotaDipilih) {
                    indexHapus = i;
                    break;
                }
            }

            if (indexHapus != -1) {
                for (int i = indexHapus; i < DataStore.jumlahAnggota - 1; i++) {
                    DataStore.daftarAnggota[i] = DataStore.daftarAnggota[i + 1];
                }
                DataStore.daftarAnggota[DataStore.jumlahAnggota - 1] = null;
                DataStore.jumlahAnggota--;
            }

            cariAnggota(txtCariAnggota.getText());
        }
    }

    // 5. CARI ANGGOTA (SEARCH)
    private void cariAnggota(String kataKunci) {
        tableAnggota.getItems().clear();
        String keyword = (kataKunci == null) ? "" : kataKunci.toLowerCase().trim();

        for (int i = 0; i < DataStore.jumlahAnggota; i++) {
            Anggota anggota = DataStore.daftarAnggota[i];

            if (keyword.isEmpty()) {
                tableAnggota.getItems().add(anggota);
            } else {
                boolean cocokNama = anggota.getNama().toLowerCase().contains(keyword);
                boolean cocokIdentitas = anggota.getNomorIdentitas().toLowerCase().contains(keyword);
                boolean cocokKategori = anggota.getKategori().name().toLowerCase().contains(keyword);

                if (cocokNama || cocokIdentitas || cocokKategori) {
                    tableAnggota.getItems().add(anggota);
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