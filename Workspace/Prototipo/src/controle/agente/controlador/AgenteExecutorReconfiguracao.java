package controle.agente.controlador;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.kie.internal.runtime.StatefulKnowledgeSession;

import controle.agente.DiretorioAgenteJadeUtil;
import controle.dominio.identificador.IdentificadorElementoGerenciado;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import util.Erro;

/**
 * Agente responsável por gerenciar a execução dos agentes Atuadores
 */
public class AgenteExecutorReconfiguracao extends Agent {

	private static final long serialVersionUID = 273786892468632402L;

	/**
	 * Sessão Drools inicializada pelo AgenteInicializador
	 */
	private StatefulKnowledgeSession session = null;

	/**
	 * Fila de reconfigurações a serem executadas
	 */
	private Queue<Reconfiguracao> reconfiguracoes = new ConcurrentLinkedQueue<>();

	/**
	 * Armazena referência ao agente atuador a ser ativado
	 */
	public class Reconfiguracao {

		private Class<? extends Agent> classeAgente;
		private IdentificadorElementoGerenciado identificadorElemento;

		public Reconfiguracao(Class<? extends Agent> classeAgente,
				IdentificadorElementoGerenciado identificadorElemento) {
			this.classeAgente = classeAgente;
			this.identificadorElemento = identificadorElemento;
		}

		@Override
		public boolean equals(Object obj) {
			Reconfiguracao other = (Reconfiguracao) obj;
			return this.classeAgente.equals(other.classeAgente)
					&& this.identificadorElemento
							.equals(other.identificadorElemento);
		}

		@Override
		public int hashCode() {
			int hash = 1;
			hash = hash * 17 + classeAgente.hashCode();
			hash = hash * 31 + classeAgente.hashCode();
			return hash;
		}
	}

	/**
	 * Método disparado pelas regras do processador de eventos, declaradas no
	 * arquivo {@link regrasReconfiguracao.drl}. Este método adiciona um item de
	 * reconfiguração na fila do agente.
	 * 
	 * @param classeAgente
	 *            Classe do agente atuador a ser ativado
	 * @param identificadorElemento
	 *            Identificador do elemento onde ocorrerá a atuação
	 */
	public void ruleFired(Class<? extends Agent> classeAgente,
			IdentificadorElementoGerenciado identificadorElemento) {

		Reconfiguracao r = new Reconfiguracao(classeAgente,
				identificadorElemento);
		if (!reconfiguracoes.contains(r)) {
			reconfiguracoes.add(r);
			System.out.println("ruleFired: " + classeAgente + ", "
					+ identificadorElemento.toString());
		}
	}

	/**
	 * Comportamento cíclico, executado a cada 300ms, que consome a fila de
	 * reconfigurações e ativa os respectivos agentes.
	 */
	public class ExecutorReconfiguracao extends CyclicBehaviour {

		private static final long serialVersionUID = -1017720562038855599L;

		@Override
		public void action() {
			try {
				Reconfiguracao reconfiguracao = ((AgenteExecutorReconfiguracao) this.myAgent).reconfiguracoes
						.poll();
				if (reconfiguracao != null) {
					((AgenteExecutorReconfiguracao) this.myAgent).reconfiguracoes
							.size();
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					// msg.setContent(reconfiguracao.mensagem);
					msg.addReceiver(DiretorioAgenteJadeUtil.pesquisar(
							this.myAgent, reconfiguracao.classeAgente,
							reconfiguracao.identificadorElemento));
					this.myAgent.send(msg);
					Thread.sleep(300);
				}
			} catch (Exception e) {
				Erro.registrar(e);
			}
		}
	}

	@Override
	protected void setup() {
		super.setup();
		this.session = (StatefulKnowledgeSession) super.getArguments()[0];
		super.addBehaviour(new ExecutorReconfiguracao());
		// atribui a referência ao agente dentro do mecanismo de processamento
		// de eventos para permitir a execução de agentes atuadores a partir de
		// regras definidas
		this.session.setGlobal("controladorAtuacao", this);

		DiretorioAgenteJadeUtil.registrar(this, null);
	}

	@Override
	protected void finalize() throws Throwable {

		DiretorioAgenteJadeUtil.remover(this);
		super.finalize();
	}

}
