package controle.agente.sensor;

import jade.core.Agent;
import util.DFUtil;
import controle.agente.sensor.comportamento.PublicarEventoPrimitivo;
import controle.dominio.identificador.IdentificadorAtributoElementoGerenciado;
import controle.dominio.identificador.IdentificadorElementoGerenciado;
import controle.evento.EventoNumeroRequisicoesSimultaneas;

/**
 * Agente sensor destinado a obter o número de requisições em execu;áo
 * simultaneamente no balanceador de carga.
 */
public class AgenteSensorBalanceador extends Agent {

	private static final long serialVersionUID = 3320202385578532920L;

	/**
	 * Comportamento do agente, destinado a publicar instâncias do evento
	 * primitivo {@link EventoNumeroRequisicoesSimultaneas} a cada 1s.
	 */
	public class PublicarEventoComNumeroMensagensFila extends
			PublicarEventoPrimitivo<EventoNumeroRequisicoesSimultaneas> {

		private static final long serialVersionUID = 1200706390207965224L;

		// private JMXUtil filaMensagem = new JMXUtil();

		@Override
		public EventoNumeroRequisicoesSimultaneas coletarEvento() {
			return new EventoNumeroRequisicoesSimultaneas(
					IdentificadorElementoGerenciado.BALANCEADOR,
					IdentificadorAtributoElementoGerenciado.NUMERO_REQUISICOES_SIMULTANEAS, 15L);
			
					// TODO: ajustar para obter quantidade do balanceador
					// (Long) filaMensagem.invocarMetodoFila(JMXUtil.MetodoFila.QUEUE_SIZE, null));
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

}
