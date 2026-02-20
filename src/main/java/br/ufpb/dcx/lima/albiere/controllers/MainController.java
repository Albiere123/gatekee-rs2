package br.ufpb.dcx.lima.albiere.controllers;

import br.ufpb.dcx.lima.albiere.sistema.Registrador;
import br.ufpb.dcx.lima.albiere.sistema.SistemaRegistrador;
import br.ufpb.dcx.lima.albiere.gui.MainView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainController {

    private final SistemaRegistrador sistema;
    private final MainView view;
    private int linhaSenhaRevelada = -1;

    public MainController(SistemaRegistrador sistema) {
        this.sistema = sistema;
        this.view = new MainView();

        inicializarController();
    }

    private void encerrarSistema() {
        int confirm = JOptionPane.showConfirmDialog(view,
                "Deseja salvar as alterações e sair?",
                "Sair",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            sistema.salvarDados();
            System.exit(0);
        } else if (confirm == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }


    private void inicializarController() {
        view.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                encerrarSistema();
            }
        });

        view.getLblUsuarioLogado().setText("Usuário logado: " + sistema.getCurrentUser());
        view.getTabela().getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (row == linhaSenhaRevelada) {
                    setText(value != null ? value.toString() : "");
                    setForeground(new java.awt.Color(0, 100, 0));
                } else {
                    setText("******");
                    setForeground(java.awt.Color.BLACK);
                }

                setHorizontalAlignment(SwingConstants.CENTER);

                return this;
            }
        });

        view.getTabela().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int linha = view.getTabela().rowAtPoint(e.getPoint());
                int coluna = view.getTabela().columnAtPoint(e.getPoint());

                if (coluna == 3 && linha >= 0) {
                    if (linhaSenhaRevelada == linha) {
                        linhaSenhaRevelada = -1;
                    } else {
                        linhaSenhaRevelada = linha;
                    }

                    view.getTabela().repaint();
                } else {
                    linhaSenhaRevelada = -1;
                    view.getTabela().repaint();
                }
            }
        });


        atualizarTabela();

        view.getBtnNovo().addActionListener(e -> adicionarRegistro());
        view.getBtnEditar().addActionListener(e -> editarRegistro());
        view.getBtnExcluir().addActionListener(e -> excluirRegistro());
        view.getBtnTrocarUsuario().addActionListener(e -> trocarUsuario(false));
        view.getBtnSair().addActionListener(e -> encerrarSistema());
        view.getBtnAlterarSenhaLogin().addActionListener(e -> alterarSenhaLogin());

        view.setVisible(true);
    }

    private void trocarUsuario(boolean exit) {
        view.dispose();
        sistema.logout();

        JFrame janelaLogin = new JFrame();
        janelaLogin.setUndecorated(true);
        janelaLogin.setLocationRelativeTo(null);
        LoginController loginCtrl = new LoginController(sistema, janelaLogin);
        loginCtrl.iniciarProcessoLogin(exit);
    }

    private void atualizarTabela() {
        view.getModeloTabela().setRowCount(0);

        try {
            List<Registrador> lista = sistema.getListaUsuarioAtual();

            for (Registrador r : lista) {
                view.getModeloTabela().addRow(new Object[]{
                        r.getAplicativo(),
                        r.getUsuario(),
                        r.getEmail(),
                        r.getSenha()
                });
            }
        } catch (Exception e) {
        }
    }

    private void adicionarRegistro() {
        JTextField txtApp = new JTextField();
        JTextField txtUser = new JTextField();
        JTextField txtEmail = new JTextField();
        JPasswordField txtPass = new JPasswordField();

        Object[] message = {
                "Nome do Aplicativo/Site:", txtApp,
                "Usuário/Login:", txtUser,
                "Email:", txtEmail,
                "Senha:", txtPass
        };

        int option = JOptionPane.showConfirmDialog(view, message, "Novo Registro", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String app = txtApp.getText();
            String user = txtUser.getText();
            String email = txtEmail.getText();
            String pass = new String(txtPass.getPassword());

            if (!app.isEmpty() && !user.isEmpty() && !pass.isEmpty()) {
                sistema.registrar(app, user, email, pass);
                atualizarTabela();
            } else {
                JOptionPane.showMessageDialog(view, "Preencha pelo menos App, Usuário e Senha!");
            }
        }
    }

    private void editarRegistro() {
        int linhaSelecionada = view.getTabela().getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(view, "Selecione uma linha para editar!");
            return;
        }

        List<Registrador> lista = sistema.getListaUsuarioAtual();
        Registrador regParaEditar = lista.get(linhaSelecionada);

        JTextField txtApp = new JTextField(regParaEditar.getAplicativo());
        JTextField txtUser = new JTextField(regParaEditar.getUsuario());
        JTextField txtEmail = new JTextField(regParaEditar.getEmail());
        JTextField txtPass = new JTextField(regParaEditar.getSenha());

        Object[] message = {
                "Editar Aplicativo:", txtApp,
                "Editar Usuário:", txtUser,
                "Editar Email:", txtEmail,
                "Editar Senha:", txtPass
        };

        int option = JOptionPane.showConfirmDialog(view, message, "Editar Registro", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {

            if (!txtUser.getText().equals(regParaEditar.getUsuario()))
                sistema.replaceUser(regParaEditar, txtUser.getText());

            if (!txtEmail.getText().equals(regParaEditar.getEmail()))
                sistema.replaceEmail(regParaEditar, txtEmail.getText());

            if (!txtPass.getText().equals(regParaEditar.getSenha()))
                sistema.replacePassword(regParaEditar, txtPass.getText());


            atualizarTabela();
        }
    }

    private void excluirRegistro() {
        int linhaSelecionada = view.getTabela().getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(view, "Selecione um registro para excluir.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Tem certeza que deseja apagar este registro?", "Excluir", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<Registrador> lista = sistema.getListaUsuarioAtual();
            Registrador r = lista.get(linhaSelecionada);

            sistema.deleteDado(r);
            atualizarTabela();
        }
    }

    private void alterarSenhaLogin() {
        JPasswordField txtSenhaAtual = new JPasswordField();
        int check = JOptionPane.showConfirmDialog(view, new Object[]{"Digite sua senha ATUAL para continuar:", txtSenhaAtual}, "Segurança", JOptionPane.OK_CANCEL_OPTION);

        if (check != JOptionPane.OK_OPTION) return; // Cancelou

        String senhaAtual = new String(txtSenhaAtual.getPassword());
        if (!sistema.verificarSenhaAtual(senhaAtual)) {
            JOptionPane.showMessageDialog(view, "Senha atual incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPasswordField txtNovaSenha = new JPasswordField();
        JPasswordField txtConfirmaSenha = new JPasswordField();

        Object[] mensagem = {
                "Nova Senha Mestra:", txtNovaSenha,
                "Confirme a Nova Senha:", txtConfirmaSenha
        };

        int option = JOptionPane.showConfirmDialog(view, mensagem, "Alterar Senha Mestra", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String novaSenha = new String(txtNovaSenha.getPassword());
            String confirmaSenha = new String(txtConfirmaSenha.getPassword());
            if (novaSenha.isEmpty()) {
                JOptionPane.showMessageDialog(view, "A senha não pode ser vazia.");
                return;
            }

            if (!novaSenha.equals(confirmaSenha)) {
                JOptionPane.showMessageDialog(view, "As senhas não coincidem!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            sistema.alterarSenhaMestra(novaSenha);
            JOptionPane.showMessageDialog(view, "Senha Mestra alterada com sucesso!\nPor favor, faça login novamente.");
            trocarUsuario(true);
        }
    }

    private void sair() {
        sistema.logout();
    }
}