/**
 * 
 */
package clt.lembreteponto;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author andre
 *
 */
public class ProcessadorAlertas implements Runnable {

	private Map<Date, TipoAlerta> alertas;
	private IProcessadorAlertasListener listener;
	private TipoAlerta tipoAlerta;
	private long chatId;

	public void cancelar() {
		alertas.clear();
	}

	public ProcessadorAlertas(IProcessadorAlertasListener listener,
			long chatId, Map<Date, TipoAlerta> alertas) {
		this.listener = listener;
		this.alertas = alertas;
		this.chatId = chatId;
	}

	public void run() {
		try {
			if (listener == null) {
				return;
			}
			boolean done = false;
			while (!done) {
				Long timeout = getNextTimeOut();
				if (timeout == null) {
					done = true;
				} else {
					log.debug("Tempo do timeout: " + timeout);
					Thread.sleep(timeout);
					listener.alertar(chatId, tipoAlerta);
				}
			}
		} catch (InterruptedException e) {
			log.error("Interrompido", e);
		}
	}

	private Long getNextTimeOut() {
		Date min = null;
		for (Date d : alertas.keySet()) {
			if (min == null || d.compareTo(min) < 0) {
				min = d;
			}
		}
		log.debug("Alerta: " + min);
		Date agora = new Date();
		if (min != null) {
			tipoAlerta = alertas.remove(min);
			if (agora.compareTo(min) > 0) {
				return getNextTimeOut();
			}
			Long timeout = min.getTime() - agora.getTime();
			listener.informarProximoAlerta(chatId, min);
			return timeout;
		}
		return null;
	}

	private static Log log = LogFactory.getLog(ProcessadorAlertas.class);

}
