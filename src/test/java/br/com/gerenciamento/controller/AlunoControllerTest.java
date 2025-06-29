package br.com.gerenciamento.controller;

import br.com.gerenciamento.enums.Curso;
import br.com.gerenciamento.enums.Status;
import br.com.gerenciamento.enums.Turno;
import br.com.gerenciamento.model.Aluno;
import br.com.gerenciamento.repository.AlunoRepository;
import br.com.gerenciamento.service.ServiceAluno;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AlunoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ServiceAluno alunoService;

    @Autowired
    private AlunoRepository repoAluno;

    @Before
    public void inicializarCenario() {
        repoAluno.deleteAll();
    }

    @Test
    public void deveRedirecionarAoCadastrarAlunoComSucesso() throws Exception {
        mvc.perform(post("/InsertAlunos")
                        .param("nome", "Lucas Silva")
                        .param("matricula", "998877")
                        .param("curso", "INFORMATICA")
                        .param("turno", "MATUTINO")
                        .param("status", "ATIVO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/alunos-adicionados"));
    }

    @Test
    public void deveManterFormularioComErroAoFaltarDados() throws Exception {
        mvc.perform(post("/InsertAlunos")
                        .param("nome", "Mariana Costa")
                        .param("curso", "INFORMATICA")
                        .param("turno", "NOTURNO")
                        .param("status", "INATIVO")) // matrícula ausente
                .andExpect(status().isOk())
                .andExpect(view().name("Aluno/formAluno"))
                .andExpect(model().attributeExists("aluno"))
                .andExpect(model().hasErrors());
    }

    @Test
    public void deveExibirResultadoBuscaSeAlunoExistir() throws Exception {
        Aluno mockAluno = new Aluno();
        mockAluno.setNome("Ana Paula");
        mockAluno.setCurso(Curso.INFORMATICA);
        mockAluno.setStatus(Status.ATIVO);
        mockAluno.setTurno(Turno.MATUTINO);
        mockAluno.setMatricula("20241234");
        alunoService.save(mockAluno);

        mvc.perform(post("/pesquisar-aluno")
                        .param("nome", "Ana Paula"))
                .andExpect(status().isOk())
                .andExpect(view().name("Aluno/pesquisa-resultado"));
    }

    @Test
    public void deveAtualizarAlunoExistenteAoEditar() throws Exception {
        // 1. Cria e salva um aluno no banco
        Aluno alunoOriginal = new Aluno();
        alunoOriginal.setNome("Pedro Henrique");
        alunoOriginal.setMatricula("100200");
        alunoOriginal.setCurso(Curso.INFORMATICA);
        alunoOriginal.setTurno(Turno.NOTURNO);
        alunoOriginal.setStatus(Status.ATIVO);
        alunoService.save(alunoOriginal); // salva e recupera com ID

        // 2. Simula a edição do aluno
        mvc.perform(post("/editar")
                        .param("id", alunoOriginal.getId().toString())
                        .param("nome", "Pedro H. Editado")
                        .param("matricula", "100200") // mesma matrícula
                        .param("curso", "INFORMATICA")
                        .param("turno", "NOTURNO")
                        .param("status", "INATIVO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/alunos-adicionados"));

        // 3. Verifica se as alterações foram persistidas
        Aluno alunoEditado = repoAluno.findById(alunoOriginal.getId()).orElseThrow();
        org.junit.Assert.assertEquals("Pedro H. Editado", alunoEditado.getNome());
        org.junit.Assert.assertEquals(Status.INATIVO, alunoEditado.getStatus());
    }
}
