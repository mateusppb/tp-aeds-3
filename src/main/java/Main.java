import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.aeds.dao.PacienteDAO;
import com.aeds.model.Paciente;

public class Main {
    public static void main(String[] args) {
        try {
            PacienteDAO dao = new PacienteDAO();

            System.out.println("=== TESTE 1: INSERÇÃO (CREATE) ===");
            List<String> meds1 = new ArrayList<>(Arrays.asList("Dipirona", "Vitamina C"));
            Paciente p1 = new Paciente(0, "Alice Silva", "1199999-0001", LocalDate.of(1995, 1, 15), meds1);
            
            int id1 = dao.create(p1);
            System.out.println("Paciente 1 criado com ID: " + id1);

            Paciente p2 = new Paciente(0, "Bob Souza", "1199999-0002", LocalDate.of(1980, 10, 20), null);
            int id2 = dao.create(p2);
            System.out.println("Paciente 2 criado com ID: " + id2);


            System.out.println("\n=== TESTE 2: BUSCA (READ) ===");
            Paciente busca = dao.read(id1);
            System.out.println("Busca ID " + id1 + ": " + (busca != null ? busca : "Não encontrado"));


            System.out.println("\n=== TESTE 3: ATUALIZAÇÃO (UPDATE) ===");
            System.out.println("Aumentando a lista de medicamentos do " + p1.getNome() + "...");
            // Aumentar o tamanho força o DAO a marcar lápide e criar novo registro no fim
            p1.getMedicamentos().add("Amoxicilina");
            p1.getMedicamentos().add("Ibuprofeno");
            
            boolean updated = dao.update(p1);
            System.out.println("Update realizado: " + updated);
            
            // Verificando se ele ainda é encontrado (com os novos dados)
            Paciente p1Atualizado = dao.read(id1);
            System.out.println("Dados após Update: " + p1Atualizado);


            System.out.println("\n=== TESTE 4: EXCLUSÃO (DELETE) ===");
            boolean deleted = dao.delete(id2);
            System.out.println("Paciente 2 deletado: " + deleted);
            
            Paciente p2Busca = dao.read(id2);
            System.out.println("Busca ID " + id2 + " após delete: " + (p2Busca != null ? p2Busca : "Corretamente não encontrado"));

        } catch (Exception e) {
            System.err.println("ERRO NO TESTE: " + e.getMessage());
            e.printStackTrace();
        }
    }
}