package controle.agente.atuador;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import sistemadistribuido.servidor.conector.JMXConectorAtivacaoUtil;
import util.Erro;
import controle.agente.DiretorioAgenteJadeUtil;
import controle.agente.comportamento.Basico;

/**
 * Agente responsável pela interrupção de uma instância do servidor
 * {@link Emulador}.
 */
public class AgenteInterruptorInstanciaServidor extends Agent {

	private static final long serialVersionUID = -2117862749424897781L;

	/**
	 * Comportamento do agente, interrompe a execução de uma instância da
	 * aplicação quando recebe notificação do agente Executor de Reconfiguração.
	 */
	public class InterromperInstancia extends Basico {

		private static final long serialVersionUID = -1969910869557015465L;

		private JMXConectorAtivacaoUtil instancia = new JMXConectorAtivacaoUtil();

		@Override
		public void action() {
			try {
				ACLMessage msgReceived = super.myAgent.receive();
				if (msgReceived != null) {

					String elementoGerenciado = msgReceived.getContent();

					if (!(Boolean) instancia
							.invocarMetodoInstanciaExecutorConsulta(
									JMXConectorAtivacaoUtil.MetodoInstancia.IS_ATIVO,
									elementoGerenciado)) {
						return;
					}

					instancia.invocarMetodoInstanciaExecutorConsulta(
							JMXConectorAtivacaoUtil.MetodoInstancia.INATIVAR,
							elementoGerenciado);
				}
			} catch (Exception e) {
				Erro.registrar(e);
			}
		}
	}

	@Override
	protected void setup() {
		super.setup();
		super.addBehaviour(new InterromperInstancia());

		DiretorioAgenteJadeUtil.registrar(this);
	}

	@Override
	protected void finalize() throws Throwable {

		DiretorioAgenteJadeUtil.remover(this);
		super.finalize();
	}

}
