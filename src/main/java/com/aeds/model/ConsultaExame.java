package com.aeds.model;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ConsultaExame {

    private int id;
    private int idConsulta;
    private int idExame;

    public ConsultaExame() {
    }

    public ConsultaExame(int idConsulta, int idExame) {
        this.idConsulta = idConsulta;
        this.idExame = idExame;
    }

    public ConsultaExame(int id, int idConsulta, int idExame) {
        this.id = id;
        this.idConsulta = idConsulta;
        this.idExame = idExame;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdConsulta() { return idConsulta; }
    public void setIdConsulta(int idConsulta) { this.idConsulta = idConsulta; }

    public int getIdExame() { return idExame; }
    public void setIdExame(int idExame) { this.idExame = idExame; }


    public void escreverArquivo(RandomAccessFile raf) throws IOException {
        raf.writeByte(0);
        raf.writeInt(verificarTamanho());

        raf.writeInt(id);
        raf.writeInt(idConsulta);
        raf.writeInt(idExame);
    }

    public void escreverDados(RandomAccessFile raf) throws IOException {
        raf.writeInt(id);
        raf.writeInt(idConsulta);
        raf.writeInt(idExame);
    }

    public void lerArquivo(RandomAccessFile raf, long pos) throws IOException {
        raf.seek(pos);

        this.id = raf.readInt();
        this.idConsulta = raf.readInt();
        this.idExame = raf.readInt();
    }

    public int verificarTamanho() {
        return 4 + 4 + 4; // id + idConsulta + idExame
    }
}