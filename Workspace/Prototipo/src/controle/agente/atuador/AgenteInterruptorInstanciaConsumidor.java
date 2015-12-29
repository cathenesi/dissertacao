package controle.agente.atuador;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import util.DFUtil;
import util.JMXUtil;
import util.Log;
import controle.agente.sensor.comportamento.ComportamentoPadrao;

/**
 * Agente responsável pela interrupção de instância de um consumidor da fila.
 */
public class AgenteInterruptorInstanciaConsumidor extends Agent {

	private static final long serialVersionUID = -2117862749424897781L;

	/**
	 * Comportamento do agente, interrompe a execução de uma instância da
	 * aplicação consumidora quando recebe notificação do agente Executor de
	 * Reconfiguração.
	 */
	public class InterromperInstancia extends ComportamentoPadrao {

		private static final long serialVersionUID = -1969910869557015465L;

		private JMXUtil instancia = new JMXUtil();

		@Override
		public void action() {
			try {
				ACLMessage msgReceived = super.myAgent.receive();
				if (msgReceived != null) {

					String elementoGerenciado = msgReceived.getContent();

					if (!(Boolean) instancia.invocarMetodoInstanciaExecutorConsulta(JMXUtil.MetodoInstancia.IS_ATIVO,
							elementoGerenciado)) {
						return;
					}

					instancia.invocarMetodoInstanciaExecutorConsulta(JMXUtil.MetodoInstancia.INATIVAR,
							elementoGerenciado);
				}
			} catch (Exception e) {
				Log.registrar(e);
			}
		}
	}

	@Override
	protected void setup() {
		super.setup();
		super.addBehaviour(new InterromperInstancia());

		DFUtil.register(this);
	}

	@Override
	protected void finalize() throws Throwable {

		DFUtil.deregister(this);
		super.finalize();
	}

}
