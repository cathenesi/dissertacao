package sistemadistribuido.servidor.conector;

/**
 * Definição de conector que permite ativar e inativar uma instância do servidor
 * {@link Emulador}
 */
public interface ConectorAtivacao {

	/**
	 * Método através do qual um agente atuador ativa a instância. 
	 */
	public void ativar();

	/**
	 * Método através do qual um agente atuador inativa a instância. 
	 */
	public void inativar();

	/**
	 * Método através do qual um agente sensor verifica se a instância está ativa ou inativa. 
	 */
	public boolean isAtivo();

	/**
	 * Método através do qual um agente sensor identifica o nome da instância.
	 */
	public String getNomeInstancia();

}
