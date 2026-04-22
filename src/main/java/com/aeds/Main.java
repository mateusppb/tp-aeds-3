package com.aeds;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.aeds.dao.ConsultaDAO;
import com.aeds.dao.ExameDAO;
import com.aeds.dao.PacienteDAO;
import com.aeds.model.Consulta;
import com.aeds.model.Exame;
import com.aeds.model.Paciente;

public class Main {

    public static void main(String[] args) throws Exception {

        PacienteDAO pacienteDAO = new PacienteDAO();
        ConsultaDAO consultaDAO = new ConsultaDAO();
        ExameDAO exameDAO = new ExameDAO();

        System.out.println("=== TESTE PACIENTE ===");

        // CREATEes
        Paciente p1 = new Paciente(0, "João Silva", "31999990001", LocalDate.of(1990, 5, 10), Arrays.asList("Aspirina", "Losartana"));
        Paciente p2 = new Paciente(0, "Maria Souza", "31999990002", LocalDate.of(1985, 3, 22), Arrays.asList("Metformina"));
        int idP1 = pacienteDAO.create(p1);
        int idP2 = pacienteDAO.create(p2);
        System.out.println("Criados: id=" + idP1 + " e id=" + idP2);

        // READ
        System.out.println("Read p1: " + pacienteDAO.read(idP1));
        System.out.println("Read inexistente: " + pacienteDAO.read(999));

        // UPDATE
        p1.setNome("João Silva Atualizado");
        p1.setMedicamentos(Arrays.asList("Aspirina", "Losartana", "Omeprazol")); // cresce o registro
        boolean upP1 = pacienteDAO.update(p1);
        System.out.println("Update p1: " + upP1);
        System.out.println("Read p1 atualizado: " + pacienteDAO.read(idP1));

        // DELETE
        boolean delP2 = pacienteDAO.delete(idP2);
        System.out.println("Delete p2: " + delP2);
        System.out.println("Read p2 deletado: " + pacienteDAO.read(idP2));

        // DELETE inexistente
        boolean delInexistente = pacienteDAO.delete(999);
        System.out.println("Delete inexistente: " + delInexistente);

        System.out.println("\n=== TESTE EXAME ===");

        // CREATE
        Exame e1 = new Exame(0, "Hemograma", "Exame de sangue completo");
        Exame e2 = new Exame(0, "Raio-X", "Imagem do tórax");
        int idE1 = exameDAO.create(e1);
        int idE2 = exameDAO.create(e2);
        System.out.println("Criados: id=" + idE1 + " e id=" + idE2);

        // READ
        System.out.println("Read e1: " + exameDAO.read(idE1));

        // UPDATE
        e1.setDescricao("Exame de sangue completo com diferencial de leucócitos - descrição bem maior para forçar realocação no arquivo");
        boolean upE1 = exameDAO.update(e1);
        System.out.println("Update e1: " + upE1);
        System.out.println("Read e1 atualizado: " + exameDAO.read(idE1));

        // DELETE
        boolean delE2 = exameDAO.delete(idE2);
        System.out.println("Delete e2: " + delE2);
        System.out.println("Read e2 deletado: " + exameDAO.read(idE2));

        System.out.println("\n=== TESTE CONSULTA ===");

        // CREATE — várias consultas para o mesmo paciente (testa 1:N)
        Consulta c1 = new Consulta(0, 150.0, "Consulta de rotina", LocalDate.of(2024, 1, 10), idP1);
        Consulta c2 = new Consulta(0, 200.0, "Retorno", LocalDate.of(2024, 3, 5), idP1);
        Consulta c3 = new Consulta(0, 300.0, "Urgência", LocalDate.of(2024, 6, 20), idP1);
        int idC1 = consultaDAO.create(c1);
        int idC2 = consultaDAO.create(c2);
        int idC3 = consultaDAO.create(c3);
        System.out.println("Criadas: id=" + idC1 + ", id=" + idC2 + ", id=" + idC3);

        // READ por PK
        System.out.println("Read c1: " + consultaDAO.read(idC1));

        // READ 1:N — todas as consultas do paciente
        System.out.println("\nConsultas do paciente " + idP1 + ":");
        List<Consulta> consultasPaciente = consultaDAO.readByPaciente(idP1);
        for (Consulta c : consultasPaciente) {
            System.out.println("  " + c);
        }

        // UPDATE — força realocação (observação maior)
        c1.setObservacao("Consulta de rotina com anamnese completa e solicitação de exames laboratoriais detalhados");
        boolean upC1 = consultaDAO.update(c1);
        System.out.println("\nUpdate c1: " + upC1);
        System.out.println("Read c1 atualizado: " + consultaDAO.read(idC1));

        // DELETE
        boolean delC2 = consultaDAO.delete(idC2);
        System.out.println("Delete c2: " + delC2);
        System.out.println("Read c2 deletado: " + consultaDAO.read(idC2));

        // READ 1:N após delete — deve ter 2 consultas agora
        System.out.println("\nConsultas do paciente " + idP1 + " após delete:");
        consultasPaciente = consultaDAO.readByPaciente(idP1);
        for (Consulta c : consultasPaciente) {
            System.out.println("  " + c);
        }

        System.out.println("\n=== TESTE PERSISTÊNCIA ===");
        System.out.println("Feche e reabra o programa.");
        System.out.println("Os dados devem ser mantidos e o índice carregado do .idx sem rebuildIndex.");

        pacienteDAO.close();
        consultaDAO.close();
    }
}