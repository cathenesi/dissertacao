package controle.dominio;

/**
 * Lista os elementos gerenciados.
 */
public enum IdentificadorElementoGerenciado {

	/**
	 * Identifica a fila de mensagens.
	 */
	FILA_MENSAGEM,
	/**
	 * Identifica a instância n.1 do consumidor
	 */
	CONSUMIDOR_INSTANCIA_1,
	/**
	 * Identifica a instância n.2 do consumidor
	 */
	CONSUMIDOR_INSTANCIA_2;

	/**
	 * Retorna identificador pela sua representa;'ao em String.
	 */
	public static IdentificadorElementoGerenciado getByName(String name) {

		for (IdentificadorElementoGerenciado identificador : values()) {
			if (identificador.toString().equals(name)) {
				return identificador;
			}
		}
		return null;
	}

}
