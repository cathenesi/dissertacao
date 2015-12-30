package util;

import java.util.Properties;

/**
 * Utilitário para varíaveis de ambiente da aplicação
 */
public class Ambiente {

	private static Properties atributosAmbiente = new Properties();

	static {
		try {
			atributosAmbiente.load(Ambiente.class.getClassLoader().getResourceAsStream("ambiente.properties"));
		} catch (Exception e) {
			Log.registrar(e);
		}
	}

	public enum AtributoFila {

		JMS_BROKER_URL("elemento.monitorado.FILA_MENSAGEM.jms.broker.url"), JMS_BROKER_JMX_HOST(
				"elemento.monitorado.FILA_MENSAGEM.jmx.host"), JMS_BROKER_USER(
						"elemento.monitorado.FILA_MENSAGEM.jms.broker.user"), JMS_BROKER_PASSWORD(
								"elemento.monitorado.FILA_MENSAGEM.jms.broker.password"), JMS_BROKER_NAME(
										"elemento.monitorado.FILA_MENSAGEM.jmx.name"), JMS_BROKER_QUEUE_NAME(
												"elemento.monitorado.FILA_MENSAGEM.jms.broker.queue.name");

		private String value;

		private AtributoFila(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	/**
	 * Retorna o nome de um atributo do ambiente
	 */
	public static String getAtributo(String nome) {
		return atributosAmbiente.getProperty(nome);
	}

	/**
	 * Obtém o nome parametrizado para a instância
	 */
	public static String getNomeInstancia() {
		return System.getProperty("nomeInstancia");
	}

	/**
	 * Indica se a instância deve ser inicializada ativa, executando, ou deve
	 * ficar em espera
	 */
	public static Boolean isInstanciaAtiva() {
		return Boolean.valueOf(System.getProperty("instanciaAtiva"));
	}

	/**
	 * Retorna o número da porta configurada para inicializar o socket
	 */
	public static Integer getPortaSocket() {
		return Integer.valueOf(System.getProperty("portaSocket"));
	}

	/**
	 * Retorna o nome do host - usado pelo cliente
	 */
	public static String getHostSocket() {
		return System.getProperty("hostSocket");
	}

}
