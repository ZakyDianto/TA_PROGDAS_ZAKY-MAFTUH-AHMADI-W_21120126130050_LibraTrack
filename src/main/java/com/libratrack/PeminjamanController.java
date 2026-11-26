package com.libratrack;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PeminjamanController {

    @FXML
    private TableView<Transaksi> tablePeminjaman;

    @FXML
    private TextField txtCariPeminjaman;

    // 1. TAMPIL PEMINJAMAN AKTIF (READ)
    @FXML
    private void initialize() {

        for (TableColumn<Transaksi, ?> kolom : tablePeminjaman.getColumns()) {
            kolom.setResizable(false);
            kolom.setReorderable(false);
        }

        TableColumn<Transaksi, Integer> kolomNo =
                (TableColumn<Transaksi, Integer>) tablePeminjaman.getColumns().get(0);

        TableColumn<Transaksi, String> kolomPeminjam =
                (TableColumn<Transaksi, String>) tablePeminjaman.getColumns().get(1);

        TableColumn<Transaksi, String> kolomBuku =
                (TableColumn<Transaksi, String>) tablePeminjaman.getColumns().get(2);

        TableColumn<Transaksi, String> kolomTanggal =
                (TableColumn<Transaksi, String>) tablePeminjaman.getColumns().get(3);

        TableColumn<Transaksi, String> kolomBatas =
                (TableColumn<Transaksi, String>) tablePeminjaman.getColumns().get(4);

        TableColumn<Transaksi, Void> kolomAksi =
                (TableColumn<Transaksi, Void>) tablePeminjaman.getColumns().get(5);

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

        kolomBatas.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAnggota().getMaksimalHariPinjam() + " Hari")
        );

        // Tombol Kembalikan di kolom Aksi
        kolomAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnKembalikan = new Button("Kembalikan");

            {
                btnKembalikan.getStyleClass().add("edit-button");
                btnKembalikan.setOnAction(event -> {
                    Transaksi transaksi = getTableView().getItems().get(getIndex());
                    prosesPengembalian(transaksi);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnKembalikan);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        txtCariPeminjaman.textProperty().addListener((observable, oldValue, newValue) -> {
            cariPeminjaman(newValue);
        });

        cariPeminjaman("");
    }

    // 2. TAMBAH PEMINJAMAN BARU (CREATE)
    @FXML
    private void tambahPeminjaman() {
        if (DataStore.jumlahAnggota == 0 || DataStore.jumlahBuku == 0) {
            tampilkanPeringatan("Pastikan data anggota dan data buku sudah tersedia terlebih dahulu!");
            return;
        }

        if (DataStore.jumlahPeminjaman >= DataStore.daftarPeminjaman.length) {
            tampilkanPeringatan("Kapasitas pencatatan peminjaman penuh!");
            return;
        }

        Dialog<Transaksi> dialog = new Dialog<>();
        dialog.setTitle("Peminjaman Baru");
        dialog.setHeaderText("Pilih anggota dan buku yang akan dipinjam");

        ButtonType tombolSimpan = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(tombolSimpan, ButtonType.CANCEL);

        ComboBox<Anggota> cbAnggota = new ComboBox<>();
        for (int i = 0; i < DataStore.jumlahAnggota; i++) {
            cbAnggota.getItems().add(DataStore.daftarAnggota[i]);
        }
        cbAnggota.setConverter(new StringConverter<>() {
            @Override
            public String toString(Anggota a) {
                return (a == null) ? "" : a.getNomorIdentitas() + " - " + a.getNama() + " (" + a.getKategori() + ")";
            }
            @Override
            public Anggota fromString(String s) { return null; }
        });
        cbAnggota.setPromptText("Pilih anggota");
        cbAnggota.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Buku> cbBuku = new ComboBox<>();
        for (int i = 0; i < DataStore.jumlahBuku; i++) {
            cbBuku.getItems().add(DataStore.daftarBuku[i]);
        }
        cbBuku.setConverter(new StringConverter<>() {
            @Override
            public String toString(Buku b) {
                return (b == null) ? "" : b.getJudul() + " (Stok: " + b.getStok() + ")";
            }
            @Override
            public Buku fromString(String s) { return null; }
        });
        cbBuku.setPromptText("Pilih buku");
        cbBuku.setMaxWidth(Double.MAX_VALUE);

        DatePicker dpTanggal = new DatePicker(LocalDate.now());
        dpTanggal.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Anggota"), 0, 0);
        grid.add(cbAnggota, 1, 0);
        grid.add(new Label("Buku"), 0, 1);
        grid.add(cbBuku, 1, 1);
        grid.add(new Label("Tanggal Pinjam"), 0, 2);
        grid.add(dpTanggal, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Button btnSimpan = (Button) dialog.getDialogPane().lookupButton(tombolSimpan);
        btnSimpan.addEventFilter(ActionEvent.ACTION, event -> {
            Anggota anggota = cbAnggota.getValue();
            Buku buku = cbBuku.getValue();

            if (anggota == null || buku == null || dpTanggal.getValue() == null) {
                tampilkanPeringatan("Anggota, Buku, dan Tanggal Pinjam wajib dipilih!");
                event.consume();
                return;
            }

            if (buku.getStok() <= 0) {
                tampilkanPeringatan("Stok buku \"" + buku.getJudul() + "\" sedang kosong (0)!");
                event.consume();
            }
        });

        dialog.setResultConverter(button -> {
            if (button == tombolSimpan) {
                Anggota anggota = cbAnggota.getValue();
                Buku buku = cbBuku.getValue();
                String tanggal = dpTanggal.getValue().toString();

                // Kurangi stok buku otomatis
                buku.setStok(buku.getStok() - 1);

                int id = (DataStore.jumlahPeminjaman == 0) ? 1 :
                        DataStore.daftarPeminjaman[DataStore.jumlahPeminjaman - 1].getId() + 1;

                return new Transaksi(id, anggota, buku, tanggal);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(transaksi -> {
            DataStore.daftarPeminjaman[DataStore.jumlahPeminjaman] = transaksi;
            DataStore.jumlahPeminjaman++;
            cariPeminjaman(txtCariPeminjaman.getText());
        });
    }

    // 3. PROSES PENGEMBALIAN & HITUNG DENDA (SELECTED DATE + POLYMORPHISM + STACK)
    private void prosesPengembalian(Transaksi transaksi) {
        Anggota peminjam = transaksi.getAnggota();
        int batasHari = peminjam.getMaksimalHariPinjam();
        int tarifDenda = peminjam.getTarifDendaPerHari();
        LocalDate tglPinjam = LocalDate.parse(transaksi.getTanggalPinjam());

        Dialog<LocalDate> dialog = new Dialog<>();
        dialog.setTitle("Pengembalian Buku");
        dialog.setHeaderText("Proses Pengembalian: " + transaksi.getBuku().getJudul());

        ButtonType tombolProses = new ButtonType("Proses Kembali", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(tombolProses, ButtonType.CANCEL);

        // DatePicker tanggal kembali (default hari ini)
        DatePicker dpTanggalKembali = new DatePicker(LocalDate.now());
        dpTanggalKembali.setEditable(false);
        dpTanggalKembali.setMaxWidth(Double.MAX_VALUE);

        // Nonaktifkan tanggal sebelum tanggal pinjam di kalender
        dpTanggalKembali.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && date.isBefore(tglPinjam)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #EEEEEE;");
                }
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Peminjam:"), 0, 0);
        grid.add(new Label(peminjam.getNama() + " (" + peminjam.getKategori() + ")"), 1, 0);

        grid.add(new Label("Tanggal Pinjam:"), 0, 1);
        grid.add(new Label(tglPinjam.toString()), 1, 1);

        grid.add(new Label("Batas Maksimal:"), 0, 2);
        grid.add(new Label(batasHari + " Hari"), 1, 2);

        grid.add(new Label("Tarif Denda:"), 0, 3);
        grid.add(new Label("Rp" + String.format("%,d", tarifDenda).replace(',', '.') + " / hari telat"), 1, 3);

        grid.add(new Label("Tanggal Kembali:"), 0, 4);
        grid.add(dpTanggalKembali, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Validasi tanggal kembali
        Button btnProses = (Button) dialog.getDialogPane().lookupButton(tombolProses);
        btnProses.addEventFilter(ActionEvent.ACTION, event -> {
            LocalDate tglKembali = dpTanggalKembali.getValue();
            if (tglKembali == null) {
                tampilkanPeringatan("Tanggal pengembalian wajib dipilih!");
                event.consume();
            } else if (tglKembali.isBefore(tglPinjam)) {
                tampilkanPeringatan("Tanggal kembali tidak boleh sebelum tanggal pinjam (" + tglPinjam + ")!");
                event.consume();
            }
        });

        dialog.setResultConverter(button -> {
            if (button == tombolProses) {
                return dpTanggalKembali.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(tglKembali -> {
            // 1. Hitung selisih hari otomatis antar dua tanggal
            int lamaAktual = (int) ChronoUnit.DAYS.between(tglPinjam, tglKembali);

            // 2. Hitung keterlambatan & denda menggunakan Polymorphism (Mahasiswa vs Dosen)
            int hariTelat = Math.max(0, lamaAktual - batasHari);
            int denda = peminjam.hitungDenda(hariTelat);

            // 3. Update data transaksi & kembalikan stok buku
            transaksi.setLamaPinjamAktual(lamaAktual);
            transaksi.setDenda(denda);
            transaksi.setStatus("DIKEMBALIKAN");
            transaksi.getBuku().setStok(transaksi.getBuku().getStok() + 1);
            DataStore.totalDendaTerkumpul += denda;

            // 4. Masukkan ke Riwayat Transaksi menggunakan operasi PUSH pada Stack (LIFO)
            DataStore.riwayatStack.push(transaksi);

            // 5. Hapus dari daftar peminjaman aktif (geser elemen array ke kiri)
            int indexHapus = -1;
            for (int i = 0; i < DataStore.jumlahPeminjaman; i++) {
                if (DataStore.daftarPeminjaman[i] == transaksi) {
                    indexHapus = i;
                    break;
                }
            }
            if (indexHapus != -1) {
                for (int i = indexHapus; i < DataStore.jumlahPeminjaman - 1; i++) {
                    DataStore.daftarPeminjaman[i] = DataStore.daftarPeminjaman[i + 1];
                }
                DataStore.daftarPeminjaman[DataStore.jumlahPeminjaman - 1] = null;
                DataStore.jumlahPeminjaman--;
            }

            cariPeminjaman(txtCariPeminjaman.getText());

            // 6. Tampilkan struk ringkasan pengembalian
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Pengembalian Berhasil");
            info.setHeaderText("Buku Telah Dikembalikan");
            info.setContentText(
                    "Buku           : " + transaksi.getBuku().getJudul() + "\n" +
                    "Tgl Pinjam     : " + tglPinjam + "\n" +
                    "Tgl Kembali    : " + tglKembali + "\n" +
                    "Lama Pinjam    : " + lamaAktual + " hari (Batas: " + batasHari + " hari)\n" +
                    "Keterlambatan  : " + hariTelat + " hari\n" +
                    "Total Denda    : Rp" + String.format("%,d", denda).replace(',', '.')
            );
            info.showAndWait();
        });
    }

    // 4. CARI PEMINJAMAN
    private void cariPeminjaman(String kataKunci) {
        tablePeminjaman.getItems().clear();
        String keyword = (kataKunci == null) ? "" : kataKunci.toLowerCase().trim();

        for (int i = 0; i < DataStore.jumlahPeminjaman; i++) {
            Transaksi t = DataStore.daftarPeminjaman[i];

            if (keyword.isEmpty()) {
                tablePeminjaman.getItems().add(t);
            } else {
                boolean cocokNama = t.getAnggota().getNama().toLowerCase().contains(keyword);
                boolean cocokBuku = t.getBuku().getJudul().toLowerCase().contains(keyword);

                if (cocokNama || cocokBuku) {
                    tablePeminjaman.getItems().add(t);
                }
            }
        }
    }

    // 5. HELPER METHOD
    private void tampilkanPeringatan(String pesan) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText("Input Tidak Valid");
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    // 6. NAVIGASI SIDEBAR
    @FXML private void keDashboard(MouseEvent event) { SceneManager.pindahHalaman(event, "dashboard.fxml"); }
    @FXML private void keDataBuku(MouseEvent event) { SceneManager.pindahHalaman(event, "buku.fxml"); }
    @FXML private void keDataAnggota(MouseEvent event) { SceneManager.pindahHalaman(event, "anggota.fxml"); }
    @FXML private void kePeminjaman(MouseEvent event) { SceneManager.pindahHalaman(event, "peminjaman.fxml"); }
    @FXML private void keRiwayat(MouseEvent event) { SceneManager.pindahHalaman(event, "riwayat.fxml"); }
}