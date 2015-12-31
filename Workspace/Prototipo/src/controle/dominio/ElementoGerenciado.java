package controle.dominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import controle.dominio.identificador.IdentificadorElementoGerenciado;

/**
 * Indica um artefato de hardware ou de software que compõe o sistema
 * distribuído. Superclasse de {@link Computador} e de {@link Programa}.
 * 
 * Populado, no momento da inicialiação do controle, usando o conteúdo do
 * arquivo {@link dominio.xml}.
 */
public abstract class ElementoGerenciado implements Serializable {

	private static final long serialVersionUID = -4316744938658315217L;

	private IdentificadorElementoGerenciado identificador;
	private String descricao;
	private List<Atributo> atributos = new ArrayList<>();

	public IdentificadorElementoGerenciado getIdentificador() {
		return identificador;
	}

	public void setIdentificador(IdentificadorElementoGerenciado identificador) {
		this.identificador = identificador;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Atributo> getAtributos() {
		return atributos;
	}

	public void setAtributos(List<Atributo> atributos) {
		this.atributos = atributos;
	}

	@Override
	public String toString() {
		return "identificador=" + this.identificador;
	}
}
