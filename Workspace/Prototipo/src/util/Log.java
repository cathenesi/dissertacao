package util;

/**
 * Log de erros
 */
public class Log {

	/**
	 * Registra a exceção no próprio console
	 */
	public static void registrar(Exception e) {
		System.out.println("Instancia: " + Ambiente.getNomeInstancia());
		System.out.println("Excecao: " + e);
		e.printStackTrace();
	}
}
