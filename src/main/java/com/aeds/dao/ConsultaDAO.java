package com.aeds.dao;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.aeds.model.Consulta;
import com.aeds.utils.ExtendibleHash;

public class ConsultaDAO {
    private RandomAccessFile raf;
    private String arquivo = "consultas.dat";
    private ExtendibleHash indice;

    public ConsultaDAO() throws IOException {
        raf = new RandomAccessFile(arquivo, "rw");

        if (raf.length() == 0){
            raf.writeInt(0);
        }

        indice = new ExtendibleHash(2);
        rebuildIndex();
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
                    indice.insert(id, pos);
                } catch (Exception e) {}
            }

            raf.seek(pos + 1 + 4 + tam);
        }
    }

//------------------------------------------------------------

    public int create(Consulta con) throws IOException {
        
        int id = getUltimoId() + 1;
        setUltimoId(id);
        con.setId(id);

        long pos = raf.length();

        raf.seek(pos);
        con.escreverArquivo(raf);

        try {
            indice.insert(con.getId(), pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public Consulta read(int idProcurado) throws IOException {
        try {
            long pos = indice.search(idProcurado);

            raf.seek(pos + 1 + 4); // pula lápide +tamanho

            Consulta con = new Consulta();
            con.lerArquivo(raf, raf.getFilePointer());

            return con;

        } catch (Exception e) {
            System.out.println("ID não encontrado.");
            return null;
        }
    }

    public boolean update(Consulta con) throws IOException {
        try {
                long pos = indice.search(con.getId());

                raf.seek(pos);

                byte lapide = raf.readByte();
                int tamRegistro = raf.readInt();

                long posDados = raf.getFilePointer();
                if (lapide != 0) return false;

                if (tamRegistro >= con.verificarTamanho()) {

                    raf.seek(posDados);
                    con.escreverDados(raf);

                    } else {
                                raf.seek(pos);
                                raf.writeByte(1);

                                long novaPos = raf.length();
                                raf.seek(novaPos);
                                con.escreverArquivo(raf);

                                indice.delete(con.getId());
                                indice.insert(con.getId(), novaPos);
                            }

                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }

    public boolean delete(int idProcurado) throws IOException {
        try {
            long pos = indice.search(idProcurado);

            raf.seek(pos);
            raf.writeByte(1);

            indice.delete(idProcurado);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

}
