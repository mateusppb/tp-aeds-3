package com.aeds.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Paciente {

    private int id;
    private String nome;
    private String telefone;
    private LocalDate dataNascimento;
    private List<String> medicamentos;

    // CONSTRUTORES

    public Paciente() {
    }

    public Paciente(int id, String nome, String telefone, LocalDate dataNascimento, List<String> medicamentos){

        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.medicamentos = medicamentos;
    }

    //GETTERS E SETTERS

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public List<String> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<String> medicamentos) {
        this.medicamentos = medicamentos;
    }

    // SERIALIZAÇÃO

    public void escreverArquivo(RandomAccessFile raf) throws IOException {
        
        raf.writeByte(0); //lápide (0 - ativo, 1 - inativo)
        int tamanhoRegistro = this.verificarTamanho();
        raf.writeInt(tamanhoRegistro);

        raf.writeInt(id);
        raf.writeUTF(nome);
        raf.writeUTF(telefone);
        raf.writeLong(dataNascimento.toEpochDay());
        
        if (medicamentos != null) {
            raf.writeInt(medicamentos.size());
            for(String m : medicamentos) raf.writeUTF(m);
        
        } else {
            raf.writeInt(0);
        }

    }

    public void escreverDados(RandomAccessFile raf) throws IOException {
        
        raf.writeInt(id);
        raf.writeUTF(nome);
        raf.writeUTF(telefone);
        raf.writeLong(dataNascimento.toEpochDay());
        
        if (medicamentos != null) {
            raf.writeInt(medicamentos.size());
            for(String m : medicamentos) raf.writeUTF(m);
        
        } else {
            raf.writeInt(0);
        }
    }

    public void lerArquivo(RandomAccessFile raf,Long posId) throws IOException {
        raf.seek(posId);
        this.id = raf.readInt();
        this.nome = raf.readUTF();
        this.telefone = raf.readUTF();
        this.dataNascimento = LocalDate.ofEpochDay(raf.readLong());
        
        int qtdMedicamentos = raf.readInt();
        this.medicamentos = new ArrayList<>();
        for(int i = 0; i < qtdMedicamentos; i++) {
            this.medicamentos.add(raf.readUTF());
        }
    }

    public int verificarTamanho() {

        int tamanho = 0;

        tamanho += 4; //id
        tamanho += nome.getBytes(StandardCharsets.UTF_8).length + 2; //nome
        tamanho += 2 + telefone.getBytes(StandardCharsets.UTF_8).length; //telefone
        tamanho += 8; //data de nascimento
        
        tamanho += 4; // quantidade de medicamentos
        if(medicamentos != null){
            for(String m : medicamentos) {
                tamanho += 2 + m.getBytes(StandardCharsets.UTF_8).length; //cada medicamento
            }
        }
            
        return tamanho;
    }

    @Override
    public String toString() {
        return "Paciente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", telefone='" + telefone + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", medicamentos=" + medicamentos +
                '}';
    }
}
