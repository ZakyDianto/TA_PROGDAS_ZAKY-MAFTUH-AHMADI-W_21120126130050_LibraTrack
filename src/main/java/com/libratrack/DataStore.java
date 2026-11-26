package com.libratrack;

public class DataStore {

    // Array 1 Dimensi Terpusat
    public static Buku[] daftarBuku = new Buku[100];
    public static int jumlahBuku = 0;

    public static Anggota[] daftarAnggota = new Anggota[100];
    public static int jumlahAnggota = 0;

    // Penyimpanan Peminjaman Aktif (Array 1D) & Riwayat Selesai (Stack LIFO)
    public static Transaksi[] daftarPeminjaman = new Transaksi[100];
    public static int jumlahPeminjaman = 0;
    
    public static StackTransaksi riwayatStack = new StackTransaksi(100);
    public static int totalDendaTerkumpul = 0;

    // Data awal agar tabel & dropdown peminjaman langsung siap diuji
    static {
        daftarBuku[jumlahBuku++] = new Buku(1, "Laskar Pelangi", "Andrea Hirata", "Novel", 10);
        daftarBuku[jumlahBuku++] = new Buku(2, "Bumi", "Tere Liye", "Fantasi", 5);
        daftarBuku[jumlahBuku++] = new Buku(3, "Negeri 5 Menara", "A. Fuadi", "Novel", 8);

        daftarAnggota[jumlahAnggota++] = new Mahasiswa(1, "21120126130001", "Budi Santoso");
        daftarAnggota[jumlahAnggota++] = new Dosen(2, "198504122010121001", "Dr. Siti Aminah");
    }
}