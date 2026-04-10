package com.aeds;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.aeds.dao.PacienteDAO;
import com.aeds.model.Paciente;

public class Main {

    public static void main(String[] args) {
        try {
            PacienteDAO dao = new PacienteDAO();

            System.out.println("--- Início dos Testes ---");
            
            //testarCriacao(dao);
            //testarLeitura(dao, 1);
             //testarAtualizacao(dao, 1);
            testarExclusao(dao, 1);
            System.out.println("\n--- Fim dos Testes ---");

        } catch (Exception e) {
            System.err.println("Erro ao processar arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 1. TESTE DE CREATE
    public static void testarCriacao(PacienteDAO dao) throws Exception {
        System.out.println("\n[Teste Create]");
        
        List<String> meds = Arrays.asList("Dipirona", "Vitamina C");
        Paciente p1 = new Paciente(-1, "João Silva", "3198888-7777", LocalDate.of(1990, 5, 15), meds);
        
        int idGerado = dao.create(p1);
        System.out.println("Paciente criado com ID: " + idGerado);
        
        Paciente p2 = new Paciente(-1, "Maria Oliveira", "1197777-6666", LocalDate.of(1985, 10, 20), null);
        int idGerado2 = dao.create(p2);
        System.out.println("Paciente criado com ID: " + idGerado2);
    }

    // 2. TESTE DE READ
    public static void testarLeitura(PacienteDAO dao, int id) throws Exception {
        System.out.println("\n[Teste Read]");
        Paciente p = dao.read(id);
        
        if (p != null) {
            System.out.println("Registro encontrado: " + p.toString());
        } else {
            System.out.println("Paciente com ID " + id + " não encontrado ou excluído.");
        }
    }

    // 3. TESTE DE UPDATE
    public static void testarAtualizacao(PacienteDAO dao, int id) throws Exception {
        System.out.println("\n[Teste Update]");
        Paciente p = dao.read(id);
        
        if (p != null) {
            System.out.println("Nome antigo: " + p.getNome());
            p.setNome(p.getNome() + " (Editado)");
            
            List<String> novosMeds = Arrays.asList("Remédio A", "Remédio B", "Remédio C", "Remédio D");
            p.setMedicamentos(novosMeds);

            boolean sucesso = dao.update(p);
            System.out.println("Atualização realizada? " + sucesso);
            
            System.out.println("Novo estado: " + dao.read(id));
        } else {
            System.out.println("Não foi possível atualizar: ID não encontrado.");
        }
    }

    // 4. TESTE DELETE
    public static void testarExclusao(PacienteDAO dao, int id) throws Exception {
        System.out.println("\n[Teste Delete]");
        boolean deletado = dao.delete(id);
        
        if (deletado) {
            System.out.println("Paciente ID " + id + " marcado como excluído (lápide).");
        } else {
            System.out.println("Paciente ID " + id + " não encontrado para exclusão.");
        }
        Paciente p = dao.read(id);
        System.out.println("Resultado da leitura após delete: " + p);
    }
}