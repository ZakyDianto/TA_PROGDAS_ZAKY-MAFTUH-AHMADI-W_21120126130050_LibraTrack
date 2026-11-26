package com.libratrack;

public class StackTransaksi {
    private Transaksi[] data;
    private int top;

    public StackTransaksi(int kapasitas) {
        data = new Transaksi[kapasitas];
        top = -1;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == data.length - 1;
    }

    public void push(Transaksi transaksi) {
        if (!isFull()) {
            top++;
            data[top] = transaksi;
        }
    }

    public Transaksi pop() {
        if (!isEmpty()) {
            Transaksi temp = data[top];
            data[top] = null;
            top--;
            return temp;
        }
        return null;
    }

    public Transaksi peek() {
        if (!isEmpty()) {
            return data[top];
        }
        return null;
    }

    public int size() {
        return top + 1;
    }

    // Mengambil elemen berdasarkan indeks untuk ditampilkan ke tabel GUI dari atas ke bawah (LIFO)
    public Transaksi getAt(int index) {
        if (index >= 0 && index <= top) {
            return data[index];
        }
        return null;
    }
}