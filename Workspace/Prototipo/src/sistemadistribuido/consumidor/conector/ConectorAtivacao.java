package sistemadistribuido.consumidor.conector;

/**
 * Definição de conector que permite ativar e inativar uma instância da
 * aplicação {@link Consumidor}
 */
public interface ConectorAtivacao {

	public void ativar();

	public void inativar();

	public boolean isAtivo();

	public String getNomeInstancia();

}
