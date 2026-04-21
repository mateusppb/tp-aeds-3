package com.aeds.dao;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.aeds.model.Exame;
import com.aeds.utils.ExtendibleHash;

public class ExameDAO {

    private RandomAccessFile raf;
    private String arquivo = "exames.dat";
    private ExtendibleHash indicePK;

    public ExameDAO() throws IOException {
        raf = new RandomAccessFile(arquivo, "rw");

        if (raf.length() == 0) {
            raf.writeInt(0);
        }

        indicePK = new ExtendibleHash(2, true);
        rebuildIndex();
    }

    private int getUltimoId() throws IOException {
        raf.seek(0);
        return raf.readInt();
    }

    private void setUltimoId(int id) throws IOException {
        raf.seek(0);
        raf.writeInt(id);
    }

    private void rebuildIndex() throws IOException {
        raf.seek(4);

        while (raf.getFilePointer() < raf.length()) {
            long pos = raf.getFilePointer();

            byte lapide = raf.readByte();
            int tam = raf.readInt();

            if (lapide == 0) {
                int id = raf.readInt();
                try {
                    indicePK.insert(id, pos);
                } catch (Exception e) {}
            }

            raf.seek(pos + 1 + 4 + tam);
        }
    }

    public int create(Exame e) throws IOException {
        int id = getUltimoId() + 1;

        setUltimoId(id);
        e.setId(id);

        long pos = raf.length();
        raf.seek(pos);
        e.escreverArquivo(raf);

        try {
            indicePK.insert(id, pos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return id;
    }

    public Exame read(int idProcurado) throws IOException {
        try {
            long pos = indicePK.search(idProcurado).get(0);

            raf.seek(pos + 1 + 4);
            Exame e = new Exame();
            e.lerArquivo(raf, raf.getFilePointer());
            return e;

        } catch (Exception e) {
            System.out.println("ID não encontrado.");
            return null;
        }
    }

    public boolean update(Exame e) throws IOException {
        try {
            long pos = indicePK.search(e.getId()).get(0);

            raf.seek(pos);
            byte lapide = raf.readByte();
            int tamanhoRegistro = raf.readInt();
            long posDados = raf.getFilePointer();

            if (lapide != 0) return false;

            if (tamanhoRegistro >= e.verificarTamanho()) {
                raf.seek(posDados);
                e.escreverDados(raf);

            } else {
                raf.seek(pos);
                raf.writeByte(1);

                long novaPos = raf.length();
                raf.seek(novaPos);
                e.escreverArquivo(raf);

                indicePK.delete(e.getId(), pos);
                indicePK.insert(e.getId(), novaPos);
            }

            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public boolean delete(int idProcurado) throws IOException {
        try {
            long pos = indicePK.search(idProcurado).get(0);

            raf.seek(pos);
            raf.writeByte(1);

            indicePK.delete(idProcurado, pos);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}