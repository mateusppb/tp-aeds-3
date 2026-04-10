package com.aeds.dao;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.aeds.model.Consulta;
import com.aeds.model.Paciente;

public class ConsultaDAO {
    private RandomAccessFile raf;
    private String arquivo = "consultas.dat";

    public ConsultaDAO() throws IOException {
        raf = new RandomAccessFile(arquivo, "rw");

        if (raf.length() == 0){
            raf.writeInt(0);
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

//------------------------------------------------------------

    public int create(Consulta con) throws IOException {
        
        int id = getUltimoId() + 1;
        setUltimoId(id);
        con.setId(id);

        raf.seek(raf.length());
        con.escreverArquivo(raf);

        return id;
    }

    public Consulta read(int idProcurado) throws IOException {
        raf.seek(4);

        while (raf.getFilePointer() < raf.length()) {
            byte lapide = raf.readByte();
            int tamRegistro = raf.readInt();

            long posRegistro = raf.getFilePointer();

            if(lapide == 0){
                int id = raf.readInt();
                if(idProcurado == id) {
                    Consulta con = new Consulta();
                    con.lerArquivo(raf, posRegistro);

                    return con;
                }
            }
            raf.seek(posRegistro + tamRegistro);
        }
        return null;
    }

    public boolean update(Consulta con) throws IOException {
        raf.seek(4);

        while (raf.getFilePointer() < raf.length()) {
            long posLapide = raf.getFilePointer();
            
            byte lapide = raf.readByte();
            int tamRegistro = raf.readInt();

            long posDados = raf.getFilePointer();

            if(lapide == 0) {
                int id = raf.readInt();
                
                if(con.getId() == id){
                    Paciente pAtual = new Paciente();
                    pAtual.lerArquivo(raf, posDados);
                    
                    if(tamRegistro >= con.verificarTamanho()) {
                        raf.seek(posDados);
                        con.escreverDados(raf);

                    } else {
                        raf.seek(posLapide);
                        raf.writeByte(1);

                        raf.seek(raf.length());
                        con.escreverArquivo(raf);
                    }

                    return true;
                }
            }

            raf.seek(posDados + tamRegistro);
        }
        
        return false;
    }

    public boolean delete(int idProcurado) throws IOException {
        raf.seek(4);

        while(raf.getFilePointer() < raf.length()) {
            long posRegistro = raf.getFilePointer();

            byte lapide = raf.readByte();
            int tamRegistro = raf.readInt();


            if(lapide == 0) {
                int id = raf.readInt();

                if(idProcurado == id) {
                    raf.seek(posRegistro);
                    raf.writeByte(1);
                    return true;
                }
            }
            raf.seek(posRegistro + 1 + 4 + tamRegistro);
        }

        return false;
    }

}
