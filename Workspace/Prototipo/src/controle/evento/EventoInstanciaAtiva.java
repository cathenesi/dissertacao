package controle.evento;

import controle.dominio.identificador.IdentificadorAtributoElementoGerenciado;
import controle.dominio.identificador.IdentificadorElementoGerenciado;

/**
 * Evento destinado a indicar que uma instância do servidor {@link Emulador} está ativa.
 */
public class EventoInstanciaAtiva extends EventoPrimitivo {

	private static final long serialVersionUID = 8093831685175655276L;

	public EventoInstanciaAtiva() {
	}

	public EventoInstanciaAtiva(IdentificadorElementoGerenciado identificadorElementoGerenciado,
			IdentificadorAtributoElementoGerenciado identificadorAtributoElementoGerenciado) {
		super(identificadorElementoGerenciado, identificadorAtributoElementoGerenciado);
	}

}