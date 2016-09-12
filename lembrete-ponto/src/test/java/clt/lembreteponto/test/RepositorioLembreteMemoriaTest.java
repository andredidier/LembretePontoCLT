/**
 * 
 */
package clt.lembreteponto.test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

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
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier",
				"andredidier");
		repositorioLembretes.registrarRegime(1, 6);
		Date entrada1 = criarData(2016, 9, 12, 7, 0);
		Map<Date, TipoAlerta> alertas = repositorioLembretes.registrar(1,
				entrada1);
		Date saida1 = criarData(2016, 9, 12, 9, 0);
		Date saida2 = criarData(2016, 9, 12, 10, 55);
		assertSame(alertas.size(), 2);
		assertThat(alertas.keySet(), hasItems(saida1, saida2));
		assertSame(alertas.get(saida1), TipoAlerta.SaidaMinima);
		assertSame(alertas.get(saida2), TipoAlerta.SaidaMaxima);
	}

	@Test
	public void testAlertasSaidaFinal6hPrimeiro2h() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier",
				"andredidier");
		repositorioLembretes.registrarRegime(1, 6);
		Date entrada = criarData(2016, 9, 12, 7, 0);
		Date saidaIntervalo = criarData(2016, 9, 12, 9, 0);
		Date entradaIntervao = criarData(2016, 9, 12, 9, 15);
		repositorioLembretes.registrar(1, entrada);
		repositorioLembretes.registrar(1, saidaIntervalo);
		Map<Date, TipoAlerta> alertas = repositorioLembretes.registrar(1,
				entradaIntervao);
		Date saida1 = criarData(2016, 9, 12, 13, 10);
		assertSame(alertas.size(), 1);
		assertThat(alertas.keySet(), hasItems(saida1));
		assertSame(alertas.get(saida1), TipoAlerta.SaidaMaxima);
	}

	@Test
	public void testAlertasSaidaFinal6hPrimeiro2h30min() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier",
				"andredidier");
		repositorioLembretes.registrarRegime(1, 6);
		Date entrada = criarData(2016, 9, 12, 7, 0);
		Date saidaIntervalo = criarData(2016, 9, 12, 9, 30);
		Date entradaIntervao = criarData(2016, 9, 12, 9, 45);
		repositorioLembretes.registrar(1, entrada);
		repositorioLembretes.registrar(1, saidaIntervalo);
		Map<Date, TipoAlerta> alertas = repositorioLembretes.registrar(1,
				entradaIntervao);
		Date saida1 = criarData(2016, 9, 12, 13, 40);
		Date saida2 = criarData(2016, 9, 12, 13, 10);
		assertSame(alertas.size(), 2);
		assertThat(alertas.keySet(), hasItems(saida1, saida2));
		assertSame(alertas.get(saida1), TipoAlerta.SaidaMaxima);
		assertSame(alertas.get(saida2), TipoAlerta.SaidaHorarioCompleto);
	}

	@Test
	public void testAlertasSaidaFinal6hPrimeiro4h() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier",
				"andredidier");
		repositorioLembretes.registrarRegime(1, 6);
		Date entrada = criarData(2016, 9, 12, 7, 0);
		Date saidaIntervalo = criarData(2016, 9, 12, 11, 00);
		Date entradaIntervao = criarData(2016, 9, 12, 12, 00);
		repositorioLembretes.registrar(1, entrada);
		repositorioLembretes.registrar(1, saidaIntervalo);
		Map<Date, TipoAlerta> alertas = repositorioLembretes.registrar(1,
				entradaIntervao);
		Date saida1 = criarData(2016, 9, 12, 15, 55);
		Date saida2 = criarData(2016, 9, 12, 13, 55);
		assertSame(alertas.size(), 2);
		assertThat(alertas.keySet(), hasItems(saida1, saida2));
		assertSame(alertas.get(saida1), TipoAlerta.SaidaMaxima);
		assertSame(alertas.get(saida2), TipoAlerta.SaidaHorarioCompleto);
	}

	@Test
	public void testAlertasSaida8h() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier",
				"andredidier");
		repositorioLembretes.registrarRegime(1, 8);
		Date entrada1 = criarData(2016, 9, 12, 7, 0);
		Map<Date, TipoAlerta> alertas = repositorioLembretes.registrar(1,
				entrada1);
		Date saida1 = criarData(2016, 9, 12, 10, 0);
		Date saida2 = criarData(2016, 9, 12, 11, 55);
		assertSame(alertas.size(), 2);
		assertThat(alertas.keySet(), hasItems(saida1, saida2));
		assertSame(alertas.get(saida1), TipoAlerta.SaidaMinima);
		assertSame(alertas.get(saida2), TipoAlerta.SaidaMaxima);
	}

	@Test
	public void testAlertasSaidaFinal8hPrimeiro3h() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier",
				"andredidier");
		repositorioLembretes.registrarRegime(1, 8);
		Date entrada = criarData(2016, 9, 12, 7, 0);
		Date saidaIntervalo = criarData(2016, 9, 12, 10, 0);
		Date entradaIntervao = criarData(2016, 9, 12, 11, 00);
		repositorioLembretes.registrar(1, entrada);
		repositorioLembretes.registrar(1, saidaIntervalo);
		Map<Date, TipoAlerta> alertas = repositorioLembretes.registrar(1,
				entradaIntervao);
		Date saida1 = criarData(2016, 9, 12, 15, 55);
		assertSame(alertas.size(), 1);
		assertThat(alertas.keySet(), hasItems(saida1));
		assertSame(alertas.get(saida1), TipoAlerta.SaidaMaxima);
	}

	@Test
	public void testAlertasSaidaFinal8hPrimeiro3h30min() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier",
				"andredidier");
		repositorioLembretes.registrarRegime(1, 8);
		Date entrada = criarData(2016, 9, 12, 7, 0);
		Date saidaIntervalo = criarData(2016, 9, 12, 10, 30);
		Date entradaIntervao = criarData(2016, 9, 12, 11, 30);
		repositorioLembretes.registrar(1, entrada);
		repositorioLembretes.registrar(1, saidaIntervalo);
		Map<Date, TipoAlerta> alertas = repositorioLembretes.registrar(1,
				entradaIntervao);
		Date saida1 = criarData(2016, 9, 12, 16, 25);
		Date saida2 = criarData(2016, 9, 12, 15, 55);
		assertSame(alertas.size(), 2);
		assertThat(alertas.keySet(), hasItems(saida1, saida2));
		assertSame(alertas.get(saida1), TipoAlerta.SaidaMaxima);
		assertSame(alertas.get(saida2), TipoAlerta.SaidaHorarioCompleto);
	}

	@Test
	public void testAlertasSaidaFinal8hPrimeiro5h() {
		repositorioLembretes.iniciarConversa(1, "Andre", "Didier",
				"andredidier");
		repositorioLembretes.registrarRegime(1, 8);
		Date entrada = criarData(2016, 9, 12, 7, 0);
		Date saidaIntervalo = criarData(2016, 9, 12, 12, 00);
		Date entradaIntervao = criarData(2016, 9, 12, 13, 00);
		repositorioLembretes.registrar(1, entrada);
		repositorioLembretes.registrar(1, saidaIntervalo);
		Map<Date, TipoAlerta> alertas = repositorioLembretes.registrar(1,
				entradaIntervao);
		Date saida1 = criarData(2016, 9, 12, 15, 55);
		Date saida2 = criarData(2016, 9, 12, 17, 55);
		assertSame(alertas.size(), 2);
		assertThat(alertas.keySet(), hasItems(saida1, saida2));
		assertSame(alertas.get(saida1), TipoAlerta.SaidaHorarioCompleto);
		assertSame(alertas.get(saida2), TipoAlerta.SaidaMaxima);
	}
}
