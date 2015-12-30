package sistemadistribuido.servidor.conector;

/**
 * Definição de conector que permite ativar e inativar uma instância do servidor
 * {@link Emulador}
 */
public interface ConectorAtivacao {

	public void ativar();

	public void inativar();

	public boolean isAtivo();

	public String getNomeInstancia();

}
