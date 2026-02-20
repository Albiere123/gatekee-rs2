package br.ufpb.dcx.lima.albiere.sistema;

import br.ufpb.dcx.lima.albiere.database.ArquivoDB;
import br.ufpb.dcx.lima.albiere.database.LoginDB;
import br.ufpb.dcx.lima.albiere.exceptions.*;
import br.ufpb.dcx.lima.albiere.login.LoginSistema;
import br.ufpb.dcx.lima.albiere.login.VerificarLogin;
import br.ufpb.dcx.lima.albiere.verificarsenhas.GerarSenhaSegura;
import br.ufpb.dcx.lima.albiere.verificarsenhas.VerificadorSenha;

import javax.swing.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SistemaRegistrador implements InterfaceServico {

    private Map<String, List<Registrador>> bancoDeDadosVirtual;
    private String currentUser;
    private LoginDB logins;
    private ArquivoDB arquivoDB;

    public SistemaRegistrador() {
        this.bancoDeDadosVirtual = new HashMap<>();
        this.logins = new LoginDB("logins.yml");
        this.arquivoDB = new ArquivoDB("GateKeepers.yml");
    }

    public void salvarDados() {
        if (currentUser != null) {
            arquivoDB.salvarTudo(currentUser, bancoDeDadosVirtual.get(currentUser));
            System.out.println("Dados salvos com sucesso para: " + currentUser);
        }
    }

    public List<Registrador> getListaUsuarioAtual() {
        if (currentUser == null) return new ArrayList<>();
        if (!bancoDeDadosVirtual.containsKey(currentUser)) {
            bancoDeDadosVirtual.put(currentUser, new ArrayList<>());
        }
        return bancoDeDadosVirtual.get(currentUser);
    }

    public String getCurrentUser() {
        return currentUser;
    }
    @Override
    public void registrar(String aplicativo, String usuario, String email, String senha) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "Nenhum usuário logado!");
            return;
        }

        Registrador rg = new Registrador(aplicativo, usuario, email, senha);
        getListaUsuarioAtual().add(rg);
    }

    @Override
    public List<Registrador> procuraAplicativo(String aplicativo) throws AplicativoInexistenteException {
        List<Registrador> listaDoUsuario = getListaUsuarioAtual();
        List<Registrador> encontrados = new ArrayList<>();

        for (Registrador r : listaDoUsuario) {
            if (r.getAplicativo().equalsIgnoreCase(aplicativo)) {
                encontrados.add(r);
            }
        }
        if (encontrados.isEmpty()) throw new AplicativoInexistenteException("Aplicativo não registrado");
        return encontrados;
    }

    @Override
    public List<Registrador> procuraUsuario(String usuario) throws UsuarioInexistenteException {
        List<Registrador> listaDoUsuario = getListaUsuarioAtual();
        List<Registrador> encontrados = new ArrayList<>();

        for (Registrador r : listaDoUsuario) {
            if (r.getUsuario().equalsIgnoreCase(usuario)) {
                encontrados.add(r);
            }
        }
        if (encontrados.isEmpty()) throw new UsuarioInexistenteException("Usuário não registrado");
        return encontrados;
    }

    public void replacePassword(Registrador r1, String senha) {
        try {
            if (r1.getSenha().equals(senha)) {
                throw new DadoIgualException("Falha: senha igual");
            } else {
                r1.setSenha(senha);
                JOptionPane.showMessageDialog(null, "Senha alterada com sucesso!");
            }
        } catch (DadoIgualException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void replaceUser(Registrador r1, String usuario) {
        try {
            if (r1.getUsuario().equals(usuario)) {
                throw new DadoIgualException("Falha: usuário igual");
            } else {
                r1.setUsuario(usuario);
                JOptionPane.showMessageDialog(null, "Usuário alterado com sucesso!");
            }
        } catch (DadoIgualException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void replaceEmail(Registrador r1, String email) {
        try {
            if (r1.getEmail().equals(email)) {
                throw new DadoIgualException("Falha: email igual");
            } else {
                r1.setEmail(email);
                JOptionPane.showMessageDialog(null, "Email alterado com sucesso!");
            }
        } catch (DadoIgualException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void deleteDado(Registrador r1) {
        if (currentUser != null) {
            getListaUsuarioAtual().remove(r1);
        }
    }

    public void formatar() {
        if (currentUser != null) {
            getListaUsuarioAtual().clear();
        }
    }

    public String gerarSenhaForte() {
        return GerarSenhaSegura.gerarSenha(10);
    }

    public boolean verificarSenha(String senha) {
        return VerificadorSenha.senhaSegura(senha);
    }

    public boolean login(String user, String senha) throws SenhaIncorreta, LoginNaoEncontrado, NoSuchAlgorithmException {
        try {
            boolean deuCerto = VerificarLogin.verificarSenha(senha, logins.carregarSenha(user.toLowerCase()));
            if (deuCerto) {
                this.currentUser = user.toLowerCase();
                if (!bancoDeDadosVirtual.containsKey(currentUser)) {
                    bancoDeDadosVirtual.put(currentUser, arquivoDB.carregar(currentUser));
                }
                return true;
            }
        } catch (Exception e) {
            if (Objects.equals(e.getMessage(), "Usuário não encontrado!")) {
                JOptionPane.showMessageDialog(null, "Usuário Inexistente!", "Login - GateKeepers", 3);
            }
        }
        return false;
    }

    public boolean registrarLogin(String user, String senha) throws NoSuchAlgorithmException {
        String senhaParaSalvar = LoginSistema.criarHashComSalt(senha);
        String userLower = user.toLowerCase();
        try {
            if (logins.procurarUser(userLower)) return false;

            logins.salvarLogin(userLower, senhaParaSalvar);
            this.currentUser = userLower;
            bancoDeDadosVirtual.put(currentUser, new ArrayList<>());
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean procurarUsuarioLogin(String user) {
        return logins.procurarUser(user);
    }

    public void logout() {
        salvarDados();
        currentUser = null;
    }

    public boolean verificarSenhaAtual(String senhaDigitada) {
        try {
            String hashSalvo = logins.carregarSenha(currentUser);
            return VerificarLogin.verificarSenha(senhaDigitada, hashSalvo);
        } catch (Exception e) {
            return false;
        }
    }

    public void alterarSenhaMestra(String novaSenha) {
        try {
            String novoHash = LoginSistema.criarHashComSalt(novaSenha);
            logins.salvarLogin(currentUser, novoHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}