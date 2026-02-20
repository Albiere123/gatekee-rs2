package br.ufpb.dcx.lima.albiere.database;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LoginDB {

    private final String nomeArquivo;
    private Map<String, String> cacheLogins;

    public LoginDB(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
        this.cacheLogins = carregarArquivoCompleto();
    }

    public void recarregarCache() {
        this.cacheLogins = carregarArquivoCompleto();
    }

    public void salvarLogin(String user, String senhaHash) {
        this.cacheLogins.put(user.toLowerCase().trim(), senhaHash);
        salvarArquivoCompleto(this.cacheLogins);
    }

    public String carregarSenha(String user) {
        String userTratado = user.toLowerCase().trim();
        String senha = this.cacheLogins.get(userTratado);

        if (senha == null) {
            System.out.println("[DEBUG LoginDB] Senha não encontrada para: '" + userTratado + "'");
            System.out.println("[DEBUG LoginDB] Usuários disponíveis no Map: " + this.cacheLogins.keySet());
        }

        return senha;
    }

    public boolean procurarUser(String user) {
        return cacheLogins.containsKey(user.toLowerCase().trim());
    }

    private Map<String, String> carregarArquivoCompleto() {
        Yaml yaml = new Yaml();
        File arquivo = new File(nomeArquivo);
        Map<String, String> mapaSeguro = new LinkedHashMap<>();

        System.out.println("[DEBUG LoginDB] Lendo arquivo de: " + arquivo.getAbsolutePath());

        if (!arquivo.exists()) {
            System.out.println("[DEBUG LoginDB] Arquivo não existe. Criando novo mapa vazio.");
            return mapaSeguro;
        }

        try (FileReader reader = new FileReader(arquivo)) {

            Map<String, Object> dadosBrutos = yaml.load(reader);

            if (dadosBrutos == null) return mapaSeguro;

            for (Map.Entry<String, Object> entry : dadosBrutos.entrySet()) {
                String chave = String.valueOf(entry.getKey()).toLowerCase().trim();
                String valor = String.valueOf(entry.getValue());
                mapaSeguro.put(chave, valor);
            }

            System.out.println("[DEBUG LoginDB] Dados carregados com sucesso: " + mapaSeguro.keySet());
            return mapaSeguro;

        } catch (IOException e) {
            System.err.println("[ERRO LoginDB] Falha ao ler arquivo: " + e.getMessage());
            return mapaSeguro;
        } catch (Exception e) {
            System.err.println("[ERRO LoginDB] Erro genérico ao processar YAML: " + e.getMessage());
            return mapaSeguro;
        }
    }

    private void salvarArquivoCompleto(Map<String, String> dados) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        File arquivo = new File(nomeArquivo);
        if (arquivo.getParentFile() != null) {
            arquivo.getParentFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(arquivo)) {
            yaml.dump(dados, writer);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar o arquivo de logins: " + e.getMessage(), e);
        }
    }
}