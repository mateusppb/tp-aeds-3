package com.aeds.dao;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.aeds.model.Exame;

public class ExameDAO {

    private RandomAccessFile raf;
    private String arquivo = "exames.dat";

    public ExameDAO() throws IOException {
        raf = new RandomAccessFile(arquivo, "rw");

        if (raf.length() == 0) {
            raf.writeInt(0);
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

    public int create(Exame e) throws IOException {
        int id = getUltimoId() + 1;

        setUltimoId(id);
        e.setId(id);

        raf.seek(raf.length());
        e.escreverArquivo(raf);

        return id;
    }

    public Exame read(int idProcurado) throws IOException {
        raf.seek(4);

        while (raf.getFilePointer() < raf.length()) {
            byte lapide = raf.readByte();
            int tam = raf.readInt();

            long pos = raf.getFilePointer();

            if (lapide == 0) {
                int id = raf.readInt();

                if (id == idProcurado) {
                    Exame e = new Exame();
                    e.lerArquivo(raf, pos);
                    return e;
                }
            }

            raf.seek(pos + tam);
        }

        return null;
    }

    public boolean update(Exame e) throws IOException {
        raf.seek(4);

        while (raf.getFilePointer() < raf.length()) {
            long posLapide = raf.getFilePointer();

            byte lapide = raf.readByte();
            int tamanhoRegistro = raf.readInt();

            long posDados = raf.getFilePointer();

            if (lapide == 0) {
                int id = raf.readInt();

                if (id == e.getId()) {

                    Exame atual = new Exame();
                    atual.lerArquivo(raf, posDados);

                    if (tamanhoRegistro >= e.verificarTamanho()) {
                        raf.seek(posDados);
                        e.escreverDados(raf);
                    } else {
                        raf.seek(posLapide);
                        raf.writeByte(1);

                        raf.seek(raf.length());
                        e.escreverArquivo(raf);
                    }

                    return true;
                }
            }

            raf.seek(posDados + tamanhoRegistro);
        }

        return false;
    }

    public boolean delete(int idProcurado) throws IOException {
        raf.seek(4);

        while (raf.getFilePointer() < raf.length()) {
            long pos = raf.getFilePointer();

            byte lapide = raf.readByte();
            int tam = raf.readInt();

            if (lapide == 0) {
                int id = raf.readInt();

                if (id == idProcurado) {
                    raf.seek(pos);
                    raf.writeByte(1);
                    return true;
                }
            }

            raf.seek(pos + 1 + 4 + tam);
        }

        return false;
    }
}