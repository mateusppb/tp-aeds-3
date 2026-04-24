package com.aeds.dao;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import com.aeds.model.Exame;
import com.aeds.utils.ExtendibleHash;

public class ExameDAO {

    private RandomAccessFile raf;
    private String arquivo = "exames.dat";
    private ExtendibleHash indicePK;

    public ExameDAO() throws IOException {
        raf = new RandomAccessFile("exames.dat", "rw");

        if (raf.length() == 0) {
            raf.writeInt(0);
        }

        indicePK = new ExtendibleHash(2, true);

        File arquivoIdx = new File("exames.idx");

        if (arquivoIdx.exists()) {
            indicePK.carregarDoDisco("exames.idx");
        } else {
            rebuildIndex();
        }
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

        indicePK.salvarEmDisco("exames.idx");
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
            indicePK.salvarEmDisco("exames.idx");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return id;
    }

    public Exame read(int idProcurado) throws IOException {
        try {
            List<Long> posicoes = indicePK.search(idProcurado);

            if (posicoes == null || posicoes.isEmpty()) {
                return null;
            }

            long pos = posicoes.get(0);

            raf.seek(pos);

            byte lapide = raf.readByte();
            int tamanho = raf.readInt();

            if (lapide != 0) {
                return null;
            }

            Exame e = new Exame();
            e.lerArquivo(raf, raf.getFilePointer());

            return e;

        } catch (Exception ex) {
            return null;
        }
    }

    public boolean update(Exame e) throws IOException {
        try {
            long pos = indicePK.search(e.getId()).get(0);

            raf.seek(pos);

            byte lapide = raf.readByte();
            int tamRegistro = raf.readInt();
            long posDados = raf.getFilePointer();

            if (lapide != 0) return false;

            if (tamRegistro >= e.verificarTamanho()) {

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

            indicePK.salvarEmDisco("exames.idx");

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

            indicePK.salvarEmDisco("exames.idx");

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}