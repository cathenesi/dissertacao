package controle.agente.controlador;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import controle.agente.DiretorioAgenteJadeUtil;
import controle.dominio.Atributo;
import controle.dominio.Computador;
import controle.dominio.Localidade;
import controle.dominio.Programa;
import controle.dominio.SistemaControlado;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.wrapper.AgentContainer;
import util.Erro;

/**
 * Agente responsável pela inicialização do controlador
 */
@SuppressWarnings("deprecation")
public class AgenteInicializador extends Agent {

	private static final long serialVersionUID = 273786892468632402L;

	private KnowledgeBase knowledgeBase = null;
	private StatefulKnowledgeSession session = null;
	private EntryPoint queueStream = null;

	/**
	 * Inicializa os componentes do controlador: base de regras do Drools,
	 * repositório de estados dos atributos dos elementos gerenciados, fluxo de
	 * eventos, agente Executor de Reconfigurações, agente Processador de
	 * Eventos
	 */
	public class Startup extends OneShotBehaviour {

		private static final long serialVersionUID = -1969910869557015465L;

		/**
		 * Este método é executado apenas uma vez
		 */
		@Override
		public void action() {

			try {

				KnowledgeBuilder builder = KnowledgeBuilderFactory
						.newKnowledgeBuilder();

				// 1) Inicializa a base de regras -----------------------------
				builder.add(ResourceFactory
						.newClassPathResource("regrasAtualizacao.drl"),
						ResourceType.DRL);
				builder.add(ResourceFactory
						.newClassPathResource("regrasReconfiguracao.drl"),
						ResourceType.DRL);
				if (builder.hasErrors()) {
					throw new RuntimeException(builder.getErrors().toString());
				}
				KieBaseConfiguration kBaseConfig = KieServices.Factory.get()
						.newKieBaseConfiguration();
				kBaseConfig.setOption(EventProcessingOption.STREAM);
				AgenteInicializador.this.knowledgeBase = KnowledgeBaseFactory
						.newKnowledgeBase(kBaseConfig);
				knowledgeBase.addKnowledgePackages(builder
						.getKnowledgePackages());

				KieSessionConfiguration sessionConfig = KieServices.Factory
						.get().newKieSessionConfiguration();
				sessionConfig.setOption(ClockTypeOption.get("realtime"));
				AgenteInicializador.this.session = knowledgeBase
						.newStatefulKnowledgeSession(sessionConfig, null);

				// 2) Inicializa o repositório de estados ---------------------
				XStream stream = new XStream(new DomDriver());
				stream.alias("SistemaControlado", SistemaControlado.class);
				stream.alias("Programa", Programa.class);
				stream.alias("Computador", Computador.class);
				stream.alias("Atributo", Atributo.class);
				stream.alias("Localidade", Localidade.class);
				try {
					SistemaControlado sistemaControlado = (SistemaControlado) stream
							.fromXML(ResourceFactory.newClassPathResource(
									"dominio.xml").getInputStream());
					sistemaControlado.validar();
					List<Atributo> atributos = sistemaControlado
							.getListaAtributosGerenciados();
					for (Atributo atributo : atributos) {
						AgenteInicializador.this.session.insert(atributo);
					}
					AgenteInicializador.this
							.gerarRepresentacao(sistemaControlado);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				// 3) Inicializa o fluxo de eventos ---------------------------
				AgenteInicializador.this.queueStream = AgenteInicializador.this.session
						.getEntryPoint("FluxoDeEventos");

				// 4) Inicializa o agente Executor de Reconfigurações ---------
				AgentContainer ac = super.myAgent.getContainerController();
				String agentName = DiretorioAgenteJadeUtil
						.getNomeAgente(AgenteControladorAtuacao.class);
				// A sessão Drools criada é passada ao agente
				ac.createNewAgent(agentName,
						AgenteControladorAtuacao.class.getName(),
						new Object[] { session });
				ac.getAgent(agentName).start();

				// 5) Inicializa o agente Processador de Eventos --------------
				agentName = DiretorioAgenteJadeUtil
						.getNomeAgente(AgenteProcessadorEvento.class);
				// A sessão Drools e o fluxo de eventos criados são passados em
				// váriaveis do agente
				ac.createNewAgent(agentName,
						AgenteProcessadorEvento.class.getName(), new Object[] {
								session, queueStream });
				ac.getAgent(agentName).start();

			} catch (Exception e) {
				e.printStackTrace();
				super.myAgent.doDelete();
			}
		}
	}

	@Override
	protected void setup() {
		super.setup();
		super.addBehaviour(new Startup());

		DiretorioAgenteJadeUtil.registrar(this, null);
	}

	@Override
	protected void finalize() throws Throwable {

		DiretorioAgenteJadeUtil.remover(this);
		super.finalize();
	}

	/**
	 * Gera representação em HTML para melhor visualização no arquivo
	 * {@link controle.html}. Os dados são atualizados a cada 2s.
	 */
	public void gerarRepresentacao(final SistemaControlado sistemaControlado) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					try {
						Path html = Paths.get("controle.html");
						try (OutputStream out = new BufferedOutputStream(
								Files.newOutputStream(
										html,
										java.nio.file.StandardOpenOption.CREATE,
										java.nio.file.StandardOpenOption.TRUNCATE_EXISTING))) {
							out.write(sistemaControlado.toString().getBytes(),
									0, sistemaControlado.toString().length());
						} catch (IOException x) {
							System.err.println(x);
						}
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						Erro.registrar(e);
					}
				}
			}
		}).start();
	}

}
