package br.unb.cic.bionimbus.p2p.dht;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.unb.cic.bionimbus.p2p.rpc.multicast.MulticastTransport;
import br.unb.cic.bionimbus.p2p.transport.Command;
import br.unb.cic.bionimbus.p2p.transport.PeerCredentials;
import br.unb.cic.bionimbus.p2p.transport.TcpTransport;
import br.unb.cic.bionimbus.p2p.transport.TransportHandler;
import br.unb.cic.bionimbus.p2p.transport.WireFormatException;
import br.unb.cic.bionimbus.p2p.transport.WireMessage;


public class MessengerService implements TransportHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessengerService.class);
	
	private final Map<String, Command> commands = new HashMap<String, Command>();
	
	private static final Random random = new Random(System.nanoTime());
	
	private volatile boolean running = false;
		
	private TcpTransport tcpTransport;
	private MulticastTransport mcastTransport;

	private final PeerConfig config;
	
	public MessengerService(PeerConfig config) {
		this.config = config;
	}
	
	public void addCommand(Command command) {
		commands.put(command.getName(), command);
	}

	public void start() throws IOException {
		LOG.debug("Starting MessengerService ...");
		tcpTransport = new TcpTransport(config.getPort());
		tcpTransport.addHandler(this);
		tcpTransport.start();
		
//		mcastTransport = new MulticastTransport("233.1.2.8", 32334);		
//		mcastTransport.addListener(this);
//		mcastTransport.start();
		
		running = true;
	}
	
	public void stop() throws IOException {
		LOG.debug("Stopping MessengerService ...");
		tcpTransport.stop();
		
		running = false;
	}

	@Override
	public WireMessage doRequestResponse(WireMessage message) {
		String body = message.getBody();
		
		Command command = commands.get(body);
		String response;
		try {
			response = command.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response = "Error"; // add error code
		}
		
		WireMessage wireMessage = new WireMessage(message.getId(), response);
		
		return wireMessage;
	}
	
	public String sendMessage(String message) throws IOException, WireFormatException {
		WireMessage response = tcpTransport.sendMessage("localhost", TcpTransport.DEFAULT_PORT, new WireMessage(10L, message));
		return response.getBody();
	}

	public boolean isRunning() {
		return running;
	}

	public PeerNode ping(Endpoint host) throws IOException, WireFormatException {
		
		WireMessage response = tcpTransport.sendMessage(host.getAddress()
				                                    ,host.getPort()
				                                    ,new WireMessage(Math.abs(random.nextLong()), "PING"));
		
		ObjectMapper mapper = new ObjectMapper();		
		
	
		PeerCredentials cred = mapper.readValue(response.getBody(), PeerCredentials.class);
		
		ID id = IDFactory.fromString(cred.getId());
		PeerNode node = new PeerNode(id);		
		node.setEndpoint(cred.getEndpoint());
		
		return node;		
	}
	
}
