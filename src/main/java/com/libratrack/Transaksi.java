package com.libratrack;

public class Transaksi {
    private int id;
    private Anggota anggota;
    private Buku buku;
    private String tanggalPinjam;
    private int lamaPinjamAktual;
    private int denda;
    private String status;

    public Transaksi(int id, Anggota anggota, Buku buku, String tanggalPinjam) {
        this.id = id;
        this.anggota = anggota;
        this.buku = buku;
        this.tanggalPinjam = tanggalPinjam;
        this.lamaPinjamAktual = 0;
        this.denda = 0;
        this.status = "DIPINJAM";
    }

    public int getId() { return id; }
    public Anggota getAnggota() { return anggota; }
    public Buku getBuku() { return buku; }
    public String getTanggalPinjam() { return tanggalPinjam; }

    public int getLamaPinjamAktual() { return lamaPinjamAktual; }
    public void setLamaPinjamAktual(int lamaPinjamAktual) { this.lamaPinjamAktual = lamaPinjamAktual; }

    public int getDenda() { return denda; }
    public void setDenda(int denda) { this.denda = denda; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}