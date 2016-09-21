/**
 * 
 */
package clt.lembreteponto;

import java.util.Date;

/**
 * @author andre
 *
 */
public class Alerta {
	private TipoAlerta tipo;
	private Date horario;

	public TipoAlerta getTipo() {
		return tipo;
	}

	public void setTipo(TipoAlerta tipo) {
		this.tipo = tipo;
	}

	public Date getHorario() {
		return horario;
	}

	public void setHorario(Date horario) {
		this.horario = horario;
	}
}
