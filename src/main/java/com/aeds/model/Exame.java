package com.aeds.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class Exame {

    private int id;
    private String nome;
    private String descricao;

    public Exame() {
    }

    public Exame(int nomeId, String nome, String descricao) {
        this.id = nomeId;
        this.nome = nome;
        this.descricao = descricao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }


    public void escreverArquivo(RandomAccessFile raf) throws IOException {
        raf.writeByte(0);
        int tamanhoRegistro = this.verificarTamanho();
        raf.writeInt(tamanhoRegistro);

        raf.writeInt(id);
        raf.writeUTF(nome);
        raf.writeUTF(descricao);
    }

    public void escreverDados(RandomAccessFile raf) throws IOException {
        raf.writeInt(id);
        raf.writeUTF(nome);
        raf.writeUTF(descricao);
    }

    public void lerArquivo(RandomAccessFile raf, long pos) throws IOException {
        raf.seek(pos);

        this.id = raf.readInt();
        this.nome = raf.readUTF();
        this.descricao = raf.readUTF();
    }

    public int verificarTamanho() {
        int tamanho = 0;

        tamanho += 4; // id
        tamanho += 2 + nome.getBytes(StandardCharsets.UTF_8).length;
        tamanho += 2 + descricao.getBytes(StandardCharsets.UTF_8).length;

        return tamanho;
    }
}