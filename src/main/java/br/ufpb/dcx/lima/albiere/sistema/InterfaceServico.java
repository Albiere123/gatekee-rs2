package br.ufpb.dcx.lima.albiere.sistema;

import br.ufpb.dcx.lima.albiere.exceptions.AplicativoInexistenteException;
import br.ufpb.dcx.lima.albiere.exceptions.UsuarioInexistenteException;
import java.util.List;

public interface InterfaceServico {

    void registrar(String aplicativo, String usuario, String email, String senha);

    List<Registrador> procuraAplicativo(String aplicativo) throws AplicativoInexistenteException;

    List<Registrador> procuraUsuario(String usuario) throws UsuarioInexistenteException;

    void replacePassword(Registrador r1, String senha);

    void replaceUser(Registrador r1, String usuario);

    void replaceEmail(Registrador r1, String email);

    void deleteDado(Registrador r1);

    void formatar();

}