package controle.agente.atuador;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import sistemadistribuido.servidor.conector.JMXConectorAtivacaoUtil;
import util.Erro;
import controle.agente.DiretorioAgenteJadeUtil;
import controle.agente.comportamento.Basico;

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
	public class InicializarInstancia extends Basico {

		private static final long serialVersionUID = -1969910869557015465L;

		private JMXConectorAtivacaoUtil instancia = new JMXConectorAtivacaoUtil();

		@Override
		public void action() {
			try {
				ACLMessage msgReceived = super.myAgent.receive();
				if (msgReceived != null) {

					String elementoGerenciado = msgReceived.getContent();

					if ((Boolean) instancia
							.invocarMetodoInstanciaExecutorConsulta(
									JMXConectorAtivacaoUtil.MetodoInstancia.IS_ATIVO,
									elementoGerenciado)) {
						return;
					}

					instancia.invocarMetodoInstanciaExecutorConsulta(
							JMXConectorAtivacaoUtil.MetodoInstancia.ATIVAR, elementoGerenciado);
				}
			} catch (Exception e) {
				Erro.registrar(e);
			}
		}
	}

	@Override
	protected void setup() {
		super.setup();
		super.addBehaviour(new InicializarInstancia());

		DiretorioAgenteJadeUtil.registrar(this);
	}

	@Override
	protected void finalize() throws Throwable {

		DiretorioAgenteJadeUtil.remover(this);
		super.finalize();
	}

}
