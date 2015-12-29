package controle.dominio;

/**
 * Artefatos de hardware que compõe o sistema distribuído.
 * 
 * Populado via arquivo {@link dominio.xml}.
 */
public class Computador extends ElementoGerenciado {

	private static final long serialVersionUID = -1214878254388302509L;

	private Localidade localidade;

	public Localidade getLocalidade() {
		return localidade;
	}

	public void setLocalidade(Localidade localidade) {
		this.localidade = localidade;
	}

}
