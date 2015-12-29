package sistemadistribuido.consumidor;

import java.lang.management.ManagementFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.apache.activemq.ActiveMQConnectionFactory;

import sistemadistribuido.consumidor.conector.ConectorAtivacao;
import sistemadistribuido.consumidor.conector.ConectorAtivacaoImpl;
import util.Ambiente;
import util.Ambiente.AtributoFila;
import util.Log;

/**
 * Aplicação consumidora da fila de mensagens.
 */
public class Consumidor {

	/**
	 * Implementação de consumidor da fila de mensagens criada para simular
	 * cenário de negócio.
	 */
	public static class ConsumidorFila implements Runnable, ExceptionListener {

		private Connection conexao = null;
		private Session sessao = null;
		private MessageConsumer consumidor = null;
		private ConectorAtivacao conector = null;

		/**
		 * Abre conexão com a fila de mensagens
		 */
		private void conectarFila() {

			try {

				if (this.conexao == null) {
					ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
							Ambiente.getAtributo(AtributoFila.JMS_BROKER_URL.getValue()));
					this.conexao = connectionFactory.createConnection();
					this.conexao.start();
					this.conexao.setExceptionListener(this);
				}

				if (sessao == null) {
					this.sessao = this.conexao.createSession(false, Session.AUTO_ACKNOWLEDGE);
				}

				if (this.consumidor == null) {
					Destination destination = this.sessao
							.createQueue(Ambiente.getAtributo(AtributoFila.JMS_BROKER_QUEUE_NAME.getValue()));
					this.consumidor = this.sessao.createConsumer(destination);
				}
			} catch (Exception e) {
				Log.registrar(e);
				desconectarFila();
			}
		}

		/**
		 * Fecha conexão com a fila de mensagem
		 */
		private void desconectarFila() {

			try {

				if (this.consumidor != null) {
					this.consumidor.close();
					this.consumidor = null;
				}
				if (this.sessao != null) {
					this.sessao.close();
					this.sessao = null;
				}
				if (this.conexao != null) {
					this.conexao.close();
					this.conexao = null;
				}
			} catch (Exception e) {
				Log.registrar(e);
			}
		}

		/**
		 * Consome a fila de mensagens com delay que varia de 1 a 1.5s entre
		 * mensagens, simulando o cenário de negócio. A fila é consumida se o
		 * respectivo conector indicar que a está ativo.
		 */
		public void run() {

			try {

				// Random intervaloMilisegundos = new Random();

				while (!Thread.currentThread().isInterrupted()) {

					if (this.conector.isAtivo()) {
						conectarFila();
					} else {
						desconectarFila();
						continue;
					}

					// long momentoInicio = System.currentTimeMillis();
					// Thread.sleep(500 + intervaloMilisegundos.nextInt(1000));
					Thread.sleep(1000);

					// recupera mensagem da fila
					Message mensagem = this.consumidor.receive(1000);
					if (mensagem != null) {
						if (mensagem instanceof TextMessage) {
							// System.out.println("[" +
							// (System.currentTimeMillis() - momentoInicio) +
							// "ms] Recebido: " + ((TextMessage) mensagem)
							// .getText());
						}
					}
				}
			} catch (Exception e) {
				Log.registrar(e);
				desconectarFila();
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
	private static ConectorAtivacao registrarConector(String nomeInstancia, boolean instanciaAtiva) {

		ConectorAtivacaoImpl conector = null;

		try {

			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			conector = new ConectorAtivacaoImpl(instanciaAtiva);
			StandardMBean mbean = new StandardMBean(conector, ConectorAtivacao.class);
			mbs.registerMBean(mbean, getJMXObjectName(nomeInstancia));

		} catch (Exception e) {
			Log.registrar(e);
		}
		return conector;
	}

	public static ObjectName getJMXObjectName(String nomeInstancia) throws MalformedObjectNameException {
		return new ObjectName(Consumidor.class.getSimpleName() + ":name=" + nomeInstancia);
	}

	/**
	 * Torna executável a aplicação. Informar a varíavel de ambiente
	 * {@code ExecutorConsulta.nomeInstancia} para identificar o nome da
	 * instância.
	 */
	public static void main(String[] args) {

		ConsumidorFila consumidor = new ConsumidorFila();
		consumidor.conector = registrarConector(Ambiente.getNomeInstancia(), Ambiente.isInstanciaAtiva());
		Thread brokerThread = new Thread(consumidor);
		brokerThread.setDaemon(false);
		brokerThread.start();

		System.out.println("Executor " + consumidor.conector.getNomeInstancia() + " iniciado!");
	}

}