package br.biofoco.p2p.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.biofoco.p2p.config.PeerConfig;
import br.biofoco.p2p.kad.KadException;

public class OpenKad {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenKad.class);
	
	private static OpenKad INSTANCE = null;
	
	private final Messenger messenger;
	
	private final ID peerID;
	
	private ExecutorService execServ = Executors.newSingleThreadExecutor();
	
	private ScheduledExecutorService schedServ = Executors.newScheduledThreadPool(1);

	private final PeerConfig config;

	private final Set<String> neighbours = new HashSet<String>();
	
	/** TODO: Singleton, convert to Guice 
	 * @param config */
	private OpenKad(PeerConfig config) {
		
		this.config = config;
		
		//if (!config.isPersistent()) {
		
		peerID = createRandomPeerID();
		config.setPeerID(peerID);
		messenger = new Messenger(config.getPort());
		
		config.setUptime(System.currentTimeMillis());
	}
	
	public PeerConfig getConfig() {
		return config;
	}
	
	private ID createRandomPeerID() {
		String input = "" + System.nanoTime();
		return IDFactory.newID(input.getBytes());
	}

	public static synchronized OpenKad getInstance(PeerConfig config) {		
		if (INSTANCE == null)
			INSTANCE = new OpenKad(config);
		return INSTANCE;
	}
	
	public static synchronized OpenKad get() {
		return INSTANCE;
	}
	
	public Set<String> neighbours() {
		return neighbours;
	}

	public void start() throws KadException {
		
		LOGGER.debug("starting peer instance " + peerID);
		
		// TODO replace by a Future to get when it actually started
		execServ.execute(new Runnable() {

			@Override
			public void run() {
				try {
					messenger.start();
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
				}				
			}			
		});
		
		schedServ.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				pingSeeds();
			}		
		}
		, 0
		, 10
		, TimeUnit.SECONDS);
		
		LOGGER.debug("instance started!");
	}
	
	public void pingSeeds() {
		LOGGER.debug("checking seeds " + config.getSeeds());
		
		for (String seed: config.getSeeds()){
			LOGGER.debug(seed);
			
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL("http://" + seed).openConnection();
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				
				String line;
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null){
					sb.append(line);
				}				
				
				neighbours.add(sb.toString());
				System.out.println(sb.toString());
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			//TODO move to own class
//			Client client = Client.create();
//			client.setConnectTimeout(1);
//			client.setReadTimeout(1); 
//			WebResource webResource = client.resource("http://" + seed);
//			String s = webResource.get(String.class);
//			System.out.println(s);
		}
	}	
	
	void stop() throws KadException {
		try {
			messenger.stop();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new KadException(e);
		}
		
		execServ.shutdown();
		
		LOGGER.debug("instance stopped");
	}

	public ID getPeerID() {
		return peerID;
	}
}
