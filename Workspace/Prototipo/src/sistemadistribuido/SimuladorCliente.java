package sistemadistribuido;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import util.Ambiente;
import util.Log;

/**
 * Simula um caso de teste de um cliente da aplicação executor consultas
 * executando dois volumes distintos de consultas
 *
 */
public class SimuladorCliente {

	public static class Cliente implements Runnable, ExceptionListener {

		private boolean pararDepoisDeUmMinuto = false;

		public Cliente(boolean pararDepoisDeUmMinuto) {
			this.pararDepoisDeUmMinuto = pararDepoisDeUmMinuto;
		}

		/**
		 * Executa uma requisição de consulta no load balancer
		 */
		@Override
		public void run() {

			Socket socket = null;
			BufferedReader br = null;

			long agora = System.currentTimeMillis();

			while (!Thread.currentThread().isInterrupted()) {

				try {

					long tempo = System.currentTimeMillis();

					socket = new Socket(Ambiente.getHostSocket(), Ambiente.getPortaSocket());
					br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

					DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
					BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

					outToServer.writeBytes("TEST\n");
					String sentence = inFromServer.readLine();
					System.out.println(sentence + " -> " + (System.currentTimeMillis() - tempo) + " ms");

					if (pararDepoisDeUmMinuto && System.currentTimeMillis() > (agora + 60000)) {
						System.out.println(new Date() + " -> parando... ");
						Thread.sleep(60000);
						agora = System.currentTimeMillis();
						System.out.println(new Date() + " -> iniciando... ");
					}

				} catch (IOException | InterruptedException e) {
					Log.registrar(e);
				} finally {
					try {
						if (br != null) {
							br.close();
						}
						if (socket != null) {
							socket.close();
						}
					} catch (IOException e) {
						Log.registrar(e);
					}
				}
			}

		}

		public synchronized void onException(JMSException e) {
			Log.registrar(e);
		}
	}

	public static void main(String args[]) throws Exception {

		Cliente cliente01 = new Cliente(false);
		Thread brokerThread01 = new Thread(cliente01);
		brokerThread01.setDaemon(false);
		brokerThread01.start();

		Cliente cliente02 = new Cliente(true);
		Thread brokerThread02 = new Thread(cliente02);
		brokerThread02.setDaemon(false);
		brokerThread02.start();

		System.out.println("Simulador do cliente iniciado!");

	}

}
