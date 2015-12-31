package controle.evento;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import controle.dominio.identificador.IdentificadorAtributoElementoGerenciado;
import controle.dominio.identificador.IdentificadorElementoGerenciado;

/**
 * Super classe dos eventos primitivos, gerados pelos sensores.
 */
public abstract class EventoPrimitivo implements Serializable {

	private static final long serialVersionUID = -347863980913150974L;

	private IdentificadorElementoGerenciado identificadorElementoGerenciado;
	private IdentificadorAtributoElementoGerenciado identificadorAtributoElementoGerenciado;
	private Date dataHora;
	private String ipOrigem;

	public EventoPrimitivo() {
	}

	/**
	 * Cria o evento, passando por parametro qual elemento gerou o evento, e
	 * qual o nome do atributo a que se refere. O evento transporta, também, a
	 * data/hora e o endereço IP do host onde o evento foi criado.
	 * 
	 * @param identificadorElementoGerenciado
	 * @param identificadorAtributoElementoGerenciado
	 */
	public EventoPrimitivo(
			IdentificadorElementoGerenciado identificadorElementoGerenciado,
			IdentificadorAtributoElementoGerenciado identificadorAtributoElementoGerenciado) {
		this.identificadorElementoGerenciado = identificadorElementoGerenciado;
		this.identificadorAtributoElementoGerenciado = identificadorAtributoElementoGerenciado;
		this.dataHora = new Date();
		try {
			this.ipOrigem = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			this.ipOrigem = null;
		}
	}

	public void setIdentificadorElementoGerenciado(
			IdentificadorElementoGerenciado identificadorElementoGerenciado) {
		this.identificadorElementoGerenciado = identificadorElementoGerenciado;
	}

	public IdentificadorElementoGerenciado getIdentificadorElementoGerenciado() {
		return identificadorElementoGerenciado;
	}

	public void setIdentificadorAtributoElementoGerenciado(
			IdentificadorAtributoElementoGerenciado identificadorAtributoElementoGerenciado) {
		this.identificadorAtributoElementoGerenciado = identificadorAtributoElementoGerenciado;
	}

	public IdentificadorAtributoElementoGerenciado getIdentificadorAtributoElementoGerenciado() {
		return identificadorAtributoElementoGerenciado;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setIpOrigem(String ipOrigem) {
		this.ipOrigem = ipOrigem;
	}

	public String getIpOrigem() {
		return ipOrigem;
	}

}
