package sistemadistribuido;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;

import util.Ambiente;
import util.Erro;

/**
 * Simula um caso de teste de um cliente da aplicação executor consultas
 * executando dois volumes distintos de consultas
 *
 */
public class SimuladorCliente {

	public static class Cliente implements Runnable {

		private boolean pararDepoisDeUmMinuto = false;

		public Cliente(boolean pararDepoisDeUmMinuto) {
			this.pararDepoisDeUmMinuto = pararDepoisDeUmMinuto;
		}

		/**
		 * Executa uma requisição de consulta no load balancer
		 */
		@SuppressWarnings("unused")
		@Override
		public void run() {

			Socket socket = null;
			BufferedReader br = null;

			long agora = System.currentTimeMillis();

			while (!Thread.currentThread().isInterrupted()) {

				try {

					long tempo = System.currentTimeMillis();

					socket = new Socket(Ambiente.getHostSocketParametro(),
							Ambiente.getPortaSocketParametro());
					br = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));

					DataOutputStream outToServer = new DataOutputStream(
							socket.getOutputStream());
					BufferedReader inFromServer = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));

					outToServer.writeBytes("TEST\n");
					String sentence = inFromServer.readLine();
					// System.out.println(sentence + " -> "
					// + (System.currentTimeMillis() - tempo) + " ms");

					if (pararDepoisDeUmMinuto
							&& System.currentTimeMillis() > (agora + 60000)) {
						System.out.println(new Date() + " -> parando... ");
						Thread.sleep(60000);
						agora = System.currentTimeMillis();
						System.out.println(new Date() + " -> reiniciando... ");
					}

				} catch (IOException | InterruptedException e) {
					Erro.registrar(e);
				} finally {
					try {
						if (br != null) {
							br.close();
						}
						if (socket != null) {
							socket.close();
						}
					} catch (IOException e) {
						Erro.registrar(e);
					}
				}
			}

		}

	}

	/**
	 * Inicializa lotes de 8 clientes, alternando a configuração de forma a
	 * criar dois volumes: um abaixo de 10 requisicoes simultaenas e outro
	 * acima, para testar a execução do controle
	 */
	public static void main(String args[]) throws Exception {

		System.out.println("Simulador do cliente iniciado!");

		boolean pararClienteAposUmMinuto = false;
		for (int i = 0; i < 2; i++) {

			for (int c = 0; c < 8; c++) {
				Thread brokerThread = new Thread(new Cliente(
						pararClienteAposUmMinuto));
				brokerThread.setDaemon(false);
				brokerThread.start();

			}

			// aguarda um minuto para inicializar os demais clientes
			Thread.sleep(60000);

			pararClienteAposUmMinuto = !pararClienteAposUmMinuto;
		}

	}

}
