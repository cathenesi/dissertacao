package controle.agente;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
 * Utilitário para acessar o diretório de agentes da plataforma Jade.
 */
public class DiretorioAgenteJadeUtil {

	private static final String DESCRICAO_TIPO = "-Type";

	/**
	 * Padroniza o nome de um agente para publicação no diretório.
	 */
	public static String getNomeAgente(Class<? extends Agent> classeAgente) {
		return classeAgente.getSimpleName();
	}

	/**
	 * Padroniza o nome de um tipos de agentes para publicação no diretório.
	 */
	public static String getTipoAgente(Class<? extends Agent> classeAgente) {
		return classeAgente.getSimpleName() + DESCRICAO_TIPO;
	}

	/**
	 * Registra um agente no diretório.
	 */
	public static void registrar(Agent agente) {

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(agente.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getNomeAgente(agente.getClass()));
		sd.setType(getTipoAgente(agente.getClass()));
		dfd.addServices(sd);
		try {
			DFService.register(agente, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Remove o registro de um agente no diretório.
	 */
	public static void remover(Agent agente) {

		try {
			DFService.deregister(agente);
		} catch (FIPAException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Pesquisa um agente no diretório.
	 */
	public static AID pesquisar(Agent agenteAtual, Class<? extends Agent> classeAgentePesquisar) {

		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getNomeAgente(classeAgentePesquisar));
		sd.setType(getTipoAgente(classeAgentePesquisar));
		dfd.addServices(sd);
		try {
			DFAgentDescription[] results = DFService.search(agenteAtual, dfd);
			if (results != null && results.length > 0) {
				return results[0].getName();
			}
		} catch (FIPAException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return null;
	}

}
