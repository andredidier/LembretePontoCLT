/**
 * 
 */
package clt.lembreteponto;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author andre
 *
 */
public class ProcessadorAlertas implements Runnable {

	private IProcessadorAlertasListener listener;
	private long chatId;

	public ProcessadorAlertas(IProcessadorAlertasListener listener, long chatId) {
		this.listener = listener;
		this.chatId = chatId;
	}

	public void run() {
		try {
			if (listener == null) {
				return;
			}
			while (true) {
				// FIXME criar semáforo aqui (ver blocos sync do BOT).
				Alerta proximoAlerta = listener.obterProximoAlerta(chatId);
				if (proximoAlerta == null) {
					listener.cancelarAlertas(chatId);
					break;
				}
				// FIXME FIM do semáforo
				long timeout = proximoAlerta.getHorario().getTime()
						- new Date().getTime();
				log.debug("Tempo do timeout: " + timeout);
				Thread.sleep(timeout);
				listener.alertar(chatId, proximoAlerta.getTipo());

			}
		} catch (InterruptedException e) {
			log.error("Interrompido", e);
		}
	}

	private static Log log = LogFactory.getLog(ProcessadorAlertas.class);

}
