package br.ufpb.dcx.lima.albiere.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MainView extends JFrame {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnNovo, btnEditar, btnExcluir, btnAlterarSenhaLogin, btnSair;
    private JLabel lblUsuarioLogado;
    private JButton btnTrocarUsuario;

    public MainView() {
        setTitle("GateKeepers - Gerenciador de Senhas");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Painel Superior (Barra de Ferramentas) ---
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.setBackground(new Color(60, 63, 65)); // Cor escura estilo IDE

        btnNovo = criarBotao("Novo Registro");
        btnEditar = criarBotao("Editar Selecionado");
        btnExcluir = criarBotao("Excluir");
        btnAlterarSenhaLogin = criarBotao("Alterar Minha Senha Mestra");
        btnTrocarUsuario = criarBotao("Trocar Usuário");
        btnTrocarUsuario.setBackground(new Color(70, 130, 180)); // Azulzinho para diferenciar
        btnTrocarUsuario.setForeground(Color.WHITE);
        btnSair = criarBotao("Sair");
        btnSair.setBackground(new Color(190, 50, 50)); // Vermelho para sair

        painelTopo.add(btnNovo);
        painelTopo.add(btnEditar);
        painelTopo.add(btnExcluir);
        painelTopo.add(Box.createHorizontalStrut(20)); // Espaço
        painelTopo.add(btnAlterarSenhaLogin);
        painelTopo.add(btnTrocarUsuario);
        painelTopo.add(btnSair);

        add(painelTopo, BorderLayout.NORTH);

        // --- Tabela Central ---
        // Colunas: Aplicativo | Usuário | Email | Senha
        String[] colunas = {"Aplicativo", "Usuário", "Email", "Senha"};

        // Modelo que impede edição direta na célula (para obrigar usar o botão Editar)
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(25);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Renderizador para esconder a senha com ******
        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                super.setValue("******"); // Mascara a senha visualmente
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        // --- Rodapé ---
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblUsuarioLogado = new JLabel("Usuário: Desconhecido");
        rodape.add(lblUsuarioLogado);
        add(rodape, BorderLayout.SOUTH);
    }

    private JButton criarBotao(String texto) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return btn;
    }

    // Getters para o Controller usar
    public JTable getTabela() { return tabela; }
    public DefaultTableModel getModeloTabela() { return modeloTabela; }
    public JButton getBtnNovo() { return btnNovo; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnExcluir() { return btnExcluir; }
    public JButton getBtnAlterarSenhaLogin() { return btnAlterarSenhaLogin; }
    public JButton getBtnSair() { return btnSair; }
    public JLabel getLblUsuarioLogado() { return lblUsuarioLogado; }
    public JButton getBtnTrocarUsuario() { return btnTrocarUsuario; }
    public JFrame getFrame() {return this;}
}