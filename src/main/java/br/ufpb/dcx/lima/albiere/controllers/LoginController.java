package br.ufpb.dcx.lima.albiere.controllers;

import br.ufpb.dcx.lima.albiere.sistema.SistemaRegistrador;
import javax.swing.*;

public class LoginController { // <--- Sem implements ActionListener

    private final SistemaRegistrador sistema;
    private final JFrame janela;

    public LoginController(SistemaRegistrador sistema, JFrame janela) {
        this.sistema = sistema;
        this.janela = janela;
    }

    public void iniciarProcessoLogin(boolean exit) {
        boolean logado = false;

        while (!logado) {

            String usuario = JOptionPane.showInputDialog(janela, "Digite seu usuário:", "Login - GateKeepers", JOptionPane.QUESTION_MESSAGE);

            if (usuario == null) {
                if(exit) System.exit(0);
                else {
                    janela.dispose();
                    new MainController(sistema);
                }
                return;
            }

            if (usuario.trim().isEmpty()) {
                JOptionPane.showMessageDialog(janela, "Usuário inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (!sistema.procurarUsuarioLogin(usuario)) {
                int resposta = JOptionPane.showConfirmDialog(janela,
                        "Usuário não encontrado. Deseja se registrar?",
                        "GateKeepers",
                        JOptionPane.YES_NO_OPTION);

                if (resposta == JOptionPane.YES_OPTION) {
                    registrar(usuario);

                    if (sistema.getCurrentUser() != null) logado = true;
                }

            } else {
                String senha = JOptionPane.showInputDialog(janela, "Digite sua senha:", "Login - GateKeepers", JOptionPane.QUESTION_MESSAGE);
                if (senha == null) {
                    System.exit(0);
                    return;
                }

                try {
                    if (sistema.login(usuario, senha)) {
                        JOptionPane.showMessageDialog(janela, "Bem-vindo, " + usuario + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        logado = true;
                        janela.dispose();
                        new MainController(sistema);

                    } else {
                        JOptionPane.showMessageDialog(janela, "Senha Incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(janela, "Erro ao logar: " + ex.getMessage());
                }
            }
        }
    }

    public void registrar(String usuarioInicial) {
        try {
            String usuario = (usuarioInicial != null) ? usuarioInicial :
                    JOptionPane.showInputDialog(janela, "Escolha um nome de usuário:", "Registro", JOptionPane.QUESTION_MESSAGE);

            if (usuario == null || usuario.trim().isEmpty()) return;

            if (sistema.procurarUsuarioLogin(usuario)) {
                JOptionPane.showMessageDialog(janela, "Este usuário já existe!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String senha = JOptionPane.showInputDialog(janela, "Crie uma senha:", "Registro", JOptionPane.QUESTION_MESSAGE);
            if (senha == null || senha.trim().isEmpty()) return;

            if (sistema.registrarLogin(usuario, senha)) {
                JOptionPane.showMessageDialog(janela, "Registrado com sucesso! Entrando...");

                sistema.login(usuario, senha);

                janela.dispose();
                new MainController(sistema);

            } else {
                JOptionPane.showMessageDialog(janela, "Erro ao registrar.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(janela, "Erro técnico: " + ex.getMessage());
        }
    }
}