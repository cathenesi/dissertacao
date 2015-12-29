package util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;

import sistemadistribuido.consumidor.Consumidor;
import sistemadistribuido.consumidor.conector.ConectorAtivacao;
import util.Ambiente.AtributoFila;

/**
 * Utilitario para acessar componentes via JMX.
 */
public class JMXUtil {

	/**
	 * Lista de métodos do broker da fila.
	 */
	public enum MetodoFila {

		CONSUMER_COUNT, QUEUE_SIZE, SEND_MESSAGE, PURGE;

		public Object executar(QueueViewMBean mBean, String param) throws Exception {

			switch (this) {
			case CONSUMER_COUNT:
				return mBean.getConsumerCount();
			case QUEUE_SIZE:
				return mBean.getQueueSize();
			case SEND_MESSAGE:
				mBean.sendTextMessage(param);
				break;
			case PURGE:
				mBean.purge();
				break;
			}
			return null;
		}
	}

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
	 * Invoca um método do broker da fila.
	 */
	public Object invocarMetodoFila(MetodoFila metodo, String param) {

		JMXConnector jmxc = null;
		try {
			JMXServiceURL url = new JMXServiceURL(Ambiente.getAtributo(AtributoFila.JMS_BROKER_JMX_HOST.getValue()));
			Map<String, Object> env = new HashMap<String, Object>();
			env.put(JMXConnector.CREDENTIALS,
					new String[] { Ambiente.getAtributo(AtributoFila.JMS_BROKER_USER.getValue()),
							Ambiente.getAtributo(AtributoFila.JMS_BROKER_PASSWORD.getValue()) });

			jmxc = JMXConnectorFactory.connect(url, env);
			jmxc.connect();
			MBeanServerConnection connection = jmxc.getMBeanServerConnection();
			ObjectName name = new ObjectName(Ambiente.getAtributo(AtributoFila.JMS_BROKER_NAME.getValue()));
			BrokerViewMBean brokerMbean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection,
					name, BrokerViewMBean.class, true);

			for (ObjectName queueObjectName : brokerMbean.getQueues()) {
				QueueViewMBean queueMbean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection,
						queueObjectName, QueueViewMBean.class, true);
				if (Ambiente.getAtributo(AtributoFila.JMS_BROKER_QUEUE_NAME.getValue()).equals(queueMbean.getName())) {
					return metodo.executar(queueMbean, param);
				}
			}
			throw new IllegalArgumentException(Ambiente.getAtributo(AtributoFila.JMS_BROKER_NAME.getValue()));
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			Log.registrar(e);
			e.printStackTrace();
		} finally {
			try {
				jmxc.close();
			} catch (IOException e) {
				Log.registrar(e);
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Invoca um método de uma instância de um consumidor de fila.
	 */
	public Object invocarMetodoInstanciaExecutorConsulta(MetodoInstancia metodo, String nomeInstancia) {

		JMXConnector jmxc = null;
		try {
			JMXServiceURL url = new JMXServiceURL(
					Ambiente.getAtributo("elemento.monitorado." + nomeInstancia + ".jmx.host"));

			jmxc = JMXConnectorFactory.connect(url);
			jmxc.connect();
			MBeanServerConnection connection = jmxc.getMBeanServerConnection();
			ConectorAtivacao mBean = (ConectorAtivacao) MBeanServerInvocationHandler.newProxyInstance(connection,
					Consumidor.getJMXObjectName(nomeInstancia), ConectorAtivacao.class, true);
			return metodo.executar(mBean);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			Log.registrar(e);
			e.printStackTrace();
		} finally {
			try {
				jmxc.close();
			} catch (IOException e) {
				Log.registrar(e);
				e.printStackTrace();
			}
		}
		return null;
	}

}
