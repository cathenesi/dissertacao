package controle.dominio.identificador;

/**
 * Lista os elementos gerenciados.
 */
public enum IdentificadorElementoGerenciado {

	/**
	 * Identifica a o balanceador de carga.
	 */
	BALANCEADOR,
	/**
	 * Identifica a instância n.1 do servidor
	 */
	SERVIDOR_INSTANCIA_1,
	/**
	 * Identifica a instância n.2 do servidor
	 */
	SERVIDOR_INSTANCIA_2;

	/**
	 * Retorna identificador pela sua representação em String.
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
