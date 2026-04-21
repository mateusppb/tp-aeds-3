package com.aeds.utils;

import java.util.ArrayList;
import java.util.List;

// ================= EXCEÇÕES =================

class DuplicateKeyException extends Exception {
    public DuplicateKeyException(int chave) {
        System.out.println("Chave duplicada: "+chave);
    }
}

class KeyNotFoundException extends Exception {
    public KeyNotFoundException(int chave) {
        System.out.println("Chave nao encontrada: "+chave);
    }
}

// ================= BUCKET =================

class Bucket {
    int profloc;
    List<Integer> valores;
    int capacidade;

    public Bucket(int profundidade, int capacidade) {
        this.profloc = profundidade;
        this.capacidade = capacidade;
        this.valores = new ArrayList<>();
    }

    public boolean isFull() {
        return valores.size() >= capacidade;
    }

    public boolean contains(int key) {
        return valores.contains(key);
    }

    public void insert(int key) {
        valores.add(key);
    }

    public void remove(int key) {
        valores.remove((Integer) key);
    }
}

// ================= HASH DINAMICA =================

class ExtendibleHash {
    int profglob;
    int bucketSize;
    List<Bucket> directory;

    public ExtendibleHash(int bucketSize) {
        this.profglob = 1;
        this.bucketSize = bucketSize;
        this.directory = new ArrayList<>();

        // cria 2 buckets iniciais
        for (int i = 0; i < (1 << profglob); i++) {
            directory.add(new Bucket(profglob, bucketSize));
        }
    }

    private int hash(int key) {
        return key;
    }

    private int getIndex(int key) {
        int mask = (1 << profglob) - 1;
        return hash(key) & mask;
    }

    // ================= INSERT =================
    public void insert(int key) throws DuplicateKeyException {
        int index = getIndex(key);
        Bucket bucket = directory.get(index);

        if (bucket.contains(key)) {
            throw new DuplicateKeyException(key);
        }
        if (!bucket.isFull()) {
            bucket.insert(key);
        } else {
            splitBucket(index);
            insert(key);
        }
    }

    // ================= PROCURA =================
    public int search(int key) throws KeyNotFoundException {
        int index = getIndex(key);
        Bucket bucket = directory.get(index);

        if (bucket.contains(key)) {
            return key;
        }

        throw new KeyNotFoundException(key);
    }

    // ================= DELETA =================
    public void delete(int key) throws KeyNotFoundException {
        int index = getIndex(key);
        Bucket bucket = directory.get(index);

        if (!bucket.contains(key)) {
            throw new KeyNotFoundException(key);
        }

        bucket.remove(key);
    }

    // ================= SPLIT =================
    private void splitBucket(int index) {
        Bucket oldBucket = directory.get(index);
        int oldprofundidade = oldBucket.profloc;

        if (oldprofundidade == profglob) {
            doubleDirectory();
        }

        Bucket newBucket = new Bucket(oldprofundidade + 1, bucketSize);
        oldBucket.profloc++;

        List<Integer> temp = new ArrayList<>(oldBucket.valores);
        oldBucket.valores.clear();

        for (int i = 0; i < directory.size(); i++) {
            if (directory.get(i) == oldBucket) {
                if (((i >> oldprofundidade) & 1) == 1) {
                    directory.set(i, newBucket);
                }
            }
        }

        for (int val : temp) {
            try {
                insert(val);
            } catch (DuplicateKeyException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // ================= DOBRAR DIRETÓRIO =================
    private void doubleDirectory() {
        int size = directory.size();
        for (int i = 0; i < size; i++) {
            directory.add(directory.get(i));
        }
        profglob++;
    }

    // ================= TESTE =================
    public void display() {
        System.out.println("\nprofundidade global: " + profglob);
        for (int i = 0; i < directory.size(); i++) {
            Bucket b = directory.get(i);
            System.out.println(
                "Index " + i +
                " -> " + b.valores +
                " (profloc=" + b.profloc + ")"
            );
        }
    }
}

// ================= MAIN =================

public class hashextensivo {
    public static void main(String[] args) {
        ExtendibleHash hash = new ExtendibleHash(2);

        try {
            hash.insert(10);
            hash.insert(20);
            hash.insert(30);
            hash.insert(40);
            hash.insert(50);

            hash.display();

            // TESTE DUPLICADO
            hash.insert(10);

        } catch (DuplicateKeyException e) {
            System.out.println("[ERRO INSERT] " + e.getMessage());
        }

        try {
            System.out.println("\nBusca: " + hash.search(30));

            // ERRO: não existe
            hash.search(999);

        } catch (KeyNotFoundException e) {
            System.out.println("[ERRO SEARCH] " + e.getMessage());
        }

        try {
            hash.delete(20);
            hash.delete(999); // erro

        } catch (KeyNotFoundException e) {
            System.out.println("[ERRO DELETE] " + e.getMessage());
        }

        hash.display();
    }
}