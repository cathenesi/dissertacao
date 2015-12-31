package controle.agente.sensor;

import jade.core.Agent;
import sistemadistribuido.servidor.Emulador;
import sistemadistribuido.servidor.conector.JMXConectorAtivacaoUtil;
import controle.agente.DiretorioAgenteJadeUtil;
import controle.agente.comportamento.PublicarEventoPrimitivo;
import controle.dominio.identificador.IdentificadorAtributoElementoGerenciado;
import controle.dominio.identificador.IdentificadorElementoGerenciado;
import controle.evento.EventoInstanciaAtiva;
import controle.evento.EventoInstanciaInativa;

/**
 * Agente sensor destinado a identificar o estado de uma instância do servidor
 * {@link Emulador}
 */
public class AgenteSensorInstanciaServidor extends Agent {

	private static final long serialVersionUID = 4585767475494660603L;

	/**
	 * Comportamento do agente, destinado a publicar instâncias do evento
	 * primitivo {@link EventoInstanciaAtiva} a cada 1s, caso a instância
	 * observada esteja ativa.
	 */
	public class PublicarEventoInstanciaAtiva extends
			PublicarEventoPrimitivo<EventoInstanciaAtiva> {

		private static final long serialVersionUID = -3446027377283448304L;

		private JMXConectorAtivacaoUtil instancia = new JMXConectorAtivacaoUtil();

		@Override
		public EventoInstanciaAtiva coletarEvento() {

			Boolean isAtivo = false;
			try {
				isAtivo = (Boolean) instancia
						.invocarMetodoInstanciaExecutorConsulta(
								JMXConectorAtivacaoUtil.MetodoInstancia.IS_ATIVO,
								super.getNomeElementoGerenciado());
			} catch (RuntimeException e) {
				isAtivo = false;
			}
			if (isAtivo != null && isAtivo) {

				return new EventoInstanciaAtiva(
						IdentificadorElementoGerenciado.getByName(super
								.getNomeElementoGerenciado()),
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
	public class PublicarEventoInstanciaInativa extends
			PublicarEventoPrimitivo<EventoInstanciaInativa> {

		private static final long serialVersionUID = 2564307284783450889L;

		private JMXConectorAtivacaoUtil instancia = new JMXConectorAtivacaoUtil();

		@Override
		public EventoInstanciaInativa coletarEvento() {

			Boolean isAtivo = false;
			try {
				isAtivo = (Boolean) instancia
						.invocarMetodoInstanciaExecutorConsulta(
								JMXConectorAtivacaoUtil.MetodoInstancia.IS_ATIVO,
								super.getNomeElementoGerenciado());
			} catch (RuntimeException e) {
				isAtivo = false;
			}
			if (isAtivo == null || !isAtivo) {
				return new EventoInstanciaInativa(
						IdentificadorElementoGerenciado.getByName(super
								.getNomeElementoGerenciado()),
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

		DiretorioAgenteJadeUtil.registrar(this);
	}

	@Override
	protected void finalize() throws Throwable {

		DiretorioAgenteJadeUtil.remover(this);
		super.finalize();
	}

}