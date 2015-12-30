package sistemadistribuido.servidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import sistemadistribuido.servidor.conector.ConectorAtivacao;
import sistemadistribuido.servidor.conector.ConectorAtivacaoImpl;
import util.Ambiente;
import util.Log;

/**
 * 
 * @author carlos.athenesi
 *
 */
public class Emulador {

	/**
	 * Trata uma requisição recebida. A implementação apenas lê um socket aberto
	 * e responde a string "OK" após um delay, que simula o processamento.
	 */
	public static class Requisicao implements Runnable {

		private Socket socket;

		public Requisicao(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {

				BufferedReader br = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				String line = null;
				loop: for (; (line = br.readLine()) != null;) {
					break loop;
				}

				// simula tempo de processamento variável, até 1s
				Thread.sleep(100 + (new Random()).nextInt(1000));

				System.out.println(">> " + line);

				socket.getOutputStream().write("OK\n".getBytes());
			} catch (Exception e) {
				Log.registrar(e);
			} finally {
				try {
					socket.close();
				} catch (Exception e) {
					Log.registrar(e);
				}
			}
		}

	}

	/**
	 * Emula o elemento gerenciado "ExecutorConsulta", para simulação de cenário
	 * de negócio. Inicia e Interrompe o socket conforme conector.
	 */
	public static class ExecutorConsulta implements Runnable, ExceptionListener {

		private ConectorAtivacao conector = null;

		/**
		 * Lê o socket, se o respectivo conector indicar que a está ativo.
		 */
		@Override
		public void run() {

			ServerSocket serverSocket = null;
			try {

				ExecutorService pool = Executors.newFixedThreadPool(10);

				while (!Thread.currentThread().isInterrupted()) {

					if (this.conector.isAtivo() && serverSocket == null) {
						serverSocket = new ServerSocket(
								Ambiente.getPortaSocket());
						System.out.println("iniciando conexoes....");
					} else if (!this.conector.isAtivo() && serverSocket != null) {
						serverSocket.close();
						serverSocket = null;
						System.out.println("encerrando conexoes....");
					} else if (this.conector.isAtivo() && serverSocket != null) {
						Socket socket = serverSocket.accept();
						Requisicao req = new Requisicao(socket);
						pool.execute(req);
					}
				}
			} catch (Exception e) {
				Log.registrar(e);
			} finally {
				try {
					if (serverSocket != null) {
						serverSocket.close();
					}
				} catch (Exception e) {
					Log.registrar(e);
				}
			}
		}

		public synchronized void onException(JMSException e) {
			Log.registrar(e);
		}
	}

	/**
	 * Registra um conector {@link ConectorAtivacaoImpl} para uma instância da
	 * aplicação para obter informações em tempo de execução via protocolo JMX
	 */
	private static ConectorAtivacao registrarConector(String nomeInstancia,
			boolean instanciaAtiva) {

		ConectorAtivacao conector = null;

		try {

			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			conector = new ConectorAtivacaoImpl(instanciaAtiva);
			StandardMBean mbean = new StandardMBean(conector,
					ConectorAtivacao.class);
			mbs.registerMBean(mbean, getJMXObjectName(nomeInstancia));

		} catch (Exception e) {
			Log.registrar(e);
		}
		return conector;
	}

	public static ObjectName getJMXObjectName(String nomeInstancia)
			throws MalformedObjectNameException {
		return new ObjectName(Emulador.class.getSimpleName() + ":name="
				+ nomeInstancia);
	}

	/**
	 * Torna executável a aplicação. Informar a varíavel de ambiente
	 * {@code ExecutorConsulta.nomeInstancia} para identificar o nome da
	 * instância.
	 */
	public static void main(String[] args) {

		ExecutorConsulta consumidor = new ExecutorConsulta();
		consumidor.conector = registrarConector(Ambiente.getNomeInstancia(),
				Ambiente.isInstanciaAtiva());
		Thread brokerThread = new Thread(consumidor);
		brokerThread.setDaemon(false);
		brokerThread.start();

		System.out.println(consumidor.conector.getNomeInstancia()
				+ " iniciado!");
	}

}
