package com.libratrack;

public class Dosen extends Anggota {

    public Dosen(int id, String nip, String nama) {
        super(id, nip, nama, KategoriAnggota.DOSEN);
    }

    @Override
    public int getMaksimalHariPinjam() {
        return 14;
    }

    @Override
    public int getTarifDendaPerHari() {
        return 1000;
    }

    @Override
    public int hitungDenda(int hariTerlambat) {
        if (hariTerlambat <= 0) {
            return 0;
        }
        return hariTerlambat * getTarifDendaPerHari();
    }
}