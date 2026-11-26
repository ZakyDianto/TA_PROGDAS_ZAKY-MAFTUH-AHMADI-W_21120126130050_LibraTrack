package com.libratrack;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

public class DashboardController {

    @FXML private Label lblTotalBuku;
    @FXML private Label lblTotalAnggota;
    @FXML private Label lblPeminjamanAktif;
    @FXML private Label lblTotalDenda;
    @FXML private Label lblTotalEksemplar;

    @FXML private TableView<Transaksi> tablePeminjamanDashboard;

    @FXML
    private void initialize() {
        // 1. Isi 4 Kartu Statistik Utama
        lblTotalBuku.setText(String.valueOf(DataStore.jumlahBuku));
        lblTotalAnggota.setText(String.valueOf(DataStore.jumlahAnggota));
        lblPeminjamanAktif.setText(String.valueOf(DataStore.jumlahPeminjaman));
        lblTotalDenda.setText("Rp" + String.format("%,d", DataStore.totalDendaTerkumpul).replace(',', '.'));

        // Hitung total stok fisik seluruh buku
        int totalStok = 0;
        for (int i = 0; i < DataStore.jumlahBuku; i++) {
            totalStok += DataStore.daftarBuku[i].getStok();
        }
        lblTotalEksemplar.setText(totalStok + " stok buku siap dipinjam");

        // 2. Setup Tabel Ringkasan Peminjaman Aktif
        for (TableColumn<Transaksi, ?> kolom : tablePeminjamanDashboard.getColumns()) {
            kolom.setResizable(false);
            kolom.setReorderable(false);
        }

        TableColumn<Transaksi, String> kolomPeminjam =
                (TableColumn<Transaksi, String>) tablePeminjamanDashboard.getColumns().get(0);
        TableColumn<Transaksi, String> kolomBuku =
                (TableColumn<Transaksi, String>) tablePeminjamanDashboard.getColumns().get(1);
        TableColumn<Transaksi, String> kolomTanggal =
                (TableColumn<Transaksi, String>) tablePeminjamanDashboard.getColumns().get(2);
        TableColumn<Transaksi, String> kolomBatas =
                (TableColumn<Transaksi, String>) tablePeminjamanDashboard.getColumns().get(3);

        kolomPeminjam.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAnggota().getNama())
        );
        kolomBuku.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getBuku().getJudul())
        );
        kolomTanggal.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTanggalPinjam())
        );
        kolomBatas.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAnggota().getMaksimalHariPinjam() + " Hari")
        );

        tablePeminjamanDashboard.setPlaceholder(new Label("Belum ada buku yang sedang dipinjam."));

        for (int i = 0; i < DataStore.jumlahPeminjaman; i++) {
            tablePeminjamanDashboard.getItems().add(DataStore.daftarPeminjaman[i]);
        }
    }

    // NAVIGASI SIDEBAR
    @FXML private void keDashboard(MouseEvent event) { SceneManager.pindahHalaman(event, "dashboard.fxml"); }
    @FXML private void keDataBuku(MouseEvent event) { SceneManager.pindahHalaman(event, "buku.fxml"); }
    @FXML private void keDataAnggota(MouseEvent event) { SceneManager.pindahHalaman(event, "anggota.fxml"); }
    @FXML private void kePeminjaman(MouseEvent event) { SceneManager.pindahHalaman(event, "peminjaman.fxml"); }
    @FXML private void keRiwayat(MouseEvent event) { SceneManager.pindahHalaman(event, "riwayat.fxml"); }
}