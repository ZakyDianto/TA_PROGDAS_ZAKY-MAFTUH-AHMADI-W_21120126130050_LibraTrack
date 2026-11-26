package com.libratrack;

public abstract class Anggota {
    private int id;
    private String nomorIdentitas;
    private String nama;
    private KategoriAnggota kategori;

    public Anggota(int id, String nomorIdentitas, String nama, KategoriAnggota kategori) {
        this.id = id;
        this.nomorIdentitas = nomorIdentitas;
        this.nama = nama;
        this.kategori = kategori;
    }

    // Encapsulation (Getter & Setter)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomorIdentitas() {
        return nomorIdentitas;
    }

    public void setNomorIdentitas(String nomorIdentitas) {
        this.nomorIdentitas = nomorIdentitas;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public KategoriAnggota getKategori() {
        return kategori;
    }

    public void setKategori(KategoriAnggota kategori) {
        this.kategori = kategori;
    }

    // Abstraction & Polymorphism
    public abstract int getMaksimalHariPinjam();
    public abstract int getTarifDendaPerHari();
    public abstract int hitungDenda(int hariTerlambat);
}