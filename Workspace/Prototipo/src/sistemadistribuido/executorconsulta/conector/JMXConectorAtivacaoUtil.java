package sistemadistribuido.executorconsulta.conector;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import sistemadistribuido.executorconsulta.Emulador;
import util.Ambiente;
import util.Erro;

/**
 * Utilitario para acessar os conectores de ativação via JMX.
 */
public class JMXConectorAtivacaoUtil {

	/**
	 * Lista de métodos das instâncias.
	 */
	public enum MetodoInstancia {

		IS_ATIVO, INATIVAR, ATIVAR;

		public Object executar(ConectorAtivacao mBean) throws Exception {

			switch (this) {
			case IS_ATIVO:
				return mBean.isAtivo();
			case INATIVAR:
				mBean.inativar();
				break;
			case ATIVAR:
				mBean.ativar();
				break;
			}
			return null;
		}
	}

	/**
	 * Invoca um método de uma instância de um consumidor de fila.
	 */
	public Object invocarMetodoInstanciaExecutorConsulta(
			MetodoInstancia metodo, String nomeInstancia) {

		JMXConnector jmxc = null;
		try {
			JMXServiceURL url = new JMXServiceURL(
					Ambiente.getAtributoAmbiente("elemento.gerenciado."
							+ nomeInstancia + ".jmx.host"));

			jmxc = JMXConnectorFactory.connect(url);
			jmxc.connect();
			MBeanServerConnection connection = jmxc.getMBeanServerConnection();
			ConectorAtivacao mBean = (ConectorAtivacao) MBeanServerInvocationHandler
					.newProxyInstance(connection,
							Emulador.getJMXObjectName(nomeInstancia),
							ConectorAtivacao.class, true);
			return metodo.executar(mBean);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			Erro.registrar(e);
			e.printStackTrace();
		} finally {
			try {
				jmxc.close();
			} catch (IOException e) {
				Erro.registrar(e);
				e.printStackTrace();
			}
		}
		return null;
	}

}
