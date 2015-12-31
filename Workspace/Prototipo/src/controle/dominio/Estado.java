package controle.dominio;

/**
 * Aponta o estado atual de um atributo, determinado pelas regras descritas no
 * arquivo {@link regrasAtualizacao.drl}.
 */
public enum Estado {

	/**
	 * Indica que o elemento gerenciado ao qual o atributo pertence não está
	 * operante.
	 */
	INATIVO,
	/**
	 * Indica que o valor atual do atributo está dentro da faixa de valores
	 * estabelecida, indicando bom funcionamento.
	 */
	NORMAL,
	/**
	 * Indica que o valor atual do atributo está fora da faixa de valores
	 * estabelecida, mas ainda dentro de limites aceitáveis.
	 */
	ALERTA,
	/**
	 * Indica que o valor atual do atributo está muito fora da faixa de valores
	 * estabelecida, podendo ocasionar mau funcionamento do sistema.
	 */
	CRITICO;

}
