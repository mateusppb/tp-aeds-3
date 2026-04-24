package com.aeds.dao;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

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

        File pk = new File("consultas_pk.idx");
        File paciente = new File("consultas_paciente.idx");

        indicePK = new ExtendibleHash(2, true);
        indicePaciente = new ExtendibleHash(2, false);

        if (pk.exists() && paciente.exists()) {
            indicePK.carregarDoDisco("consultas_pk.idx");
            indicePaciente.carregarDoDisco("consultas_paciente.idx");
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
                int id = raf.readInt();          // PK
                raf.readDouble();                // pula valor
                raf.readUTF();                   // pula obs
                raf.readLong();                  // pula data
                int idPaciente = raf.readInt();  // FK

                try {
                    indicePK.insert(id, pos);
                    indicePaciente.insert(idPaciente, pos);
                } catch (Exception e) {}
            }

            raf.seek(pos + 1 + 4 + tam);
        }

        indicePK.salvarEmDisco("consultas_pk.idx");
        indicePaciente.salvarEmDisco("consultas_paciente.idx");
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

            indicePK.salvarEmDisco("consultas_pk.idx");
            indicePaciente.salvarEmDisco("consultas_paciente.idx");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    public Consulta read(int idProcurado) throws IOException {
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

            Consulta con = new Consulta();
            con.lerArquivo(raf, raf.getFilePointer());

            return con;

        } catch (Exception e) {
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
                indicePaciente.delete(antiga.getIdPaciente(), pos);

                indicePK.insert(con.getId(), novaPos);
                indicePaciente.insert(con.getIdPaciente(), novaPos);
            }

            if (antiga.getIdPaciente() != con.getIdPaciente()) {
                indicePaciente.delete(antiga.getIdPaciente(), pos);
                indicePaciente.insert(con.getIdPaciente(), pos);
            }

            indicePK.salvarEmDisco("consultas_pk.idx");
            indicePaciente.salvarEmDisco("consultas_paciente.idx");

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

            Consulta con = new Consulta();
            con.lerArquivo(raf, posDados);

            raf.seek(pos);
            raf.writeByte(1);

            indicePK.delete(idProcurado, pos);
            indicePaciente.delete(con.getIdPaciente(), pos);

            indicePK.salvarEmDisco("consultas_pk.idx");
            indicePaciente.salvarEmDisco("consultas_paciente.idx");

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
                5
            );
            System.out.println("Arquivo ordenado gerado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Consulta> listarOrdenado() throws IOException {

        List<Consulta> lista = new ArrayList<>();
        RandomAccessFile raf = new RandomAccessFile("consultas_ordenado.dat", "r");

        while (raf.getFilePointer() < raf.length()) {

            byte lapide = raf.readByte();
            int tamanho = raf.readInt();
            long pos = raf.getFilePointer();

            if (lapide == 0) {
                Consulta c = new Consulta();
                c.lerArquivo(raf, pos);
                lista.add(c);
            }
            
            raf.seek(pos + tamanho);
        }

        raf.close();
        return lista;
    }

    public List<Consulta> buscarPorPaciente(int idPaciente) throws IOException {
        List<Consulta> resultado = new ArrayList<>();
        try {
            List<Long> posicoes = indicePaciente.search(idPaciente);
            for (long pos : posicoes) {
                raf.seek(pos + 1 + 4); // pula lápide +tamanho
                Consulta con = new Consulta();
                con.lerArquivo(raf, raf.getFilePointer());
                resultado.add(con);
            }
        } catch (Exception e) {
            System.out.println("Nenhuma consulta encontrada para o paciente " + idPaciente);
        }
        return resultado;
    }

}
