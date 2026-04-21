package com.aeds.dao;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.aeds.model.Consulta;
import com.aeds.utils.ExtendibleHash;
import com.aeds.utils.OrdenacaoExterna;

public class ConsultaDAO {
    private RandomAccessFile raf;
    private String arquivo = "consultas.dat";
    private ExtendibleHash indicePK; // Primary Key
    private ExtendibleHash indicePaciente; // 1:N

    public ConsultaDAO() throws IOException {
        raf = new RandomAccessFile(arquivo, "rw");

        if (raf.length() == 0){
            raf.writeInt(0);
        }

        indicePK = new ExtendibleHash(2, true);
        indicePaciente = new ExtendibleHash(2, false);
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
                double valor = raf.readDouble();
                String obs = raf.readUTF();
                long data = raf.readLong();
                int idPaciente = raf.readInt();

                try {
                    indicePK.insert(id, pos);
                    indicePaciente.insert(idPaciente, pos);
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
            indicePK.insert(con.getId(), pos);
            indicePaciente.insert(con.getIdPaciente(), pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public Consulta read(int idProcurado) throws IOException {
        try {
            long pos = indicePK.search(idProcurado).get(0);

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
                long pos = indicePK.search(con.getId()).get(0);

                raf.seek(pos);

                byte lapide = raf.readByte();
                int tamRegistro = raf.readInt();

                long posDados = raf.getFilePointer();
                if (lapide != 0) return false;

                Consulta antiga = new Consulta();
                antiga.lerArquivo(raf, posDados);

                if (tamRegistro >= con.verificarTamanho()) {

                    raf.seek(posDados);
                    con.escreverDados(raf);

                    } else {
                                raf.seek(pos);
                                raf.writeByte(1);

                                long novaPos = raf.length();
                                raf.seek(novaPos);
                                con.escreverArquivo(raf);

                                indicePK.delete(con.getId(), pos);
                                indicePK.insert(con.getId(), novaPos);

                                indicePaciente.delete(antiga.getIdPaciente(), pos);
                                indicePaciente.insert(con.getIdPaciente(), novaPos);
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

            byte lapide = raf.readByte();
            int tam = raf.readInt();
            long posDados = raf.getFilePointer();

            Consulta c = new Consulta();
            c.lerArquivo(raf, posDados);

            raf.seek(pos);
            raf.writeByte(1);

            indicePK.delete(idProcurado, pos);
            indicePaciente.delete(c.getIdPaciente(), pos);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void ordenarPorData() {
        try {
            OrdenacaoExterna.ordenarExternoPorData(
                "consultas.dat",
                "consultas_ordenado.dat",
                5 // tamanho do bloco (pode ajustar)
            );
            System.out.println("Arquivo ordenado gerado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listarOrdenado() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("consultas_ordenado.dat", "r");

        while (raf.getFilePointer() < raf.length()) {

            byte lapide = raf.readByte();
            int tamanho = raf.readInt();

            long pos = raf.getFilePointer();

            if (lapide == 0) {
                Consulta c = new Consulta();
                c.lerArquivo(raf, pos);
                System.out.println(c);
            }

            raf.seek(pos + tamanho);
        }

        raf.close();
    }

}
