package util;

import java.util.Properties;

/**
 * Utilitário para varíaveis de ambiente da aplicação
 */
public class Ambiente {

	private static Properties ATRIBUTOS = new Properties();

	static {
		try {
			ATRIBUTOS.load(Ambiente.class.getClassLoader().getResourceAsStream("ambiente.properties"));
		} catch (Exception e) {
			Log.registrar(e);
		}
	}

	/**
	 * Retorna o nome de um atributo do ambiente
	 */
	public static String getAtributoAmbiente(String nome) {
		return ATRIBUTOS.getProperty(nome);
	}

	/**
	 * Obtém o nome parametrizado para a instância
	 */
	public static String getNomeInstanciaParametro() {
		return System.getProperty("nomeInstancia");
	}

	/**
	 * Indica se a instância deve ser inicializada ativa, executando, ou deve
	 * ficar em espera
	 */
	public static Boolean isInstanciaAtivaParametro() {
		return Boolean.valueOf(System.getProperty("instanciaAtiva"));
	}

	/**
	 * Retorna o número da porta configurada para inicializar o socket
	 */
	public static Integer getPortaSocketParametro() {
		return Integer.valueOf(System.getProperty("portaSocket"));
	}

	/**
	 * Retorna o nome do host - usado pelo cliente
	 */
	public static String getHostSocketParametro() {
		return System.getProperty("hostSocket");
	}

}
