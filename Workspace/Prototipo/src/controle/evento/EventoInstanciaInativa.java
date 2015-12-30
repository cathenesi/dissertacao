package controle.evento;

import controle.dominio.identificador.IdentificadorAtributoElementoGerenciado;
import controle.dominio.identificador.IdentificadorElementoGerenciado;

/**
 * Evento destinado a indicar que uma instância do servidor {@link Emulador} está inativa.
 */
public class EventoInstanciaInativa extends EventoPrimitivo {

	private static final long serialVersionUID = 8093831685175655276L;

	public EventoInstanciaInativa() {
	}

	public EventoInstanciaInativa(IdentificadorElementoGerenciado identificadorElementoGerenciado,
			IdentificadorAtributoElementoGerenciado identificadorAtributoElementoGerenciado) {
		super(identificadorElementoGerenciado, identificadorAtributoElementoGerenciado);
	}

}