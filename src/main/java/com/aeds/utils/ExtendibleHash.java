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
    int capacidade;
    List<Long> posicoes;
    List<Integer> chaves;

    public Bucket(int profundidade, int capacidade) {
        this.profloc = profundidade;
        this.capacidade = capacidade;
        this.chaves = new ArrayList<>();
        this.posicoes = new ArrayList<>();
    }

    public boolean isFull() {
        return chaves.size() >= capacidade;
    }

    public int indexOf(int key) {
        return chaves.indexOf(key);
    }

    public long getPos(int key) {
        int i = indexOf(key);
        return (i != -1) ? posicoes.get(i) : -1;
    }

    public void insert(int key, long pos) {
        chaves.add(key);
        posicoes.add(pos);
    }

    public void remove(int key) {
        int i = indexOf(key);
        if (i != -1) {
            chaves.remove(i);
            posicoes.remove(i);
        }
    }
}

// ================= HASH DINAMICA =================

public class ExtendibleHash {
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
    public void insert(int key, long pos) throws DuplicateKeyException {
        int index = getIndex(key);
        Bucket bucket = directory.get(index);

        if (bucket.indexOf(key) != -1) {
            throw new DuplicateKeyException(key);
        }
        if (!bucket.isFull()) {
            bucket.insert(key, pos);
        } else {
            splitBucket(index);
            insert(key, pos);
        }
    }

    // ================= PROCURA =================
    public long search(int key) throws KeyNotFoundException {
        int index = getIndex(key);
        Bucket bucket = directory.get(index);

        long pos = bucket.getPos(key);

        if (pos != -1) {
            return pos;
        }

        throw new KeyNotFoundException(key);
    }

    // ================= DELETA =================
    public void delete(int key) throws KeyNotFoundException {
        int index = getIndex(key);
        Bucket bucket = directory.get(index);

        if (bucket.indexOf(key) == -1) {
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

        List<Integer> tempKeys = new ArrayList<>(oldBucket.chaves);
        List<Long> tempPos = new ArrayList<>(oldBucket.posicoes);
        oldBucket.chaves.clear();
        oldBucket.posicoes.clear();

        for (int i = 0; i < directory.size(); i++) {
            if (directory.get(i) == oldBucket) {
                if (((i >> oldprofundidade) & 1) == 1) {
                    directory.set(i, newBucket);
                }
            }
        }

        for (int i = 0; i < tempKeys.size(); i++) {
            try {
                insert(tempKeys.get(i), tempPos.get(i));
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
                " -> " + b.chaves +
                " (profloc=" + b.profloc + ")"
            );
        }
    }
}

// ================= MAIN =================

class hashextensivo {
    public static void main(String[] args) {
        ExtendibleHash hash = new ExtendibleHash(2);

        try {
            hash.insert(10, 100);
            hash.insert(20, 200);
            hash.insert(30, 300);
            hash.insert(40, 400);
            hash.insert(50, 500);

            hash.display();

            // TESTE DUPLICADO
            hash.insert(10, 100);

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