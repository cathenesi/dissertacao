package controle.agente.comportamento;

import jade.lang.acl.ACLMessage;

import java.io.IOException;

import util.Erro;
import controle.agente.DiretorioAgenteJadeUtil;
import controle.agente.controlador.AgenteProcessadorEvento;
import controle.evento.EventoPrimitivo;

/**
 * Comportamento estendido pelos sensores, para facilitar a publicação de
 * eventos.
 * 
 * @param <T>
 *            Tipo do Evento Primitivo a ser publicado.
 */
public abstract class PublicarEventoPrimitivo<T extends EventoPrimitivo> extends Basico {

	private static final long serialVersionUID = -1969910869557015465L;

	/**
	 * Este método é executado em intervalos de tempos definidos no método
	 * {@link getIntervaloExecucaoMilisegundos()} e publica um evento primitivo
	 * no fluxo do agente {@link AgenteProcessadorEvento}.
	 */
	@Override
	public void action() {

		try {
			T eventoPrimitivo = coletarEvento();

			if (eventoPrimitivo != null) {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setContentObject(eventoPrimitivo);
				msg.addReceiver(DiretorioAgenteJadeUtil.pesquisar(super.myAgent, AgenteProcessadorEvento.class));
				super.myAgent.send(msg);
			}

			Long intervalInMillisecons = getIntervaloExecucaoMilisegundos();
			if (intervalInMillisecons != null) {
				try {
					Thread.sleep(intervalInMillisecons);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			Erro.registrar(e);
		}
	}

	/**
	 * Método abstrato a ser implementado por cada comportamento, que retorna o
	 * evento primitivo a ser publicado.
	 * 
	 * @return Instância de um evento primitivo do tipo <T>
	 */
	public abstract T coletarEvento();

	/**
	 * Método abstrato, a ser implementado por cada comportamento, que retorna o
	 * valor do intervalo, em milissegundos, a ser considerado na publicação dos
	 * eventos.
	 */
	public abstract long getIntervaloExecucaoMilisegundos();

}
