package controle.evento;

import controle.dominio.IdentificadorAtributoElementoGerenciado;
import controle.dominio.IdentificadorElementoGerenciado;

/**
 * Evento publicado para indicar o número de mensagens na fila.
 */
public class EventoNumeroMensagensFila extends EventoPrimitivo {

	private static final long serialVersionUID = 6120581406971018787L;

	private Long numeroMensagensFila;

	public EventoNumeroMensagensFila() {
	}

	public EventoNumeroMensagensFila(IdentificadorElementoGerenciado identificadorElementoGerenciado,
			IdentificadorAtributoElementoGerenciado identificadorAtributoElementoGerenciado, Long numeroMensagensFila) {
		super(identificadorElementoGerenciado, identificadorAtributoElementoGerenciado);
		this.numeroMensagensFila = numeroMensagensFila;
	}

	public void setNumeroMensagensFila(Long numeroMensagensFila) {
		this.numeroMensagensFila = numeroMensagensFila;
	}

	public Long getNumeroMensagensFila() {
		return numeroMensagensFila;
	}

	@Override
	public String toString() {
		return "numeroMensagensFila=" + numeroMensagensFila;
	}

}
