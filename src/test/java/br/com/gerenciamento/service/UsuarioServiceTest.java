package br.com.gerenciamento.service;

import br.com.gerenciamento.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    private ServiceUsuario usuarioService;

    @Test
    public void deveLancarExcecaoAoSalvarUsuarioComEmailDuplicado() throws Exception {
        Usuario primeiro = new Usuario();
        primeiro.setUser("Alberto");
        primeiro.setSenha("senha123");
        primeiro.setEmail("emailDuplicado@test.com");

        usuarioService.salvarUsuario(primeiro);

        Usuario duplicado = new Usuario();
        duplicado.setUser("Carlos");
        duplicado.setSenha("outrasenha");
        duplicado.setEmail("emailDuplicado@test.com");

        Exception ex = assertThrows(Exception.class, () -> {
            usuarioService.salvarUsuario(duplicado);
        });

        assertTrue(ex.getMessage().contains("Este email já esta cadastrado"));
    }

    @Test
    public void deveLancarExcecaoAoSalvarUsuarioSemSenha() {
        Usuario usuario = new Usuario();
        usuario.setUser("Roberto");
        usuario.setEmail("roberto@test.com");

        assertThrows(Exception.class, () -> {
            usuarioService.salvarUsuario(usuario);
        });
    }

    @Test
    public void deveRetornarNuloAoLogarUsuarioInexistente() {
        Usuario resultado = usuarioService.loginUser("naoExiste", "senhaIncorreta");
        assertNull(resultado);
    }

    @Test
    public void deveSalvarUsuarioValidoSemErros() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUser("Fernanda");
        usuario.setSenha("segura123");
        usuario.setEmail("fernanda@test.com");

        assertDoesNotThrow(() -> {
            usuarioService.salvarUsuario(usuario);
        });
    }
}
