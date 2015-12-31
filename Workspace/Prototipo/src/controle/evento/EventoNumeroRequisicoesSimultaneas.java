package controle.evento;

import controle.dominio.identificador.IdentificadorAtributoElementoGerenciado;
import controle.dominio.identificador.IdentificadorElementoGerenciado;

/**
 * Evento publicado para indicar o número de requisições simultâneas em execução
 * no balanceador de carga.
 */
public class EventoNumeroRequisicoesSimultaneas extends EventoPrimitivo {

	private static final long serialVersionUID = 6120581406971018787L;

	private Long numeroRequisicoes;

	public EventoNumeroRequisicoesSimultaneas() {
	}

	/**
	 * Cria o evento, passando por parametro qual elemento gerou o evento, e
	 * qual o nome do atributo a que se refere.
	 * 
	 * @param identificadorElementoGerenciado
	 * @param identificadorAtributoElementoGerenciado
	 */
	public EventoNumeroRequisicoesSimultaneas(
			IdentificadorElementoGerenciado identificadorElementoGerenciado,
			IdentificadorAtributoElementoGerenciado identificadorAtributoElementoGerenciado,
			Long numeroRequisicoes) {
		super(identificadorElementoGerenciado,
				identificadorAtributoElementoGerenciado);
		this.numeroRequisicoes = numeroRequisicoes;
	}

	public Long getNumeroRequisicoes() {
		return numeroRequisicoes;
	}

	public void setNumeroRequisicoes(Long numeroRequisicoes) {
		this.numeroRequisicoes = numeroRequisicoes;
	}

	@Override
	public String toString() {
		return "numeroRequisicoes=" + numeroRequisicoes;
	}

}
