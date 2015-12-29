package controle.agente.sensor;

import jade.core.Agent;
import util.JMXUtil;
import util.DFUtil;
import controle.agente.sensor.comportamento.PublicarEventoPrimitivo;
import controle.dominio.IdentificadorAtributoElementoGerenciado;
import controle.dominio.IdentificadorElementoGerenciado;
import controle.evento.EventoNumeroMensagensFila;

/**
 * Agente sensor destinado a obter o número de mensagens pendentes para
 * processamento na fila de mensagens.
 */
public class AgenteSensorFilaMensagem extends Agent {

	private static final long serialVersionUID = 3320202385578532920L;

	/**
	 * Comportamento do agente, destinado a publicar instâncias do evento
	 * primitivo {@link EventoNumeroMensagensFila} a cada 1s.
	 */
	public class PublicarEventoComNumeroMensagensFila extends PublicarEventoPrimitivo<EventoNumeroMensagensFila> {

		private static final long serialVersionUID = 1200706390207965224L;

		private JMXUtil filaMensagem = new JMXUtil();

		@Override
		public EventoNumeroMensagensFila coletarEvento() {
			return new EventoNumeroMensagensFila(IdentificadorElementoGerenciado.FILA_MENSAGEM,
					IdentificadorAtributoElementoGerenciado.NUMERO_MENSAGENS,
					(Long) filaMensagem.invocarMetodoFila(JMXUtil.MetodoFila.QUEUE_SIZE, null));
		}

		@Override
		public long getIntervaloExecucaoMilisegundos() {
			return 1000;
		}
	}

	@Override
	protected void setup() {
		super.setup();
		super.addBehaviour(new PublicarEventoComNumeroMensagensFila());

		DFUtil.register(this);
	}

	@Override
	protected void finalize() throws Throwable {

		DFUtil.deregister(this);
		super.finalize();
	}

	public static void main(String[] args) {
		(new JMXUtil()).invocarMetodoFila(JMXUtil.MetodoFila.QUEUE_SIZE, null);
	}
}
