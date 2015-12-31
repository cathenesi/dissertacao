package controle.agente.sensor;

import jade.core.Agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.apache.http.client.fluent.Request;

import util.Ambiente;
import util.Erro;
import controle.agente.DiretorioAgenteJadeUtil;
import controle.agente.comportamento.PublicarEventoPrimitivo;
import controle.dominio.identificador.IdentificadorAtributoElementoGerenciado;
import controle.dominio.identificador.IdentificadorElementoGerenciado;
import controle.evento.EventoNumeroRequisicoesSimultaneas;

/**
 * S2: Agente sensor destinado a obter o número de requisições em execu;áo
 * simultaneamente no balanceador de carga.
 */
public class AgenteSensorBalanceador extends Agent {

	private static final long serialVersionUID = 3320202385578532920L;

	/**
	 * Comportamento do agente, destinado a publicar instâncias do evento
	 * primitivo {@link EventoNumeroRequisicoesSimultaneas} a cada 1s. Executa
	 * uma conexão ao load balancer, coleta as estatísticas e retorna a
	 * quantidade atual de requisições simultâneas
	 */
	public class PublicarEventoComNumeroRequisicoesSimultaneas extends
			PublicarEventoPrimitivo<EventoNumeroRequisicoesSimultaneas> {

		private static final long serialVersionUID = 1200706390207965224L;

		@Override
		public EventoNumeroRequisicoesSimultaneas coletarEvento() {

			Long result = null;
			String line = null;
			StringTokenizer st = null;
			BufferedReader in = null;

			try {

				in = new BufferedReader(new InputStreamReader(Request
						.Get(Ambiente
								.getAtributoAmbiente("elemento.gerenciado."
										+ super.getNomeElementoGerenciado()
										+ ".host")).execute().returnContent()
						.asStream()));

				linhas: while ((line = in.readLine()) != null) {
					st = new StringTokenizer(line, ",");
					String token = null;
					while (st.hasMoreTokens()) {
						token = st.nextToken();
						if ("FRONTEND".equals(token)) {
							result = Long.valueOf(st.nextToken());
							break linhas;
						}
					}
				}

			} catch (IOException e) {
				Erro.registrar(e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						Erro.registrar(e);
					}
				}
			}

			return new EventoNumeroRequisicoesSimultaneas(
					IdentificadorElementoGerenciado.BALANCEADOR,
					IdentificadorAtributoElementoGerenciado.NUMERO_REQUISICOES_SIMULTANEAS,
					result);
		}

		@Override
		public long getIntervaloExecucaoMilisegundos() {
			return 1000;
		}
	}

	@Override
	protected void setup() {
		super.setup();
		PublicarEventoComNumeroRequisicoesSimultaneas comportamento = new PublicarEventoComNumeroRequisicoesSimultaneas();
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
