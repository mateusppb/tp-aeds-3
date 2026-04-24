package com.aeds.view;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.aeds.dao.ConsultaDAO;
import com.aeds.dao.ExameDAO;
import com.aeds.dao.PacienteDAO;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() throws Exception {

        setTitle("Sistema Médico");
        setSize(300,200);
        setLayout(new java.awt.GridLayout(3,1));

        JButton btnPaciente = new JButton("Pacientes");
        JButton btnConsulta = new JButton("Consultas");
        JButton btnExame = new JButton("Exames");

        btnPaciente.addActionListener(e -> {
            try {
                new PacienteView(new PacienteDAO());
            } catch (Exception ex) {}
        });

        btnConsulta.addActionListener(e -> {
            try {
                new ConsultaView(new ConsultaDAO());
            } catch (Exception ex) {}
        });

        btnExame.addActionListener(e -> {
            try {
                new ExameView(new ExameDAO());
            } catch (Exception ex) {}
        });

        add(btnPaciente);
        add(btnConsulta);
        add(btnExame);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new MenuPrincipal();
    }
}