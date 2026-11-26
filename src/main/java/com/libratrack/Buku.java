package com.libratrack;

public class Buku {

    private int id;
    private String judul;
    private String penulis;
    private String kategori;
    private int stok;

    public Buku(int id, String judul, String penulis, String kategori, int stok) {
        this.id = id;
        this.judul = judul;
        this.penulis = penulis;
        this.kategori = kategori;
        this.stok = stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public int getId() {
        return id;
    }

    public String getJudul() {
        return judul;
    }

    public String getPenulis() {
        return penulis;
    }

    public String getKategori() {
        return kategori;
    }

    public int getStok() {
        return stok;
    }

    
}
