package sistemadistribuido.executorconsulta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import sistemadistribuido.executorconsulta.conector.ConectorAtivacao;
import sistemadistribuido.executorconsulta.conector.ConectorAtivacaoImpl;
import util.Ambiente;
import util.Erro;
import controle.dominio.identificador.IdentificadorElementoGerenciado;

/**
 * Classe criada para emular o cenário de negócio para validação do controle.
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
				@SuppressWarnings("unused")
				String line = null;
				loop: for (; (line = br.readLine()) != null;) {
					break loop;
				}

				// simula tempo de processamento variável, entre 90ms e 1s
				Thread.sleep(900 + (new Random()).nextInt(100));

				// System.out.println(">> " + line);

				socket.getOutputStream().write("OK\n".getBytes());
			} catch (Exception e) {
				Erro.registrar(e);
			} finally {
				try {
					socket.close();
				} catch (Exception e) {
					Erro.registrar(e);
				}
			}
		}

	}

	/**
	 * Emula o elemento gerenciado "ExecutorConsulta", para simulação de cenário
	 * de negócio. Inicia e Interrompe o socket conforme conector.
	 */
	public static class ExecutorConsulta implements Runnable {

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
								Ambiente.getPortaSocketParametro());
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
				Erro.registrar(e);
			} finally {
				try {
					if (serverSocket != null) {
						serverSocket.close();
					}
				} catch (Exception e) {
					Erro.registrar(e);
				}
			}
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
			Erro.registrar(e);
		}
		return conector;
	}

	public static ObjectName getJMXObjectName(String nomeInstancia)
			throws MalformedObjectNameException {
		return new ObjectName(Emulador.class.getSimpleName() + ":name="
				+ nomeInstancia);
	}

	/**
	 * Torna executável a aplicação. Na iniciaização, deve ser informada a
	 * varíavel de ambiente {@code nomeInstancia}, com um valor de
	 * {@link IdentificadorElementoGerenciado} para permitir a identificação dos
	 * eventos gerados.
	 */
	public static void main(String[] args) {

		ExecutorConsulta consumidor = new ExecutorConsulta();
		consumidor.conector = registrarConector(
				Ambiente.getNomeInstanciaParametro(),
				Ambiente.isInstanciaAtivaParametro());
		Thread brokerThread = new Thread(consumidor);
		brokerThread.setDaemon(false);
		brokerThread.start();

		System.out.println(consumidor.conector.getNomeInstancia()
				+ " iniciado!");
	}

}
