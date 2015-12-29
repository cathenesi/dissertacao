package controle.agente.controlador;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import util.DFUtil;
import util.Log;
import controle.evento.EventoPrimitivo;

/**
 * Agente responsável pelo processamento dos eventos gerados pelos sensores
 */
public class AgenteProcessadorEvento extends Agent {

	private static final long serialVersionUID = 273786892468632402L;

	private StatefulKnowledgeSession session = null;
	private EntryPoint queueStream = null;

	/**
	 * Comportamento cíclico, disparado a cada 300ms, para ler os eventos
	 * publicadas pelos sensores e adicioná-los ao fluxo de eventos, que será
	 * lido pelo processador de eventos do Drools. O processamento de eventos é
	 * executado em uma outra thread, para não interromper o agente.
	 */
	public class ProcessadorEvento extends CyclicBehaviour {

		private static final long serialVersionUID = -1969910869557015465L;

		/**
		 * O construtor inicializa uma thread que avalia o fluxo de eventos a
		 * cada 1s
		 */
		public ProcessadorEvento() {
			super();

			new Thread(new Runnable() {

				@Override
				public void run() {
					while (!Thread.currentThread().isInterrupted()) {
						AgenteProcessadorEvento.this.session.fireAllRules();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							Log.registrar(e);
						}
					}
				}
			}).start();
		}

		/**
		 * Método que obtém os eventos enviados pelos sensores e os publica no
		 * fluxo de eventos.
		 */
		@Override
		public void action() {

			try {

				ACLMessage msgReceived = super.myAgent.receive();
				if (msgReceived != null) {

					EventoPrimitivo evento = (EventoPrimitivo) msgReceived.getContentObject();
					// System.out.println(new Date() +
					// " - Evento enviado do IP "
					// + evento.getIpOrigem() + " em "
					// + evento.getDataHora() + "): "
					// + evento.getIdentificador() + " - "
					// + evento.toString());

					if (evento instanceof EventoPrimitivo) {
						// System.out.println(evento.toString());
						queueStream.insert(evento);
					}
					Thread.sleep(300);
				}

			} catch (Exception e) {
				Log.registrar(e);
			}
		}
	}

	@Override
	protected void setup() {
		super.setup();
		this.session = (StatefulKnowledgeSession) super.getArguments()[0];
		this.queueStream = (EntryPoint) super.getArguments()[1];
		super.addBehaviour(new ProcessadorEvento());

		DFUtil.register(this);
	}

	@Override
	protected void finalize() throws Throwable {

		DFUtil.deregister(this);
		super.finalize();
	}

}
