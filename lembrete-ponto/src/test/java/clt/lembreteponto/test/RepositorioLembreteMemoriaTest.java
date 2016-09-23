/**
 * 
 */
package clt.lembreteponto.test;

import static org.junit.Assert.assertSame;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import clt.lembreteponto.Alerta;
import clt.lembreteponto.IRepositorioLembretes;
import clt.lembreteponto.RepositorioLembreteMemoria;
import clt.lembreteponto.TipoAlerta;

/**
 * @author andre
 *
 */
public class RepositorioLembreteMemoriaTest {
	private IRepositorioLembretes repositorioLembretes;

	@Before
	public void before() {
		repositorioLembretes = new RepositorioLembreteMemoria();
	}

	private Date criarData(int ano, int mes, int dia, int hora, int min) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, ano);
		c.set(Calendar.MONTH, mes);
		c.set(Calendar.DAY_OF_MONTH, dia);
		c.set(Calendar.HOUR_OF_DAY, hora);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	@Test
	public void testAlertasSaida6h() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier", "andredidier");
		repositorioLembretes.registrarRegime(1, 6);
		Date entrada1 = criarData(2016, 9, 12, 7, 0);

		repositorioLembretes.registrar(1, entrada1);

		Date saida1 = criarData(2016, 9, 12, 9, 0);
		Alerta alerta1 = repositorioLembretes.proximoAlerta(1, criarData(2016, 9, 12, 8, 50));
		assertSame(saida1, alerta1.getHorario());
		assertSame(TipoAlerta.SaidaMinima, alerta1.getTipo());

		Date saida2 = criarData(2016, 9, 12, 10, 55);
		Alerta alerta2 = repositorioLembretes.proximoAlerta(1, criarData(2016, 9, 12, 10, 30));
		assertSame(saida2, alerta2.getHorario());
		assertSame(TipoAlerta.SaidaMaxima, alerta2.getTipo());
	}

	@Test
	public void testAlertasSaidaFinal6hPrimeiro2h() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier", "andredidier");
		repositorioLembretes.registrarRegime(1, 6);
		Date entrada = criarData(2016, 9, 12, 7, 0);
		Date saidaIntervalo = criarData(2016, 9, 12, 9, 0);
		Date entradaIntervao = criarData(2016, 9, 12, 9, 15);
		repositorioLembretes.registrar(1, entrada);
		repositorioLembretes.registrar(1, saidaIntervalo);

		repositorioLembretes.registrar(1, entradaIntervao);

		Date saida1 = criarData(2016, 9, 12, 13, 10);
		Alerta alerta1 = repositorioLembretes.proximoAlerta(1, criarData(2016, 9, 12, 13, 00));
		assertSame(saida1, alerta1.getHorario());
		assertSame(TipoAlerta.SaidaMaxima, alerta1.getTipo());
	}

	@Test
	public void testAlertasSaidaFinal6hPrimeiro2h30min() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier", "andredidier");
		repositorioLembretes.registrarRegime(1, 6);
		Date entrada = criarData(2016, 9, 12, 7, 0);
		Date saidaIntervalo = criarData(2016, 9, 12, 9, 30);
		Date entradaIntervao = criarData(2016, 9, 12, 9, 45);
		repositorioLembretes.registrar(1, entrada);
		repositorioLembretes.registrar(1, saidaIntervalo);

		repositorioLembretes.registrar(1, entradaIntervao);

		Date saida2 = criarData(2016, 9, 12, 13, 10);
		Alerta alerta2 = repositorioLembretes.proximoAlerta(1, criarData(2016, 9, 12, 13, 00));
		assertSame(saida2, alerta2.getHorario());
		assertSame(TipoAlerta.SaidaHorarioCompleto, alerta2.getTipo());

		Date saida1 = criarData(2016, 9, 12, 13, 40);
		Alerta alerta1 = repositorioLembretes.proximoAlerta(1, criarData(2016, 9, 12, 13, 20));
		assertSame(saida1, alerta1.getHorario());
		assertSame(TipoAlerta.SaidaMaxima, alerta1.getTipo());

	}

	@Test
	public void testAlertasSaidaFinal6hPrimeiro4h() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier", "andredidier");
		repositorioLembretes.registrarRegime(1, 6);
		Date entrada = criarData(2016, 9, 12, 7, 0);
		Date saidaIntervalo = criarData(2016, 9, 12, 11, 00);
		Date entradaIntervao = criarData(2016, 9, 12, 12, 00);
		repositorioLembretes.registrar(1, entrada);
		repositorioLembretes.registrar(1, saidaIntervalo);

		repositorioLembretes.registrar(1, entradaIntervao);

		Date saida1 = criarData(2016, 9, 12, 15, 55);
		Alerta alerta1 = repositorioLembretes.proximoAlerta(1, criarData(2016, 9, 12, 15, 00));
		assertSame(saida1, alerta1.getHorario());
		assertSame(TipoAlerta.SaidaMaxima, alerta1.getTipo());

	}

	@Test
	public void testAlertasSaida8h() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier", "andredidier");
		repositorioLembretes.registrarRegime(1, 8);
		Date entrada1 = criarData(2016, 9, 12, 7, 0);

		repositorioLembretes.registrar(1, entrada1);

		Date saida1 = criarData(2016, 9, 12, 10, 0);
		Alerta alerta1 = repositorioLembretes.proximoAlerta(1, criarData(2016, 9, 12, 9, 50));
		assertSame(saida1, alerta1.getHorario());
		assertSame(TipoAlerta.SaidaMinima, alerta1.getTipo());

		Date saida2 = criarData(2016, 9, 12, 11, 55);
		Alerta alerta2 = repositorioLembretes.proximoAlerta(1, criarData(2016, 9, 12, 11, 50));
		assertSame(saida2, alerta2.getHorario());
		assertSame(TipoAlerta.SaidaMaxima, alerta2.getTipo());

	}

	@Test
	public void testAlertasSaidaFinal8hPrimeiro3h() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier", "andredidier");
		repositorioLembretes.registrarRegime(1, 8);
		Date entrada = criarData(2016, 9, 12, 7, 0);
		Date saidaIntervalo = criarData(2016, 9, 12, 10, 0);
		Date entradaIntervao = criarData(2016, 9, 12, 11, 00);
		repositorioLembretes.registrar(1, entrada);
		repositorioLembretes.registrar(1, saidaIntervalo);
		
		repositorioLembretes.registrar(1, entradaIntervao);
		
		Date saida1 = criarData(2016, 9, 12, 15, 55);
		Alerta alerta1 = repositorioLembretes.proximoAlerta(1, criarData(2016, 9, 12, 15, 50));
		assertSame(saida1, alerta1.getHorario());
		assertSame(TipoAlerta.SaidaMaxima, alerta1.getTipo());

	}

	@Test
	public void testAlertasSaidaFinal8hPrimeiro3h30min() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier", "andredidier");
		repositorioLembretes.registrarRegime(1, 8);
		Date entrada = criarData(2016, 9, 12, 7, 0);
		Date saidaIntervalo = criarData(2016, 9, 12, 10, 30);
		Date entradaIntervao = criarData(2016, 9, 12, 11, 30);
		repositorioLembretes.registrar(1, entrada);
		repositorioLembretes.registrar(1, saidaIntervalo);
		
		repositorioLembretes.registrar(1, entradaIntervao);
		
		Date saida2 = criarData(2016, 9, 12, 15, 55);
		Alerta alerta2 = repositorioLembretes.proximoAlerta(1, criarData(2016, 9, 12, 15, 50));
		assertSame(saida2, alerta2.getHorario());
		assertSame(TipoAlerta.SaidaHorarioCompleto, alerta2.getTipo());

		Date saida1 = criarData(2016, 9, 12, 16, 25);
		Alerta alerta1 = repositorioLembretes.proximoAlerta(1, criarData(2016, 9, 12, 16, 20));
		assertSame(saida1, alerta1.getHorario());
		assertSame(TipoAlerta.SaidaMaxima, alerta1.getTipo());

	}

	@Test
	public void testAlertasSaidaFinal8hPrimeiro5h() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier", "andredidier");
		repositorioLembretes.registrarRegime(1, 8);
		Date entrada = criarData(2016, 9, 12, 7, 0);
		Date saidaIntervalo = criarData(2016, 9, 12, 12, 00);
		Date entradaIntervao = criarData(2016, 9, 12, 13, 00);
		repositorioLembretes.registrar(1, entrada);
		repositorioLembretes.registrar(1, saidaIntervalo);
		
		repositorioLembretes.registrar(1, entradaIntervao);
		
		Date saida1 = criarData(2016, 9, 12, 15, 55);
		Alerta alerta1 = repositorioLembretes.proximoAlerta(1, criarData(2016, 9, 12, 15, 50));
		assertSame(saida1, alerta1.getHorario());
		assertSame(TipoAlerta.SaidaHorarioCompleto, alerta1.getTipo());

		Date saida2 = criarData(2016, 9, 12, 17, 55);
		Alerta alerta2 = repositorioLembretes.proximoAlerta(1, criarData(2016, 9, 12, 17, 50));
		assertSame(saida2, alerta2.getHorario());
		assertSame(TipoAlerta.SaidaMaxima, alerta2.getTipo());
	}
}
