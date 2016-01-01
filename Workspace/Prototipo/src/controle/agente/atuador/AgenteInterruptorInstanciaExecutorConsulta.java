package controle.agente.atuador;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import sistemadistribuido.executorconsulta.conector.JMXConectorAtivacaoUtil;
import util.Erro;
import controle.agente.DiretorioAgenteJadeUtil;
import controle.agente.comportamento.Basico;

/**
 * A2: Agente responsável pela interrupção de uma instância do servidor
 * {@link Emulador}.
 */
public class AgenteInterruptorInstanciaExecutorConsulta extends Agent {

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

					//String elementoGerenciado = msgReceived.getContent();

					if (!(Boolean) instancia
							.invocarMetodoInstanciaExecutorConsulta(
									JMXConectorAtivacaoUtil.MetodoInstancia.IS_ATIVO,
									super.getNomeElementoGerenciado())) {
						return;
					}

					instancia.invocarMetodoInstanciaExecutorConsulta(
							JMXConectorAtivacaoUtil.MetodoInstancia.INATIVAR,
							super.getNomeElementoGerenciado());
				}
			} catch (Exception e) {
				Erro.registrar(e);
			}
		}
	}

	@Override
	protected void setup() {
		super.setup();
		InterromperInstancia comportamento = new InterromperInstancia();
		super.addBehaviour(comportamento);
		DiretorioAgenteJadeUtil.registrar(this,
				comportamento.getIdentificadorElementoGerenciado());
	}

	@Override
	protected void finalize() throws Throwable {

		DiretorioAgenteJadeUtil.remover(this);
		super.finalize();
	}

}
