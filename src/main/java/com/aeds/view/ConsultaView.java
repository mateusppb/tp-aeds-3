package com.aeds.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.aeds.dao.ConsultaDAO;
import com.aeds.model.Consulta;

public class ConsultaView extends JFrame {

    private ConsultaDAO dao;

    private JTextField campoId = new JTextField(5);
    private JTextField campoIdPaciente = new JTextField(5);
    private JTextField campoData = new JTextField(10);
    private JTextField campoValor = new JTextField(10);
    private JTextField campoObs = new JTextField(20);

    private JTextArea areaResultado = new JTextArea(8, 40);

    public ConsultaView(ConsultaDAO dao) {
        this.dao = dao;

        setTitle("Gestão de Consultas");
        setLayout(new BorderLayout(10,10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(criarFormulario(), BorderLayout.NORTH);
        add(criarBotoes(), BorderLayout.CENTER);
        add(new JScrollPane(areaResultado), BorderLayout.SOUTH);

        areaResultado.setEditable(false);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel criarFormulario() {
        JPanel panel = new JPanel(new GridLayout(5,2,5,5));

        panel.setBorder(BorderFactory.createTitledBorder("Dados da Consulta"));

        panel.add(new JLabel("ID:"));
        panel.add(campoId);

        panel.add(new JLabel("ID Paciente:"));
        panel.add(campoIdPaciente);

        panel.add(new JLabel("Data (AAAA-MM-DD):"));
        panel.add(campoData);

        panel.add(new JLabel("Valor:"));
        panel.add(campoValor);

        panel.add(new JLabel("Observação:"));
        panel.add(campoObs);

        return panel;
    }

    private JPanel criarBotoes() {
        JPanel panel = new JPanel();

        JButton btnCriar = new JButton("Criar");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnDeletar = new JButton("Deletar");
        JButton btnBuscarPaciente = new JButton("Buscar por Paciente");
        JButton btnOrdenar = new JButton("Ordenar por Data");
        JButton btnVoltar = new JButton("Voltar ao Menu");

        btnCriar.addActionListener(e -> criar());
        btnBuscar.addActionListener(e -> buscar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnDeletar.addActionListener(e -> deletar());
        btnBuscarPaciente.addActionListener(e -> buscarPorPaciente());
        btnOrdenar.addActionListener(e -> ordenar());

        btnVoltar.addActionListener(e -> {
            dispose(); // fecha a tela atual

            try {
                new MenuPrincipal();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        panel.add(btnCriar);
        panel.add(btnBuscar);
        panel.add(btnAtualizar);
        panel.add(btnDeletar);
        panel.add(btnBuscarPaciente);
        panel.add(btnOrdenar);
        panel.add(btnVoltar);

        return panel;
    }

    // ===== AÇÕES =====

    private void criar() {
        try {
            Consulta c = lerFormulario(0);
            int id = dao.create(c);
            areaResultado.setText("Criado ID: " + id);
        } catch (Exception e) {
            areaResultado.setText("Erro: " + e.getMessage());
        }
    }

    private void buscar() {
        try {
            int id = Integer.parseInt(campoId.getText());
            Consulta c = dao.read(id);

            if (c != null) {
                areaResultado.setText(c.toString());

                campoIdPaciente.setText(String.valueOf(c.getIdPaciente()));
                campoData.setText(c.getData().toString());
                campoValor.setText(String.valueOf(c.getValor()));
                campoObs.setText(c.getObservacao());
            } else {
                areaResultado.setText("Não encontrado");
            }

        } catch (Exception e) {
            areaResultado.setText("Erro: " + e.getMessage());
        }
    }

    private void atualizar() {
        try {
            int id = Integer.parseInt(campoId.getText());
            Consulta c = lerFormulario(id);

            boolean ok = dao.update(c);
            areaResultado.setText(ok ? "Atualizado!" : "Erro ao atualizar");

        } catch (Exception e) {
            areaResultado.setText("Erro: " + e.getMessage());
        }
    }

    private void deletar() {
        try {
            int id = Integer.parseInt(campoId.getText());

            boolean ok = dao.delete(id);
            areaResultado.setText(ok ? "Deletado!" : "Não encontrado");

        } catch (Exception e) {
            areaResultado.setText("Erro: " + e.getMessage());
        }
    }

    private void buscarPorPaciente() {
        try {
            int idPaciente = Integer.parseInt(campoIdPaciente.getText());

            List<Consulta> lista = dao.buscarPorPaciente(idPaciente);

            areaResultado.setText("");

            for (Consulta c : lista) {
                areaResultado.append(c + "\n");
            }

        } catch (Exception e) {
            areaResultado.setText("Erro: " + e.getMessage());
        }
    }

    private Consulta lerFormulario(int id) {
        int idPaciente = Integer.parseInt(campoIdPaciente.getText());
        double valor = Double.parseDouble(campoValor.getText());
        String obs = campoObs.getText();
        LocalDate data = LocalDate.parse(campoData.getText());

        if (obs == null) obs = "";

        return new Consulta(id, valor, obs, data, idPaciente);
    }

    private void ordenar() {
        try {
            dao.ordenarPorData();

            areaResultado.setText("Consultas ordenadas por data:\n\n");

            List<Consulta> lista = dao.listarOrdenado();

            for (Consulta c : lista) {
                areaResultado.append(c + "\n");
            }

        } catch (Exception e) {
            areaResultado.setText("Erro ao ordenar: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        new ConsultaView(new ConsultaDAO());
    }
}