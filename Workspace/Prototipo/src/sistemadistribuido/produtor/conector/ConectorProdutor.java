package sistemadistribuido.produtor.conector;

public interface ConectorProdutor {

	public void ativar();

	public void inativar();

	public boolean isAtivo();

	public String getNomeInstancia();
	
	public Long getIntervaloTempoAtualEntreMensagensMilisegundos();

	public Long getNumeroMensagens();

	public Long getNumeroConsumidores();
}
