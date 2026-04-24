package com.aeds.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.aeds.dao.ExameDAO;
import com.aeds.model.Exame;

public class ExameView extends JFrame {

    private ExameDAO dao;

    private JTextField campoId = new JTextField(5);
    private JTextField campoNome = new JTextField(20);
    private JTextField campoDescricao = new JTextField(20);

    private JTextArea areaResultado = new JTextArea(6, 40);

    public ExameView(ExameDAO dao) {
        this.dao = dao;

        setTitle("Gestão de Exames");
        setLayout(new BorderLayout(10,10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(criarFormulario(), BorderLayout.NORTH);
        add(criarBotoes(), BorderLayout.CENTER);
        add(criarResultado(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel criarFormulario() {
        JPanel p = new JPanel(new GridLayout(3,2,5,5));
        p.setBorder(BorderFactory.createTitledBorder("Dados do Exame"));

        p.add(new JLabel("ID:"));
        p.add(campoId);

        p.add(new JLabel("Nome:"));
        p.add(campoNome);

        p.add(new JLabel("Descrição:"));
        p.add(campoDescricao);

        return p;
    }

    private JPanel criarBotoes() {
        JPanel p = new JPanel(new FlowLayout());

        JButton criar = new JButton("Criar");
        JButton buscar = new JButton("Buscar");
        JButton atualizar = new JButton("Atualizar");
        JButton deletar = new JButton("Deletar");
        JButton limpar = new JButton("Limpar");
        JButton voltar = new JButton("Voltar");

        criar.addActionListener(e -> criar());
        buscar.addActionListener(e -> buscar());
        atualizar.addActionListener(e -> atualizar());
        deletar.addActionListener(e -> deletar());
        limpar.addActionListener(e -> limpar());

        voltar.addActionListener(e -> {
            dispose();
            try {
                new MenuPrincipal();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        p.add(criar);
        p.add(buscar);
        p.add(atualizar);
        p.add(deletar);
        p.add(limpar);
        p.add(voltar);

        return p;
    }

    private JScrollPane criarResultado() {
        areaResultado.setEditable(false);
        areaResultado.setBorder(BorderFactory.createTitledBorder("Resultado"));
        return new JScrollPane(areaResultado);
    }

    // ================= VALIDAÇÃO =================
    private void validarCamposObrigatorios() throws Exception {
        if (campoNome.getText().trim().isEmpty()) {
            throw new Exception("Nome é obrigatório.");
        }

        if (campoDescricao.getText().trim().isEmpty()) {
            throw new Exception("Descrição é obrigatória.");
        }
    }

    private int getId() throws Exception {
        if (campoId.getText().trim().isEmpty()) {
            throw new Exception("Informe o ID.");
        }
        return Integer.parseInt(campoId.getText().trim());
    }

    // ================= AÇÕES =================

    private void criar() {
        try {
            validarCamposObrigatorios();

            Exame e = new Exame(
                0,
                campoNome.getText().trim(),
                campoDescricao.getText().trim()
            );

            int id = dao.create(e);
            areaResultado.setText("Criado com ID: " + id);
            limpar();

        } catch (Exception ex) {
            areaResultado.setText("Erro: " + ex.getMessage());
        }
    }

    private void buscar() {
        try {
            int id = getId();

            Exame e = dao.read(id);

            if (e != null) {
                campoNome.setText(e.getNome());
                campoDescricao.setText(e.getDescricao());
                areaResultado.setText(e.toString());
            } else {
                areaResultado.setText("Exame não encontrado.");
            }

        } catch (Exception ex) {
            areaResultado.setText("Erro: " + ex.getMessage());
        }
    }

    private void atualizar() {
        try {
            int id = getId();
            validarCamposObrigatorios();

            Exame e = new Exame(
                id,
                campoNome.getText().trim(),
                campoDescricao.getText().trim()
            );

            boolean ok = dao.update(e);
            areaResultado.setText(ok ? "Atualizado com sucesso!" : "Falha ao atualizar.");

        } catch (Exception ex) {
            areaResultado.setText("Erro: " + ex.getMessage());
        }
    }

    private void deletar() {
        try {
            int id = getId();

            boolean ok = dao.delete(id);
            areaResultado.setText(ok ? "Deletado com sucesso!" : "Exame não encontrado.");
            limpar();

        } catch (Exception ex) {
            areaResultado.setText("Erro: " + ex.getMessage());
        }
    }

    private void limpar() {
        campoId.setText("");
        campoNome.setText("");
        campoDescricao.setText("");
        areaResultado.setText("");
    }
}