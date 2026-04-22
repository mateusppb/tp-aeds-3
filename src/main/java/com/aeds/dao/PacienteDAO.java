package com.aeds.dao;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.aeds.model.Paciente;
import com.aeds.utils.ExtendibleHash;

public class PacienteDAO {
    private RandomAccessFile raf;
    private String arquivo = "pacientes.dat";
    private ExtendibleHash indicePK;

    public PacienteDAO() throws IOException {
        raf = new RandomAccessFile(arquivo, "rw");

        if (raf.length() == 0) {
            raf.writeInt(0);
        }

        indicePK = new ExtendibleHash(2, true);

        File arquivoIdx = new File("pacientes.idx");
        if (arquivoIdx.exists()) {
            indicePK.carregarDoDisco("pacientes.idx");
        } else {
            rebuildIndex();
        }
    }

    public void close() throws IOException {
        if (raf != null) raf.close();
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

        indicePK.salvarEmDisco("pacientes.idx");
    }

//------------------------------------------------------------

    public int create(Paciente p) throws IOException {
        int id = getUltimoId() + 1;

        setUltimoId(id);
        p.setId(id);
        
        long pos = raf.length();
        raf.seek(pos);
        p.escreverArquivo(raf);

        try {
            indicePK.insert(id, pos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    public Paciente read(int idProcurado) throws IOException {
        try {
            long pos = indicePK.search(idProcurado).get(0);

            raf.seek(pos + 1 + 4);
            Paciente p = new Paciente();
            p.lerArquivo(raf, raf.getFilePointer());
            return p;

        } catch (Exception e) {
            System.out.println("ID não encontrado.");
            return null;
        }
    }

    public boolean update(Paciente p) throws IOException {
        try {
            long pos = indicePK.search(p.getId()).get(0);

            raf.seek(pos);
            byte lapide = raf.readByte();
            int tamRegistro = raf.readInt();
            long posDados = raf.getFilePointer();

            if (lapide != 0) return false;

            if (tamRegistro >= p.verificarTamanho()) {
                raf.seek(posDados);
                p.escreverDados(raf);

            } else {
                raf.seek(pos);
                raf.writeByte(1);

                long novaPos = raf.length();
                raf.seek(novaPos);
                p.escreverArquivo(raf);

                indicePK.delete(p.getId(), pos);
                indicePK.insert(p.getId(), novaPos);
            }

            return true;

        } catch (Exception e) {
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
