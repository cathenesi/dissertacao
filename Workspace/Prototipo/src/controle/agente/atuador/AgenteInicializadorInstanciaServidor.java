package controle.agente.atuador;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import util.DFUtil;
import util.JMXUtil;
import util.Log;
import controle.agente.sensor.comportamento.ComportamentoPadrao;

/**
 * Agente responsável pela inicialização de uma instância do servidor
 * {@link Emulador}.
 */
public class AgenteInicializadorInstanciaServidor extends Agent {

	private static final long serialVersionUID = -2117862749424897781L;

	/**
	 * Comportamento do agente, inicializa a execução de uma instância da
	 * aplicação quando recebe notificação do agente Executor de Reconfiguração.
	 */
	public class InicializarInstancia extends ComportamentoPadrao {

		private static final long serialVersionUID = -1969910869557015465L;

		private JMXUtil instancia = new JMXUtil();

		@Override
		public void action() {
			try {
				ACLMessage msgReceived = super.myAgent.receive();
				if (msgReceived != null) {

					String elementoGerenciado = msgReceived.getContent();

					if ((Boolean) instancia
							.invocarMetodoInstanciaExecutorConsulta(
									JMXUtil.MetodoInstancia.IS_ATIVO,
									elementoGerenciado)) {
						return;
					}

					instancia.invocarMetodoInstanciaExecutorConsulta(
							JMXUtil.MetodoInstancia.ATIVAR, elementoGerenciado);
				}
			} catch (Exception e) {
				Log.registrar(e);
			}
		}
	}

	@Override
	protected void setup() {
		super.setup();
		super.addBehaviour(new InicializarInstancia());

		DFUtil.register(this);
	}

	@Override
	protected void finalize() throws Throwable {

		DFUtil.deregister(this);
		super.finalize();
	}

}
