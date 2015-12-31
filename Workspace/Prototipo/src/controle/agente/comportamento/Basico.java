package controle.agente.comportamento;

import controle.dominio.identificador.IdentificadorElementoGerenciado;
import jade.core.behaviours.CyclicBehaviour;

/**
 * Comportamento padrão estendido pelos agentes.
 */
public abstract class Basico extends CyclicBehaviour {

	private static final long serialVersionUID = 2860181213874735039L;

	private static final String PREFIXO_IDENTIFICACAO = "[";
	private static final String SUFIXO_IDENTIFICACAO = "]";

	private String nomeElementoGerenciado;

	/**
	 * Retorna o nome do elemento gerenciado observado pelo comportamento de um
	 * sensor, ou influenciado pelo comportamento de um atuador. O nome do
	 * elemento gerenciado é configurado na inicialização, padronizado
	 * "[nome_do_elemento]", sendo que "nome_do_elemento" deve corresponder a um
	 * valor da enum {@link IdentificadorElementoGerenciado}
	 */
	public String getNomeElementoGerenciado() {

		if (this.nomeElementoGerenciado == null) {
			synchronized (this) {
				this.nomeElementoGerenciado = super.getAgent().getAID()
						.getLocalName();
				this.nomeElementoGerenciado = this.nomeElementoGerenciado
						.substring(
								this.nomeElementoGerenciado
										.indexOf(PREFIXO_IDENTIFICACAO)
										+ PREFIXO_IDENTIFICACAO.length(),
								this.nomeElementoGerenciado
										.indexOf(SUFIXO_IDENTIFICACAO));
			}
		}
		return this.nomeElementoGerenciado;
	}

}