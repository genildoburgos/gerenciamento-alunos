package br.com.gerenciamento.repository;

import br.com.gerenciamento.model.Usuario;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    public void deveLancarExcecaoParaUsuarioComNomeMuitoCurto() {
        Usuario usuario = new Usuario();
        usuario.setUser("a1"); // menos de 3 caracteres
        usuario.setSenha("123");
        usuario.setEmail("alex@gmail.com");

        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> {
            usuarioRepository.save(usuario);
        });

        assertTrue(
                ex.getConstraintViolations().stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("user"))
        );
    }

    @Test
    public void deveEncontrarUsuarioPorEmail() {
        Usuario usuario = new Usuario();
        usuario.setUser("Epitacio");
        usuario.setSenha("123");
        usuario.setEmail("taco@gmail.com");
        usuarioRepository.save(usuario);

        Usuario usuarioEncontrado = usuarioRepository.findByEmail("taco@gmail.com");

        assertNotNull(usuarioEncontrado);
        assertEquals(usuario.getUser(), usuarioEncontrado.getUser());
    }

    @Test
    public void deveBuscarUsuarioPorLogin() {
        Usuario usuario = new Usuario();
        usuario.setUser("User");
        usuario.setSenha("senha");
        usuario.setEmail("usuario@gmail.com");
        usuarioRepository.save(usuario);

        Usuario usuarioLogin = usuarioRepository.buscarLogin("User", "senha");

        assertNotNull(usuarioLogin);
        assertEquals(usuario.getUser(), usuarioLogin.getUser());
        assertEquals(usuario.getSenha(), usuarioLogin.getSenha());
    }

    @Test
    public void deveRetornarNuloQuandoUsuarioNaoExiste() {
        Usuario usuario = usuarioRepository.buscarLogin("alex", "123");
        assertNull(usuario);
    }
}
