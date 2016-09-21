/**
 * 
 */
package clt.lembreteponto;

import java.util.Date;

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

	void registrar(long chatId, Date time);

	Alerta proximoAlerta(long chatId, Date data);

}
