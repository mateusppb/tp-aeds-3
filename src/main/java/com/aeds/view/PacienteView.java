package com.aeds.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.aeds.dao.PacienteDAO;
import com.aeds.model.Paciente;

public class PacienteView extends JFrame {

    private PacienteDAO dao;

    // campos do formulário
    private JTextField campoId        = new JTextField(5);
    private JTextField campoNome      = new JTextField(20);
    private JTextField campoTelefone  = new JTextField(15);
    private JTextField campoNasc      = new JTextField(10); // formato YYYY-MM-DD
    private JTextField campoMeds      = new JTextField(20); // separados por vírgula

    // área de resultado
    private JTextArea areaResultado = new JTextArea(6, 40);

    public PacienteView(PacienteDAO dao) {
        this.dao = dao;

        setTitle("Gestão de Pacientes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(criarFormulario(), BorderLayout.NORTH);
        add(criarBotoes(),     BorderLayout.CENTER);
        add(criarResultado(),  BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel criarFormulario() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Dados do Paciente"));

        panel.add(new JLabel("ID (só para buscar/editar/deletar):"));
        panel.add(campoId);
        panel.add(new JLabel("Nome:"));
        panel.add(campoNome);
        panel.add(new JLabel("Telefone:"));
        panel.add(campoTelefone);
        panel.add(new JLabel("Nascimento (AAAA-MM-DD):"));
        panel.add(campoNasc);
        panel.add(new JLabel("Medicamentos (separados por vírgula):"));
        panel.add(campoMeds);

        return panel;
    }

    private JPanel criarBotoes() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton btnCriar   = new JButton("Criar");
        JButton btnBuscar  = new JButton("Buscar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnDeletar = new JButton("Deletar");
        JButton btnLimpar  = new JButton("Limpar");

        btnCriar.addActionListener(e -> criar());
        btnBuscar.addActionListener(e -> buscar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnDeletar.addActionListener(e -> deletar());
        btnLimpar.addActionListener(e -> limpar());

        panel.add(btnCriar);
        panel.add(btnBuscar);
        panel.add(btnAtualizar);
        panel.add(btnDeletar);
        panel.add(btnLimpar);

        return panel;
    }

    private JScrollPane criarResultado() {
        areaResultado.setEditable(false);
        areaResultado.setBorder(BorderFactory.createTitledBorder("Resultado"));
        return new JScrollPane(areaResultado);
    }

    // ===== AÇÕES =====

    private void criar() {
        try {
            Paciente p = lerFormulario(0);
            int id = dao.create(p);
            areaResultado.setText("Paciente criado com id=" + id);
            limpar();
        } catch (Exception ex) {
            areaResultado.setText("Erro: " + ex.getMessage());
        }
    }

    private void buscar() {
        try {
            int id = Integer.parseInt(campoId.getText().trim());
            Paciente p = dao.read(id);
            if (p != null) {
                areaResultado.setText(p.toString());
                // preenche formulário para facilitar edição
                campoNome.setText(p.getNome());
                campoTelefone.setText(p.getTelefone());
                campoNasc.setText(p.getDataNascimento().toString());
                campoMeds.setText(String.join(", ", p.getMedicamentos()));
            } else {
                areaResultado.setText("Paciente não encontrado.");
            }
        } catch (Exception ex) {
            areaResultado.setText("Erro: " + ex.getMessage());
        }
    }

    private void atualizar() {
        try {
            int id = Integer.parseInt(campoId.getText().trim());
            Paciente p = lerFormulario(id);
            boolean ok = dao.update(p);
            areaResultado.setText(ok ? "Atualizado com sucesso!" : "Falha ao atualizar.");
        } catch (Exception ex) {
            areaResultado.setText("Erro: " + ex.getMessage());
        }
    }

    private void deletar() {
        try {
            int id = Integer.parseInt(campoId.getText().trim());
            boolean ok = dao.delete(id);
            areaResultado.setText(ok ? "Deletado com sucesso!" : "Paciente não encontrado.");
            limpar();
        } catch (Exception ex) {
            areaResultado.setText("Erro: " + ex.getMessage());
        }
    }

    private void limpar() {
        campoId.setText("");
        campoNome.setText("");
        campoTelefone.setText("");
        campoNasc.setText("");
        campoMeds.setText("");
        areaResultado.setText("");
    }

    // ===== HELPER =====

    private Paciente lerFormulario(int id) {
        String nome     = campoNome.getText().trim();
        String telefone = campoTelefone.getText().trim();
        LocalDate nasc  = LocalDate.parse(campoNasc.getText().trim());
        var meds        = Arrays.asList(campoMeds.getText().split(","));
        meds.replaceAll(String::trim);
        return new Paciente(id, nome, telefone, nasc, meds);
    }

    // ===== MAIN DE TESTE =====
    public static void main(String[] args) throws Exception {
        PacienteDAO dao = new PacienteDAO();
        new PacienteView(dao);
    }
}