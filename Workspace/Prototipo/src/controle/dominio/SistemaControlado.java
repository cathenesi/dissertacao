package controle.dominio;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Agrega um conjunto de programas e computadores que formam o conjunto de
 * elementos gerenciados.
 * 
 * Populado, no momento da inicialiação do controle, usando o conteúdo do
 * arquivo {@link dominio.xml}.
 */
public class SistemaControlado implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Computador> computadores = new ArrayList<Computador>();
	private List<Programa> programas = new ArrayList<Programa>();

	public List<Computador> getComputadores() {
		return computadores;
	}

	public void setComputadores(List<Computador> computadores) {
		this.computadores = computadores;
	}

	public List<Programa> getProgramas() {
		return programas;
	}

	public void setProgramas(List<Programa> programas) {
		this.programas = programas;
	}

	/**
	 * Valida consistência da configuração.
	 */
	public boolean validar() throws IllegalArgumentException {

		List<String> identificadores = new ArrayList<>();

		if (this.computadores != null && this.computadores.size() > 0) {
			for (Computador computador : this.computadores) {
				if (computador.getAtributos() != null
						&& computador.getAtributos().size() > 0) {
					for (Atributo atributo : computador.getAtributos()) {
						atributo.setComputador(computador);
						if (computador.getIdentificador() == null
								|| atributo.getIdentificador() == null) {
							throw new IllegalArgumentException(
									"Identificador do computador ou do atributo nao pode ser nulo: "
											+ "computador.identificador="
											+ computador.getIdentificador()
											+ "atributo.identificador="
											+ atributo.getIdentificador());
						}
						if (identificadores.contains(computador
								.getIdentificador()
								+ "|"
								+ atributo.getIdentificador())) {
							throw new IllegalArgumentException("Identificador "
									+ atributo.getIdentificador()
									+ " duplicado!");
						} else {
							identificadores.add(computador.getIdentificador()
									+ "|" + atributo.getIdentificador());
						}
					}
				}
			}
		}

		if (this.programas != null && this.programas.size() > 0) {
			for (Programa programa : this.programas) {
				if (programa.getAtributos() != null
						&& programa.getAtributos().size() > 0) {
					for (Atributo atributo : programa.getAtributos()) {
						atributo.setPrograma(programa);
						if (programa.getIdentificador() == null
								|| atributo.getIdentificador() == null) {
							throw new IllegalArgumentException(
									"Identificador do programa ou do atributo nao pode ser nulo: "
											+ "programa.identificador="
											+ programa.getIdentificador()
											+ "atributo.identificador="
											+ atributo.getIdentificador());
						}
						if (identificadores.contains(programa
								.getIdentificador()
								+ "|"
								+ atributo.getIdentificador())) {
							throw new IllegalArgumentException("Identificador "
									+ atributo.getIdentificador()
									+ " duplicado!");
						} else {
							identificadores.add(programa.getIdentificador()
									+ "|" + atributo.getIdentificador());
						}
					}
				}
			}
		}

		return true;
	}

	public List<Atributo> getListaAtributosGerenciados() {

		List<Atributo> atributos = new ArrayList<>();

		if (this.computadores != null && this.computadores.size() > 0) {
			for (Computador computador : this.computadores) {
				if (computador.getAtributos() != null
						&& computador.getAtributos().size() > 0) {
					atributos.addAll(computador.getAtributos());
				}
			}
		}

		if (this.programas != null && this.programas.size() > 0) {
			for (Programa programa : this.programas) {
				if (programa.getAtributos() != null
						&& programa.getAtributos().size() > 0) {
					atributos.addAll(programa.getAtributos());
				}
			}
		}

		return atributos;
	}

	/**
	 * Gera uma representação, em HTML, para verificação do funcionamento. O
	 * HTML gerado é atualizado, por uma thread do agente
	 * {@link AgenteInicializador}, no arquivo {@link controle.html}, que pode
	 * ser visualizado em um browser.
	 */
	@Override
	public String toString() {

		StringBuilder html = new StringBuilder();
		html.append("<html><title>Controle</title>");
		html.append("<head><meta http-equiv=\"refresh\" content=\"2\"></head>");
		html.append("<body><center>");
		html.append("Em: ").append(
				new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
		html.append("<table border=1><tr>");

		for (Programa programa : this.programas) {
			html.append("<td><b>");
			html.append(programa.getDescricao());
			for (Atributo atributo : programa.getAtributos()) {
				if (Estado.INATIVO.equals(atributo.getEstadoAtual())) {
					html.append("<font color=\"gray\">");
				} else if (Estado.NORMAL.equals(atributo.getEstadoAtual())) {
					html.append("<font color=\"green\">");
				} else if (Estado.ALERTA.equals(atributo.getEstadoAtual())) {
					html.append("<font color=\"orange\">");
				} else if (Estado.CRITICO.equals(atributo.getEstadoAtual())) {
					html.append("<font color=\"red\">");
				}
				html.append("<br/>").append(atributo.getDescricao())
						.append("=");
				if (atributo.getValorAtual() != null
						&& !"".equals(atributo.getValorAtual().trim())) {
					html.append(atributo.getValorAtual());
				} else {
					html.append(atributo.getEstadoAtual().toString());
				}
			}
			html.append("</font></b></td>");
		}
		html.append("</tr></table></center></body></html>");
		return html.toString();
	}

}
