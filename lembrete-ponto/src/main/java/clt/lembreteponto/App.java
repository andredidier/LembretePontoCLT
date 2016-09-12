package clt.lembreteponto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;

/**
 *
 */
public class App {
	private static Log log = LogFactory.getLog(App.class);

	public static void main(String[] args) {
		log.info("Iniciando App");
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		try {
			telegramBotsApi.registerBot(new LembretePontoBot(
					new RepositorioLembreteMemoria()));
			log.info("App iniciado.");
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
