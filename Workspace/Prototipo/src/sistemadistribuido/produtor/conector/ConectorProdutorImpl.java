package sistemadistribuido.produtor.conector;

import sistemadistribuido.produtor.SimuladorCliente;
import util.JMXUtil;
import util.Ambiente;
import util.Log;

public class ConectorProdutorImpl implements ConectorProdutor {

	private Boolean ativo = true;

	private JMXUtil activeMQ = new JMXUtil();
	private SimuladorCliente.ProdutorFila produtor = null;

	public ConectorProdutorImpl() {
	}

	public ConectorProdutorImpl(SimuladorCliente.ProdutorFila produtor) {
		this.produtor = produtor;
	}

	/**
	 * Ativa uma instância da aplicação que consome a fila de mensagens,
	 * permitindo a execução da instância.
	 */
	@Override
	public synchronized void ativar() {
		this.ativo = true;
	}

	/**
	 * Inativa uma instância da aplicação que consome a fila de mensagens,
	 * interrompendo a execução da instância.
	 */
	@Override
	public synchronized void inativar() {
		this.ativo = false;
		this.produtor.reinicializar();
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
	 * Retorna o nome da instância
	 */
	@Override
	public String getNomeInstancia() {
		return Ambiente.getNomeInstancia();
	}

	@Override
	public Long getIntervaloTempoAtualEntreMensagensMilisegundos() {
		return this.produtor.getIntervaloTempoAtualMilisegundos();
	}

	@Override
	public Long getNumeroMensagens() {

		try {
			return (Long) this.activeMQ.invocarMetodoFila(
					JMXUtil.MetodoFila.QUEUE_SIZE, null);
		} catch (Exception e) {
			Log.registrar(e);
		}
		return null;
	}

	@Override
	public Long getNumeroConsumidores() {

		try {
			return (Long) this.activeMQ.invocarMetodoFila(
					JMXUtil.MetodoFila.CONSUMER_COUNT, null);
		} catch (Exception e) {
			Log.registrar(e);
		}
		return null;
	}

}
