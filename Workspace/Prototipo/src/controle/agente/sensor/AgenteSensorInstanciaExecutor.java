package controle.agente.sensor;

import controle.agente.sensor.comportamento.PublicarEventoPrimitivo;
import controle.dominio.IdentificadorAtributoElementoGerenciado;
import controle.dominio.IdentificadorElementoGerenciado;
import controle.evento.EventoInstanciaAtiva;
import controle.evento.EventoInstanciaInativa;
import jade.core.Agent;
import util.DFUtil;
import util.JMXUtil;

/**
 * Agente sensor destinado a identificar o estado de uma instância do consumidor
 * da fila de mensagens.
 */
public class AgenteSensorInstanciaExecutor extends Agent {

	private static final long serialVersionUID = 4585767475494660603L;

	/**
	 * Comportamento do agente, destinado a publicar instâncias do evento
	 * primitivo {@link EventoInstanciaAtiva} a cada 1s, caso a instância
	 * observada esteja ativa.
	 */
	public class PublicarEventoInstanciaAtiva extends PublicarEventoPrimitivo<EventoInstanciaAtiva> {

		private static final long serialVersionUID = -3446027377283448304L;

		private JMXUtil instancia = new JMXUtil();

		@Override
		public EventoInstanciaAtiva coletarEvento() {

			Boolean isAtivo = false;
			try {
				isAtivo = (Boolean) instancia.invocarMetodoInstanciaExecutorConsulta(JMXUtil.MetodoInstancia.IS_ATIVO,
						super.getNomeElementoGerenciado());
			} catch (RuntimeException e) {
				isAtivo = false;
			}
			if (isAtivo != null && isAtivo) {

				return new EventoInstanciaAtiva(
						IdentificadorElementoGerenciado.getByName(super.getNomeElementoGerenciado()),
						IdentificadorAtributoElementoGerenciado.ESTADO);
			}
			return null;
		}

		@Override
		public long getIntervaloExecucaoMilisegundos() {
			return 1000;
		}
	}

	/**
	 * Comportamento do agente, destinado a publicar instâncias do evento
	 * primitivo {@link EventoInstanciaInativa} a cada 1s, caso a instância
	 * observada esteja inativa.
	 */
	public class PublicarEventoInstanciaInativa extends PublicarEventoPrimitivo<EventoInstanciaInativa> {

		private static final long serialVersionUID = 2564307284783450889L;

		private JMXUtil instancia = new JMXUtil();

		@Override
		public EventoInstanciaInativa coletarEvento() {

			Boolean isAtivo = false;
			try {
				isAtivo = (Boolean) instancia.invocarMetodoInstanciaExecutorConsulta(JMXUtil.MetodoInstancia.IS_ATIVO,
						super.getNomeElementoGerenciado());
			} catch (RuntimeException e) {
				isAtivo = false;
			}
			if (isAtivo == null || !isAtivo) {
				return new EventoInstanciaInativa(
						IdentificadorElementoGerenciado.getByName(super.getNomeElementoGerenciado()),
						IdentificadorAtributoElementoGerenciado.ESTADO);
			}
			return null;
		}

		@Override
		public long getIntervaloExecucaoMilisegundos() {
			return 1000;
		}
	}

	@Override
	protected void setup() {
		super.setup();
		super.addBehaviour(new PublicarEventoInstanciaAtiva());
		super.addBehaviour(new PublicarEventoInstanciaInativa());

		DFUtil.register(this);
	}

	@Override
	protected void finalize() throws Throwable {

		DFUtil.deregister(this);
		super.finalize();
	}

}