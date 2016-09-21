package clt.lembreteponto;

import java.util.Date;

public interface IProcessadorAlertasListener {
	void alertar(long chatId, TipoAlerta tipoAlerta);

	void informarProximoAlerta(long chatId, Date data);

	Alerta obterProximoAlerta(long chatId);

	void cancelarAlertas(long chatId);
}
