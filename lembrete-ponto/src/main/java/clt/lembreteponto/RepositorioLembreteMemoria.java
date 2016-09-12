/**
 * 
 */
package clt.lembreteponto;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author andre
 *
 */
public class RepositorioLembreteMemoria implements IRepositorioLembretes {
	private Map<Long, Usuario> conversas = new TreeMap<Long, Usuario>();
	private Map<Long, String> comandos = new HashMap<Long, String>();
	private Map<Long, Date> inicios = new HashMap<Long, Date>();
	private Map<Long, Long> saldos = new HashMap<Long, Long>();
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

	public Map<Date, TipoAlerta> registrar(long chatId, Date ponto) {
		Map<Date, TipoAlerta> alertas = new HashMap<Date, TipoAlerta>();
		log.debug("Registrar: " + chatId);
		long saldo = 0;
		if (saldos.containsKey(chatId)) {
			saldo = saldos.get(chatId);
			log.debug("Saldo: " + saldo);
		}
		if (inicios.containsKey(chatId)) {
			Date inicio = inicios.remove(chatId);
			if (isMesmoDia(inicio, ponto)) {
				log.debug("Mesmo dia");
				saldo += ponto.getTime() - inicio.getTime();
				saldos.put(chatId, saldo);
			} else {
				log.debug("Dias diferentes");
				saldos.remove(chatId);
			}
			acrescentarAlertasEntrada(conversas.get(chatId).getRegime(), ponto,
					alertas, saldo);
		} else {
			inicios.put(chatId, ponto);
			acrescentarAlertasSaida(conversas.get(chatId).getRegime(), ponto,
					alertas, saldo);
		}

		return alertas;
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
}
