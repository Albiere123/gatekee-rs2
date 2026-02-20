package br.ufpb.dcx.lima.albiere;

import br.ufpb.dcx.lima.albiere.controllers.LoginController;
import br.ufpb.dcx.lima.albiere.sistema.SistemaRegistrador;

import javax.swing.*;

public class ProgramaGerenciador {
    public static void main(String[] args) {
        SistemaRegistrador sistema = new SistemaRegistrador();

        JFrame janelaBase = new JFrame();
        janelaBase.setUndecorated(true);
        janelaBase.setVisible(true);
        janelaBase.setLocationRelativeTo(null);

        LoginController login = new LoginController(sistema, janelaBase);

        login.iniciarProcessoLogin(true);
    }
}
