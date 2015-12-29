package controle.dominio;

import java.io.Serializable;

/**
 * Indica o local físico onde um computador está localizado.
 * 
 * Populado via arquivo {@link dominio.xml}.
 */
public class Localidade implements Serializable {

	private static final long serialVersionUID = -2360710198518220491L;

	private String nome;

	public Localidade(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

}
