package sistemadistribuido.servidor.conector;

import util.Ambiente;

/**
 * Implementação de conector que permite ativar e inativar uma instância do
 * servidor {@link Emulador}
 */
public class ConectorAtivacaoImpl implements ConectorAtivacao {

	private Boolean ativo = false;

	public ConectorAtivacaoImpl(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Ativa uma instância da aplicação, permitindo a execução da instância.
	 */
	@Override
	public synchronized void ativar() {
		this.ativo = true;
	}

	/**
	 * Inativa uma instância da aplicação, interrompendo a execução da
	 * instância.
	 */
	@Override
	public synchronized void inativar() {
		this.ativo = false;
	}

	/**
	 * Retorna o estado da instância (ativa/inativa)
	 */
	@Override
	public boolean isAtivo() {
		synchronized (this.ativo) {
			return this.ativo;
		}
	}

	/**
	 * Retorna o nome da instância.
	 */
	@Override
	public String getNomeInstancia() {
		return Ambiente.getNomeInstanciaParametro();
	}

}
