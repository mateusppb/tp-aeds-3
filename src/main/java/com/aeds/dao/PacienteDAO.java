package com.aeds.dao;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.aeds.model.Paciente;

public class PacienteDAO {
    private RandomAccessFile raf;
    private String arquivo = "pacientes.dat";

    public PacienteDAO() throws IOException {
        raf = new RandomAccessFile(arquivo, "rw");

        if (raf.length() == 0){
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

    public int create(Paciente p) throws IOException {
        int id = getUltimoId();
        
        id++;
        setUltimoId(id);
        p.setId(id);
        
        raf.seek(raf.length());
        p.escreverArquivo(raf);

        return id;
    }

    public Paciente read(int idProcurado) throws IOException {
        raf.seek(4);
        
        while (raf.getFilePointer() < raf.length()) {
            byte lapide = raf.readByte();
            int tamRegistro = raf.readInt();

            long posRegistro = raf.getFilePointer();

            if(lapide == 0){
                int id = raf.readInt();
                if(idProcurado == id) {
                    Paciente p = new Paciente();
                    p.lerArquivo(raf, posRegistro);

                    return p;
                }
            }
            raf.seek(posRegistro + tamRegistro);
        }

        return null;
    }

    public boolean update(Paciente p) throws IOException {
        raf.seek(4);

        while (raf.getFilePointer() < raf.length()) {
            long posLapide = raf.getFilePointer();
            
            byte lapide = raf.readByte();
            int tamRegistro = raf.readInt();

            long posDados = raf.getFilePointer();

            if(lapide == 0) {
                int id = raf.readInt();
                
                if(p.getId() == id){
                    Paciente pAtual = new Paciente();
                    pAtual.lerArquivo(raf, posDados);
                    
                    if(pAtual.verificarTamanho() >= p.verificarTamanho()) {
                        raf.seek(posDados);
                        p.escreverDados(raf);

                    } else {
                        raf.seek(posLapide);
                        raf.writeByte(1);

                        raf.seek(raf.length());
                        p.escreverArquivo(raf);
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
