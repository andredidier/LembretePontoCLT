/**
 * 
 */
package clt.lembreteponto;

import java.util.Date;
import java.util.Map;

/**
 * @author andre
 *
 */
public interface IRepositorioLembretes {

	boolean contemConversa(long id);

	void iniciarConversa(long id, String firstName, String lastName,
			String userName);

	void iniciarComando(long chatId, String comando);

	boolean possuiComandoIniciado(long chatId);

	String cancelarComando(long chatId);

	String getComandoIniciado(long chatId);

	void registrarRegime(long chatId, int regime);

	Integer getRegime(long chatId);

	void concluirComando(long chatId);

	Map<Date, TipoAlerta> registrar(long chatId, Date time);

}
