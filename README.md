# LibraTrack — Aplikasi Manajemen Peminjaman Buku dan Denda Perpustakaan

**LibraTrack** adalah aplikasi desktop berbasis **JavaFX** untuk mengelola sirkulasi perpustakaan secara terintegrasi, mulai dari pendataan koleksi buku, manajemen anggota (Mahasiswa & Dosen), transaksi peminjaman dan pengembalian berbasis pemilihan tanggal kalender, kalkulasi denda keterlambatan otomatis, hingga pencatatan riwayat transaksi menggunakan struktur data **Stack (LIFO)**.

Proyek ini disusun untuk memenuhi **Tugas Akhir Praktikum Pemrograman Dasar 2026**.

---

## Identitas Penyusun

* **Nama:** Zaky Maftuh Ahmadi Widianto
* **NIM:** 21120126130050
* **Program Studi:** Teknik Komputer — Universitas Diponegoro
* **Mata Kuliah:** Praktikum Pemrograman Dasar (2026)

---

## Fitur Utama

1. **Dashboard Statistik Real-Time**
   * Menampilkan ringkasan **Total Judul Buku**, **Total Eksemplar Tersedia**, **Total Anggota**, **Peminjaman Aktif**, dan **Total Denda Terkumpul**.
   * Dilengkapi tabel ringkasan peminjaman aktif terbaru serta panel ketentuan batas pinjam dan tarif denda.
2. **Manajemen Data Buku (CRUD + Real-Time Search)**
   * Tambah, ubah, dan hapus data buku lengkap dengan validasi stok numerik non-negatif serta konfirmasi penghapusan.
   * Pencarian dinamis berdasarkan **Judul**, **Penulis**, maupun **Kategori** buku.
3. **Manajemen Data Anggota (CRUD + Validasi NIM/NIP)**
   * Pengelolaan anggota kategori **Mahasiswa** dan **Dosen** dengan validasi format angka (`0–9`) dan pencegahan duplikasi NIM/NIP.
   * Penentuan otomatis batas hari pinjam dan tarif denda harian sesuai kategori anggota.
4. **Peminjaman & Pengembalian Otomatis (Selected Date)**
   * Peminjaman buku menggunakan `DatePicker` dan otomatis mengurangi stok fisik buku.
   * Pengembalian buku menggunakan pemilihan tanggal kembali (`DatePicker` dengan penguncian tanggal sebelum tanggal pinjam), menghitung selisih hari otomatis menggunakan `ChronoUnit.DAYS.between()`, mengalkulasi denda sesuai kategori peminjam, mengembalikan stok buku, dan menampilkan struk ringkasan.
5. **Riwayat Transaksi (Stack LIFO)**
   * Setiap transaksi pengembalian yang selesai di-`push` ke dalam `StackTransaksi`, sehingga transaksi terbaru selalu berada di posisi teratas.
   * Mendukung operasi `pop` melalui tombol **Pop Riwayat Teratas** untuk menghapus data puncak stack sekaligus menyinkronkan total denda.

---

## Implementasi 8 Modul Praktikum

| No | Modul Praktikum | Penerapan dalam Kode Program |
| :- | :--- | :--- |
| **1** | **Variabel, Tipe Data, & Array** | Penggunaan tipe data primitif (`int`, `boolean`), `String`, serta penyimpanan terpusat menggunakan **Array 1 Dimensi** (`Buku[]`, `Anggota[]`, `Transaksi[]`) di `DataStore.java`. |
| **2** | **Pengkondisian** | Validasi input form (`if-else`), pengecekan stok buku kosong, validasi format digit NIM/NIP, pencegahan duplikasi data, dan kalkulasi keterlambatan. |
| **3** | **Perulangan** | Iterasi `for` pada *linear search*, pergeseran elemen array ke kiri saat penghapusan data, penghitungan total stok eksemplar, serta pembacaan mundur elemen Stack dari `top` ke `0`. |
| **4** | **Function & Method** | Modularisasi logika ke dalam method terpisah seperti `tambahBuku()`, `editBuku()`, `hapusBuku()`, `cariBuku()`, `prosesPengembalian()`, dan `tampilkanPeringatan()`. |
| **5** | **OOP I** *(Class, Object, Instance)* | Pembuatan blueprint class (`Buku`, `Transaksi`, `SceneManager`, `DataStore`) dan instansiasi objek (`new Buku(...)`, `new Transaksi(...)`). |
| **6** | **OOP II** *(4 Pilar OOP & UDT)* | • **UDT:** `enum KategoriAnggota` (`MAHASISWA`, `DOSEN`).<br>• **Encapsulation & Abstraction:** `abstract class Anggota` dengan atribut `private`, getter/setter, serta *abstract method* `hitungDenda()`.<br>• **Inheritance & Polymorphism:** Subclass `Mahasiswa` (maks. 7 hari, denda Rp2.000/hari) dan `Dosen` (maks. 14 hari, denda Rp1.000/hari) meng-*override* aturan peminjaman dan denda dalam satu array polimorfik `Anggota[]`. |
| **7** | **Stack & Queue** | Implementasi struktur data **Stack (LIFO)** berbasis array pada `StackTransaksi.java` dengan operasi `push()`, `pop()`, `peek()`, `isEmpty()`, dan `isFull()` untuk mengelola log riwayat pengembalian. |
| **8** | **GUI (JavaFX)** | Antarmuka desktop multi-halaman menggunakan **FXML**, styling kustom **CSS** (`style.css`), `TableView`, `ComboBox`, `DatePicker`, `Dialog`, dan `Alert`. |

---

## Struktur Proyek

```text
libratrack/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── libratrack/
        │           ├── Main.java                   # Entry point aplikasi JavaFX
        │           ├── DataStore.java              # Penyimpanan terpusat (Array 1D & Stack)
        │           ├── SceneManager.java           # Pengelola navigasi antar halaman FXML
        │           ├── Buku.java                   # Model entitas Buku
        │           ├── KategoriAnggota.java        # Enum (UDT) kategori anggota
        │           ├── Anggota.java                # Abstract parent class Anggota
        │           ├── Mahasiswa.java              # Subclass Mahasiswa (7 hari, Rp2.000/hari)
        │           ├── Dosen.java                  # Subclass Dosen (14 hari, Rp1.000/hari)
        │           ├── Transaksi.java              # Model entitas Transaksi peminjaman
        │           ├── StackTransaksi.java         # Implementasi struktur data Stack (LIFO)
        │           ├── DashboardController.java    # Controller halaman Dashboard
        │           ├── BukuController.java         # Controller halaman Data Buku
        │           ├── AnggotaController.java      # Controller halaman Data Anggota
        │           ├── PeminjamanController.java   # Controller Peminjaman & Pengembalian
        │           └── RiwayatController.java      # Controller Riwayat Transaksi (Stack)
        └── resources/
            └── com/
                └── libratrack/
                    ├── dashboard.fxml              # Tampilan Dashboard
                    ├── buku.fxml                   # Tampilan Data Buku
                    ├── anggota.fxml                # Tampilan Data Anggota
                    ├── peminjaman.fxml             # Tampilan Peminjaman & Pengembalian
                    ├── riwayat.fxml                # Tampilan Riwayat Transaksi
                    └── style.css                   # Stylesheet tema antarmuka LibraTrack
```

---

## Teknologi yang Digunakan

* **Bahasa Pemrograman:** Java (JDK 17 atau lebih baru)
* **Framework GUI:** JavaFX (FXML & CSS)
* **Build Tool:** Apache Maven

---

## Cara Menjalankan Aplikasi

1. **Clone repository ini:**
   ```bash
   git clone [https://github.com/ZakyDianto/TA_PROGDAS_ZAKY-MAFTUH-AHMADI-W_21120126130050_LibraTrack.git](https://github.com/ZakyDianto/TA_PROGDAS_ZAKY-MAFTUH-AHMADI-W_21120126130050_LibraTrack.git)
   cd TA_PROGDAS_ZAKY-MAFTUH-AHMADI-W_21120126130050_LibraTrack
   ```
2. **Jalankan aplikasi menggunakan Maven:**
   ```bash
   mvn clean javafx:run
   ```