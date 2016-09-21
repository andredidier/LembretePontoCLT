/**
 * 
 */
package clt.lembreteponto;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardHide;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

/**
 * @author andre
 *
 */
public class LembretePontoBot extends TelegramLongPollingBot implements
		IProcessadorAlertasListener {
	private IRepositorioLembretes repositorio;
	private static Log log = LogFactory.getLog(LembretePontoBot.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("Language");
	private Map<String, Comando> comandos;
	private Map<Long, Thread> processadoresAlertas = new HashMap<Long, Thread>();

	private boolean i18nBoolean(String key, Object... params) {
		if (bundle.containsKey(key)) {
			String value = bundle.getString(key);
			return Boolean.parseBoolean(value);
		} else {
			return false;
		}
	}

	private Integer i18nInt(String key, Object... params) {
		if (bundle.containsKey(key)) {
			String value = bundle.getString(key);
			return Integer.parseInt(value);
		} else {
			return null;
		}
	}

	private String i18n(String key, Object... params) {
		if (bundle.containsKey(key)) {
			Object[] i18nParams = new Object[params.length];
			for (int i = 0; i < params.length; i++) {
				Object o = params[i];
				if (o instanceof String) {
					i18nParams[i] = i18n((String) o);
				} else {
					i18nParams[i] = o;
				}
			}
			return MessageFormat.format(bundle.getString(key), i18nParams);
		} else
			return key;
	}

	public LembretePontoBot(IRepositorioLembretes repositorio) {
		this.repositorio = repositorio;
		comandos = new HashMap<String, Comando>();
		comandos.put("definirregime", Comando.DefinirRegime);
		comandos.put("registrar", Comando.Registrar);
		comandos.put("regime", Comando.Regime);
	}

	public String getBotUsername() {
		return "LembretePontoCLT_bot";
	}

	public void onUpdateReceived(Update update) {
		if (update == null) {
			return;
		}
		try {
			handleMessage(update.getMessage());
		} catch (TelegramApiException e) {
			log.error("onUpdateReceived exception", e);
			sendErrorMessage(update, e);
		}
	}

	private void sendErrorMessage(Update update, TelegramApiException e) {
		if (update.getMessage() == null
				|| update.getMessage().getChatId() == null) {
			return;
		}
		// TODO Auto-generated method stub
	}

	private void handleMessage(Message message) throws TelegramApiException {
		if (message == null) {
			return;
		}
		registerChat(message.getChat());
		if (message.isCommand()) {
			log.debug("É comando");
			processarComando(message);
		} else if (message.isUserMessage()) {
			log.debug("É mensagem");
			processMessage(message);
		}
	}

	private void processMessage(Message message) throws TelegramApiException {
		if (message.getChatId() == null) {
			return;
		}
		if (repositorio.possuiComandoIniciado(message.getChatId())) {
			log.debug("Possui comando iniciado");
			processarComandoIniciado(message);
		} else {
			enviarMensagemPadrao(message.getChatId(), null);
		}
	}

	private void processarComandoIniciado(Message message)
			throws TelegramApiException {
		String textoComando = repositorio.getComandoIniciado(message
				.getChatId());
		log.debug("Texto do comando " + textoComando);
		if (comandos.containsKey(textoComando)) {
			Comando comando = comandos.get(textoComando);
			switch (comando) {
			case DefinirRegime:
				processarDefinirRegime(message);
				break;
			case Registrar:
				processarRegistro(message);
				break;
			default:
				break;
			}
		} else {
			enviarMensagemPadrao(message.getChatId(), textoComando);
		}
	}

	private void processarRegistro(Message message) {
		if (message.getChatId() == null) {
			return;
		}
		try {
			log.debug("Processando registro de ponto");
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			Date hora = sdf.parse(message.getText());
			Calendar dataASalvar = Calendar.getInstance();
			Calendar horaInformada = Calendar.getInstance();
			horaInformada.setTime(hora);
			dataASalvar.set(Calendar.HOUR_OF_DAY,
					horaInformada.get(Calendar.HOUR_OF_DAY));
			dataASalvar
					.set(Calendar.MINUTE, horaInformada.get(Calendar.MINUTE));
			dataASalvar.set(Calendar.SECOND, 0);
			dataASalvar.set(Calendar.MILLISECOND, 0);

			repositorio.registrar(message.getChatId(), dataASalvar.getTime());
			synchronized (processadoresAlertas) {
				Thread existingThread = null;
				if (processadoresAlertas.containsKey(message.getChatId())) {
					Thread t = processadoresAlertas.get(message.getChatId());
					if (t.isAlive()) {
						existingThread = t;
					}
				}

				if (existingThread == null) {
					ProcessadorAlertas pa = new ProcessadorAlertas(this,
							message.getChatId());
					Thread t = new Thread(pa, "ProcessadorAlertas-"
							+ message.getChatId());
					processadoresAlertas.put(message.getChatId(), t);
					t.start();
				}
			}
		} catch (ParseException e) {
			log.error("processarRegistro - erro", e);
		}
	}

	private void processarDefinirRegime(Message message)
			throws TelegramApiException {
		if (message.getChatId() == null) {
			return;
		}
		String text = message.getText();
		log.debug("Texto enviado: " + text);
		Integer regime = null;
		if (text.equals(i18n("definirregime.keyboard.0"))) {
			regime = 6;
		} else if (text.equals(i18n("definirregime.keyboard.1"))) {
			regime = 8;
		}
		if (regime != null) {
			log.debug("Definindo regime para " + regime + "h");
			repositorio.registrarRegime(message.getChatId(), regime);
			SendMessage sm = new SendMessage();
			sm.setChatId(message.getChatId().toString());
			sm.setText(i18n(String.format("resposta.regime_definido"), regime));
			sendMessage(sm);
			repositorio.concluirComando(message.getChatId());
		}
	}

	private void enviarMensagemPadrao(Long chatId, String comando)
			throws TelegramApiException {
		if (chatId == null) {
			return;
		}
		SendMessage sm = new SendMessage();
		sm.setChatId(chatId.toString());
		sm.setText(i18n("resposta.padrao", comando == null ? 0 : 1, comando));
		sendMessage(sm);
	}

	private void registerChat(Chat chat) throws TelegramApiException {
		if (chat == null || chat.getId() == null) {
			return;
		}
		if (!repositorio.contemConversa(chat.getId())) {
			repositorio.iniciarConversa(chat.getId(), chat.getFirstName(),
					chat.getLastName(), chat.getUserName());
			enviarMensagemPadrao(chat.getId(), null);
		}
	}

	private void processarComando(Message message) throws TelegramApiException {
		if (message.getChatId() == null) {
			return;
		}
		if (repositorio.possuiComandoIniciado(message.getChatId())) {
			String comandoAnterior = repositorio.cancelarComando(message
					.getChatId());
			SendMessage sm = new SendMessage();
			sm.setChatId(message.getChatId().toString());
			sm.setText(i18n("resposta.cancelamento_comando", comandoAnterior));
			sendMessage(sm);
		}
		String comando = message.getText().substring(1);
		log.debug("Comando: " + comando);
		if (comandos.containsKey(comando)) {
			if (i18nBoolean(String.format("%s.aguarda", comando))) {
				repositorio.iniciarComando(message.getChatId(), comando);
				SendMessage sm = new SendMessage();
				sm.setChatId(message.getChatId().toString());
				sm.setReplyMarkup(makeReplyMarkup(comando));
				sm.setText(i18n(String.format("resposta.%s.ok", comando)));
				sendMessage(sm);
			} else {
				processarComandoImediato(message.getChatId(), comando);
			}
		} else {
			enviarMensagemPadrao(message.getChatId(), comando);
		}
	}

	private void processarComandoImediato(Long chatId, String textoComando)
			throws TelegramApiException {
		Comando comando = comandos.get(textoComando);
		SendMessage sm = new SendMessage();
		sm.setChatId(chatId.toString());
		sm.setReplyMarkup(makeReplyMarkup(textoComando));
		String mensagem;
		switch (comando) {
		case Regime:
			mensagem = i18n("resposta.regime", repositorio.getRegime(chatId));
			break;
		default:
			mensagem = null;
			break;
		}
		if (mensagem != null) {
			sm.setText(mensagem);
			sendMessage(sm);
		}
	}

	private ReplyKeyboard makeReplyMarkup(String comando) {
		ReplyKeyboard rk;
		if (i18nBoolean(String.format("%s.keyboard", comando))) {
			log.debug("Criando reply em teclado");
			ReplyKeyboardMarkup rm = new ReplyKeyboardMarkup();
			rm.setOneTimeKeyboad(true);
			rm.setResizeKeyboard(true);
			rm.setKeyboard(makeKeyboard(comando));
			rk = rm;
		} else {
			log.debug("Limpando reply em teclado");
			rk = new ReplyKeyboardHide();
		}
		return rk;
	}

	private List<KeyboardRow> makeKeyboard(String comando) {
		List<KeyboardRow> rows = new LinkedList<KeyboardRow>();
		int quantidade = i18nInt(String.format("%s.keyboard.quantity", comando));
		KeyboardRow r = new KeyboardRow();
		rows.add(r);
		for (int i = 0; i < quantidade; i++) {
			r.add(i18n(String.format("%s.keyboard.%d", comando, i)));
		}
		return rows;
	}

	@Override
	public String getBotToken() {
		return "277156703:AAG7MLrzaHLfkXAT-w4cHBXxEv3BBFGmVzo";
	}

	public void alertar(long chatId, TipoAlerta tipoAlerta) {
		try {
			SendMessage sm = new SendMessage();
			sm.setChatId(String.valueOf(chatId));
			sm.setText(i18n(String.format("resposta.alertar.%s",
					tipoAlerta.toString())));
			sendMessage(sm);
		} catch (TelegramApiException e) {
			log.warn("Erro no alertar", e);
		}
	}

	public void informarProximoAlerta(long chatId, Date data) {

		try {
			SendMessage sm = new SendMessage();
			sm.setChatId(String.valueOf(chatId));
			sm.setText(i18n(String.format("resposta.proximoAlerta"), data));
			sendMessage(sm);
		} catch (TelegramApiException e) {
			log.warn("Erro ao informar próximo alerta", e);
		}
	}

	public Alerta obterProximoAlerta(long chatId) {
		synchronized (processadoresAlertas) {
			return repositorio.proximoAlerta(chatId, new Date());
		}
	}

	public void cancelarAlertas(long chatId) {
		synchronized (processadoresAlertas) {
			processadoresAlertas.remove(chatId);
		}
	}

}
