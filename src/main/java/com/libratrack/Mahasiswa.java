package com.libratrack;

public class Mahasiswa extends Anggota {

    public Mahasiswa(int id, String nim, String nama) {
        super(id, nim, nama, KategoriAnggota.MAHASISWA);
    }

    @Override
    public int getMaksimalHariPinjam() {
        return 7;
    }

    @Override
    public int getTarifDendaPerHari() {
        return 2000;
    }

    @Override
    public int hitungDenda(int hariTerlambat) {
        if (hariTerlambat <= 0) {
            return 0;
        }
        return hariTerlambat * getTarifDendaPerHari();
    }
}