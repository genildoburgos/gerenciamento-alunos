package br.com.gerenciamento.service;

import br.com.gerenciamento.enums.Curso;
import br.com.gerenciamento.enums.Status;
import br.com.gerenciamento.enums.Turno;
import br.com.gerenciamento.model.Aluno;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionSystemException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AlunoServiceTest {

    @Autowired
    private ServiceAluno alunoService;

    @Test
    public void devePersistirAlunoComDadosValidos() {
        Aluno novoAluno = new Aluno();
        novoAluno.setNome("Carolina");
        novoAluno.setTurno(Turno.MATUTINO);
        novoAluno.setCurso(Curso.CONTABILIDADE);
        novoAluno.setStatus(Status.ATIVO);
        novoAluno.setMatricula("MAT999");

        alunoService.save(novoAluno);

        assertNotNull(novoAluno.getId());
        Aluno recuperado = alunoService.getById(novoAluno.getId());
        assertEquals("Carolina", recuperado.getNome());
    }

    @Test
    public void deveLancarExcecaoQuandoSalvarAlunoSemNome() {
        Aluno alunoInvalido = new Aluno();
        alunoInvalido.setTurno(Turno.NOTURNO);
        alunoInvalido.setCurso(Curso.ADMINISTRACAO);
        alunoInvalido.setStatus(Status.ATIVO);
        alunoInvalido.setMatricula("MAT1010");

        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> {
            alunoService.save(alunoInvalido);
        });

        assertTrue(ex.getConstraintViolations()
                .stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    public void deveAtualizarStatusDoAluno() {
        Aluno aluno = new Aluno();
        aluno.setNome("Mariana");
        aluno.setTurno(Turno.MATUTINO);
        aluno.setCurso(Curso.DIREITO);
        aluno.setStatus(Status.ATIVO);
        aluno.setMatricula("MAT2020");
        alunoService.save(aluno);

        Long id = aluno.getId();
        assertNotNull(id);

        Aluno salvo = alunoService.getById(id);
        assertEquals(Status.ATIVO, salvo.getStatus());

        salvo.setStatus(Status.INATIVO);
        alunoService.save(salvo);

        Aluno atualizado = alunoService.getById(id);
        assertEquals(Status.INATIVO, atualizado.getStatus());
    }

    @Test
    public void deveExcluirAlunoPorId() {
        Aluno aluno = new Aluno();
        aluno.setNome("Felipe");
        aluno.setTurno(Turno.MATUTINO);
        aluno.setCurso(Curso.DIREITO);
        aluno.setStatus(Status.ATIVO);
        aluno.setMatricula("MAT3030");
        alunoService.save(aluno);

        Long id = aluno.getId();
        assertNotNull(id);

        alunoService.deleteById(id);

        assertThrows(Exception.class, () -> alunoService.getById(id));
    }

    @Test
    public void deveLancarExcecaoParaNomeMaiorQuePermitido() {
        Aluno aluno = new Aluno();
        aluno.setNome("Nome Extremamente Grande Para Que Seja Rejeitado Pelo Sistema de Validação e Gerar Erro Porque Passa Do Limite De 35 Caracteres");
        aluno.setTurno(Turno.MATUTINO);
        aluno.setCurso(Curso.DIREITO);
        aluno.setStatus(Status.ATIVO);
        aluno.setMatricula("MAT4040");

        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> {
            alunoService.save(aluno);
        });

        assertTrue(ex.getConstraintViolations()
                .stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    public void deveFalharAoBuscarAlunoInexistente() {
        assertThrows(Exception.class, () -> alunoService.getById(9999L));
    }
}
