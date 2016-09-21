/**
 * 
 */
package clt.lembreteponto;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author andre
 *
 */
public class RepositorioLembreteMemoria implements IRepositorioLembretes {
	private Map<Long, Usuario> conversas = new TreeMap<Long, Usuario>();
	private Map<Long, String> comandos = new HashMap<Long, String>();
	private Map<Long, Set<Date>> datas = new HashMap<Long, Set<Date>>();
	private static Log log = LogFactory
			.getLog(RepositorioLembreteMemoria.class);

	public boolean contemConversa(long id) {
		return conversas.containsKey(id);
	}

	public void iniciarConversa(long id, String firstName, String lastName,
			String userName) {
		Usuario usuario = new Usuario();
		usuario.setChatId(id);
		usuario.setPrimeiroNome(firstName);
		usuario.setSobrenome(lastName);
		usuario.setNomeUsuario(userName);
		conversas.put(id, usuario);
	}

	public void iniciarComando(long chatId, String comando) {
		comandos.put(chatId, comando);
	}

	public boolean possuiComandoIniciado(long chatId) {
		return comandos.containsKey(chatId);
	}

	public String cancelarComando(long chatId) {
		return comandos.remove(chatId);
	}

	public String getComandoIniciado(long chatId) {
		return comandos.get(chatId);
	}

	public void registrarRegime(long chatId, int regime) {
		if (conversas.containsKey(chatId)) {
			Usuario usuario = conversas.get(chatId);
			usuario.setRegime(regime);
		}
	}

	public Integer getRegime(long chatId) {
		if (conversas.containsKey(chatId)) {
			return conversas.get(chatId).getRegime();
		}
		return null;

	}

	public void concluirComando(long chatId) {
		comandos.remove(chatId);
	}

	public void registrar(long chatId, Date ponto) {
		log.debug("Registrar: " + chatId);
		Set<Date> registros;
		if (datas.containsKey(chatId)) {
			registros = datas.get(chatId);
		} else {
			registros = new TreeSet<Date>();
			datas.put(chatId, registros);
		}
		registros.add(ponto);
	}

	private boolean isMesmoDia(Date dia1, Date dia2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(dia1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(dia2);
		return c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
				&& c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
				&& c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
	}

	private void acrescentarAlertasEntrada(int regime, Date ponto,
			Map<Date, TipoAlerta> alertas, long saldo) {
	}

	private void acrescentarAlertasSaida(int regime, Date ponto,
			Map<Date, TipoAlerta> alertas, long saldo) {
		Calendar tPonto1 = Calendar.getInstance();
		tPonto1.setTime(ponto);
		Calendar tPonto2 = Calendar.getInstance();
		tPonto2.setTime(ponto);
		tPonto1.set(Calendar.SECOND, 0);
		tPonto2.set(Calendar.SECOND, 0);
		int saldoMaximo;
		int saidaMinima;
		int saidaMaxima;
		int saidaHorarioCompleto;
		if (regime == 6) {
			saldoMaximo = (int) (8 * 60 * 60 * 1000 - saldo);
			saidaMinima = (2 * 60 * 60 * 1000);
			saidaMaxima = 4 * 60 * 60 * 1000;
		} else if (regime == 8) {
			saldoMaximo = (int) (10 * 60 * 60 * 1000 - saldo);
			saidaMinima = (3 * 60 * 60 * 1000);
			saidaMaxima = 5 * 60 * 60 * 1000;
		} else {
			log.warn("Regime não suportado: " + regime);
			return;
		}
		saidaHorarioCompleto = (regime * 60 * 60 * 1000);
		log.debug("Saldo: " + saldo);
		if (saldo > 0) {
			tPonto1.add(Calendar.MILLISECOND, (int) (saidaHorarioCompleto
					- saldo - (5 * 60 * 1000)));
			tPonto2.add(Calendar.MILLISECOND,
					Math.min(saldoMaximo, saidaMaxima) - (5 * 60 * 1000));
			alertas.put(tPonto1.getTime(), TipoAlerta.SaidaHorarioCompleto);
			alertas.put(tPonto2.getTime(), TipoAlerta.SaidaMaxima);
		} else {
			tPonto1.add(Calendar.MILLISECOND,
					Math.min(saldoMaximo, saidaMinima));
			tPonto2.add(Calendar.MILLISECOND,
					Math.min(saldoMaximo, saidaMaxima) - (5 * 60 * 1000));
			alertas.put(tPonto1.getTime(), TipoAlerta.SaidaMinima);
			alertas.put(tPonto2.getTime(), TipoAlerta.SaidaMaxima);
		}
	}

	public Alerta proximoAlerta(long chatId, Date data) {
		// FIXME ESTÁ ERRADO!!!
		Set<Date> registros;
		if (!datas.containsKey(chatId)) {
			log.debug("Registros não encontrados para " + chatId);
			return null;
		}

		registros = datas.get(chatId);
		Date minima = null;
		boolean eSaida = registros.size() % 2 == 1;
		for (Date d : registros) {
			if (minima == null || (d.after(data) && d.before(minima))) {
				minima = d;
				log.debug("Nova data mínima: " + minima);
			}
		}
		if (minima == null) {
			log.debug("Data mínima nula");
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(minima);
		c.set(Calendar.SECOND, 0);
		if (eSaida) {
			c.add(Calendar.MINUTE, -5);
		}
		log.debug("Próximo alerta: " + c.getTime());
		Alerta a = new Alerta();
		a.setHorario(c.getTime());
		return a;
	}
}
