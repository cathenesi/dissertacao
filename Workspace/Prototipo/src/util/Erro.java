package util;

/**
 * Utilitário para registro de erros
 */
public class Erro {

	/**
	 * Registra a exceção no próprio console.
	 */
	public static void registrar(Exception e) {
		System.out.println("Instancia: " + Ambiente.getNomeInstanciaParametro());
		System.out.println("Excecao: " + e);
		e.printStackTrace();
	}
}
