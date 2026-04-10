package com.aeds.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class Consulta {
    int id;
    LocalDate data;
    String observacao;
    Double valor;
    int idPaciente; //foreign key

    public Consulta () {
    }

    public Consulta(int id, double valor, String observacao, LocalDate data, int idPaciente) {
        this.id = id;
        this.valor = valor;
        this.observacao = observacao;
        this.data = data;
        this.idPaciente = idPaciente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public void escreverArquivo(RandomAccessFile raf) throws IOException {
        raf.writeByte(0); //lápide (0 - ativo, 1 - inativo)
        int tamanhoRegistro = this.verificarTamanho();
        raf.writeInt(tamanhoRegistro);

        raf.writeInt(id);
        raf.writeDouble(valor);
        raf.writeUTF(observacao);
        raf.writeLong(data.toEpochDay());
        raf.writeInt(idPaciente);
    }

    //escrita sem lapide e tamanho (update)
    public void escreverDados(RandomAccessFile raf) throws IOException {
        raf.writeInt(id);
        raf.writeDouble(valor);
        raf.writeUTF(observacao);
        raf.writeLong(data.toEpochDay());
        raf.writeInt(idPaciente);
    }

    public void lerArquivo(RandomAccessFile raf,Long posId) throws IOException {
        raf.seek(posId);

        this.id = raf.readInt();
        this.valor = raf.readDouble();
        this.observacao = raf.readUTF();
        this.data = LocalDate.ofEpochDay(raf.readLong());
        this.idPaciente = raf.readInt();
    }

    //toByteArray
    public int verificarTamanho() {
        int tamanho = 0;

        tamanho+= 4; //id
        tamanho+= 8; //valor
        tamanho += observacao.getBytes(StandardCharsets.UTF_8).length + 2;
        tamanho += 8; //data
        tamanho += 4; //idPaciente

        return tamanho;
    }

}
