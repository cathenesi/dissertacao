package sistemadistribuido.produtor;

import java.lang.management.ManagementFactory;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.apache.activemq.ActiveMQConnectionFactory;

import sistemadistribuido.consumidor.Consumidor;
import sistemadistribuido.consumidor.conector.ConectorAtivacaoImpl;
import sistemadistribuido.produtor.conector.ConectorProdutor;
import sistemadistribuido.produtor.conector.ConectorProdutorImpl;
import util.Ambiente;
import util.Ambiente.AtributoFila;
import util.Log;

/**
 * Aplicação produtora de mensagens.
 */
public class SimuladorCliente {

	/**
	 * Implementação de cliente para simular cenário de negócio.
	 */
	public static class ProdutorFila implements Runnable, ExceptionListener {

		// altera o intervalo a cada um minuto de execução
		private long tempoEntreMudancaEmMilisegundos = (1 * 60 * 1000);
		private long[] intervaloEntreMensagensEmMilisegundos = { 1000l, 500l, 250l, 500l };
		private Integer posIntervaloAtual = 0;
		private long ultimaMudanca = System.currentTimeMillis();
		private ConectorProdutor conector = null;

		public void reinicializar() {
			synchronized (posIntervaloAtual) {
				posIntervaloAtual = 0;
				ultimaMudanca = System.currentTimeMillis();
			}
		}

		/**
		 * Retorna o intervalo de tempo entre mensagens a ser utilizado na
		 * simulação do cenário de negócio. Este intervalo alterna de tempo em
		 * tempo para simular vários volumes.
		 */
		public long getIntervaloTempoAtualMilisegundos() {

			if ((ultimaMudanca + tempoEntreMudancaEmMilisegundos) < System.currentTimeMillis()) {
				posIntervaloAtual++;
				if (posIntervaloAtual >= intervaloEntreMensagensEmMilisegundos.length) {
					posIntervaloAtual = 0;
				}
				ultimaMudanca = System.currentTimeMillis();
			}

			return intervaloEntreMensagensEmMilisegundos[posIntervaloAtual];
		}

		/**
		 * Cria mensagens na fila com intervalos variaveis entre mensagens,
		 * simulando cenários de negócio com volumes distintos. O intervalo de
		 * tempo entre mensagens é modificado a cada 1 minuto de execução da
		 * aplicação.
		 */
		public void run() {

			Connection conexao = null;
			Session sessao = null;
			MessageProducer produtor = null;
			Long inicio = System.currentTimeMillis();

			try {

				ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
						Ambiente.getAtributo(AtributoFila.JMS_BROKER_URL.getValue()));
				conexao = connectionFactory.createConnection();
				conexao.start();
				sessao = conexao.createSession(false, Session.AUTO_ACKNOWLEDGE);

				Destination destination = sessao
						.createQueue(Ambiente.getAtributo(AtributoFila.JMS_BROKER_QUEUE_NAME.getValue()));
				produtor = sessao.createProducer(destination);
				produtor.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

				String textoMensagem = null;

				while (!Thread.currentThread().isInterrupted()) {

					if (!this.conector.isAtivo()) {
						continue;
					}

					// Thread.sleep(this.getIntervaloTempoAtualMilisegundos());
					// depois de um minuto e meio, para por dois minutos
					if (System.currentTimeMillis() > (inicio+30000)) {
						System.out.println("Simulador pausando...");						
						Thread.sleep(12000);
						inicio = System.currentTimeMillis();
						System.out.println("...simulador reiniciando!");						
					} else {
						Thread.sleep(500);						
					}

					textoMensagem = "Requisicao=" + UUID.randomUUID();
					produtor.send(sessao.createTextMessage(textoMensagem));
					// System.out.println("["
					// + (this.getIntervaloTempoAtualMilisegundos())
					// + "ms] Enviado: " + textoMensagem);
				}

			} catch (Exception e) {
				Log.registrar(e);
			} finally {
				try {
					produtor.close();
					sessao.close();
					conexao.close();
				} catch (Exception ex) {
					Log.registrar(ex);
				}
			}
		}

		public synchronized void onException(JMSException e) {
			Log.registrar(e);
		}

	}

	/**
	 * Registra um conector {@link ConectorAtivacaoImpl} para uma instância da
	 * aplicação
	 */
	private static ConectorProdutor registrarConector(String nomeInstancia, ProdutorFila produtor) {

		ConectorProdutorImpl conector = null;

		try {

			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName nome = new ObjectName(Consumidor.class.getSimpleName() + ":name=" + nomeInstancia);
			conector = new ConectorProdutorImpl(produtor);
			StandardMBean mbean = new StandardMBean(conector, ConectorProdutor.class);
			mbs.registerMBean(mbean, nome);

		} catch (Exception e) {
			Log.registrar(e);
		}
		return conector;
	}

	/**
	 * Torna executável a aplicação.
	 */
	public static void main(String[] args) {

		ProdutorFila produtor = new ProdutorFila();
		produtor.conector = registrarConector(Ambiente.getNomeInstancia(), produtor);
		Thread brokerThread = new Thread(produtor);
		brokerThread.setDaemon(false);
		brokerThread.start();

		System.out.println("Simulador " + produtor.conector.getNomeInstancia() + " iniciado!");
	}

}
