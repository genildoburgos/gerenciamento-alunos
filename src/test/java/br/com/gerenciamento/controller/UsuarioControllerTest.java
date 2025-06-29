package br.com.gerenciamento.controller;

import br.com.gerenciamento.model.Usuario;
import br.com.gerenciamento.repository.UsuarioRepository;
import br.com.gerenciamento.service.ServiceUsuario;
import br.com.gerenciamento.util.Util;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ServiceUsuario usuarioService;

    @Before
    public void limparUsuarios() {
        usuarioRepo.deleteAll();
    }

    @Test
    public void deveAcessarTelaDeLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/login"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    public void deveAcessarTelaDeCadastro() throws Exception {
        mockMvc.perform(get("/cadastro"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/cadastro"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    public void deveSalvarNovoUsuarioComSucesso() throws Exception {
        mockMvc.perform(post("/salvarUsuario")
                        .param("user", "joaoteste")
                        .param("senha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertNotNull(usuarioRepo.buscarLogin("joaoteste", Util.md5("senha123")));
    }

    @Test
    public void deveRetornarCadastroParaUsuarioInexistenteNoLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .param("user", "desconhecido")
                        .param("senha", "senha"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/cadastro"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attributeDoesNotExist("msg"));
    }

    @Test
    public void deveLogarComUsuarioExistente() throws Exception {
        Usuario admin = new Usuario();
        admin.setUser("usuarioAdmin");
        admin.setSenha(Util.md5("admin123"));
        usuarioRepo.save(admin);

        mockMvc.perform(post("/login")
                        .param("user", "usuarioAdmin")
                        .param("senha", "admin123"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/index"))
                .andExpect(request().sessionAttribute("usuarioLogado", Matchers.notNullValue()));
    }

    @Test
    public void deveEncerrarSessaoComLogout() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUser("logoutUser");
        usuario.setId(99L);

        mockMvc.perform(post("/logout")
                        .sessionAttr("usuarioLogado", usuario))
                .andExpect(status().isOk())
                .andExpect(view().name("login/login"))
                .andExpect(request().sessionAttribute("usuarioLogado", Matchers.nullValue()));
    }
}
