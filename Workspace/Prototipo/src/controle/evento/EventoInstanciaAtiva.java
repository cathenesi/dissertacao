package controle.evento;

import controle.dominio.identificador.IdentificadorAtributoElementoGerenciado;
import controle.dominio.identificador.IdentificadorElementoGerenciado;

/**
 * Evento destinado a indicar que uma instância do servidor {@link Emulador}
 * está ativa.
 */
public class EventoInstanciaAtiva extends EventoPrimitivo {

	private static final long serialVersionUID = 8093831685175655276L;

	public EventoInstanciaAtiva() {
	}

	/**
	 * Cria o evento, passando por parametro qual elemento gerou o evento, e
	 * qual o nome do atributo a que se refere.
	 * 
	 * @param identificadorElementoGerenciado
	 * @param identificadorAtributoElementoGerenciado
	 */
	public EventoInstanciaAtiva(
			IdentificadorElementoGerenciado identificadorElementoGerenciado,
			IdentificadorAtributoElementoGerenciado identificadorAtributoElementoGerenciado) {
		super(identificadorElementoGerenciado,
				identificadorAtributoElementoGerenciado);
	}

}