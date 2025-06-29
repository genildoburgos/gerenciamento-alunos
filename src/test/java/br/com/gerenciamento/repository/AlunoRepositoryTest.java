package br.com.gerenciamento.repository;

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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AlunoRepositoryTest {

    @Autowired
    private AlunoRepository alunoRepository;

    @Test
    public void deveRetornarQuantidadeDeAlunosAtivos() {
        Aluno aluno1 = new Aluno();
        aluno1.setNome("Jose Mateus");
        aluno1.setTurno(Turno.NOTURNO);
        aluno1.setCurso(Curso.ADMINISTRACAO);
        aluno1.setStatus(Status.ATIVO);
        aluno1.setMatricula("123456");
        alunoRepository.save(aluno1);

        Aluno aluno2 = new Aluno();
        aluno2.setNome("Vinicius");
        aluno2.setTurno(Turno.MATUTINO);
        aluno2.setCurso(Curso.BIOMEDICINA);
        aluno2.setStatus(Status.ATIVO);
        aluno2.setMatricula("4444");
        alunoRepository.save(aluno2);

        assertEquals(2, alunoRepository.findByStatusAtivo().size());
    }

    @Test
    public void deveRetornarQuantidadeDeAlunosInativos() {
        Aluno alunoInativo = new Aluno();
        alunoInativo.setNome("Ribeiro");
        alunoInativo.setTurno(Turno.MATUTINO);
        alunoInativo.setCurso(Curso.DIREITO);
        alunoInativo.setStatus(Status.INATIVO);
        alunoInativo.setMatricula("11111");
        alunoRepository.save(alunoInativo);

        assertEquals(1, alunoRepository.findByStatusInativo().size());
    }

    @Test
    public void deveLancarExcecaoParaAlunoComNomeCurto() {
        Aluno aluno = new Aluno();
        aluno.setNome("Ana");
        aluno.setTurno(Turno.NOTURNO);
        aluno.setCurso(Curso.ADMINISTRACAO);
        aluno.setStatus(Status.ATIVO);
        aluno.setMatricula("123456");

        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> {
            alunoRepository.save(aluno);
        });

        assertTrue(
                ex.getConstraintViolations().stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("nome"))
        );
    }

    @Test
    public void deveEncontrarAlunosPorParteDoNome() {
        Aluno aluno1 = new Aluno();
        aluno1.setNome("Americo Luis");
        aluno1.setTurno(Turno.NOTURNO);
        aluno1.setCurso(Curso.INFORMATICA);
        aluno1.setStatus(Status.INATIVO);
        aluno1.setMatricula("091232");
        alunoRepository.save(aluno1);

        Aluno aluno2 = new Aluno();
        aluno2.setNome("Americo Jose");
        aluno2.setTurno(Turno.NOTURNO);
        aluno2.setCurso(Curso.INFORMATICA);
        aluno2.setStatus(Status.INATIVO);
        aluno2.setMatricula("091233");
        alunoRepository.save(aluno2);

        assertEquals(2, alunoRepository.findByNomeContainingIgnoreCase("americo").size());
    }

    @Test
    public void deveLancarExcecaoParaAlunoSemMatricula() {
        Aluno aluno = new Aluno();
        aluno.setNome("Jorge");
        aluno.setTurno(Turno.NOTURNO);
        aluno.setCurso(Curso.INFORMATICA);
        aluno.setStatus(Status.ATIVO);

        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> {
            alunoRepository.save(aluno);
        });

        assertTrue(
                ex.getConstraintViolations().stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("matricula"))
        );
    }
}
