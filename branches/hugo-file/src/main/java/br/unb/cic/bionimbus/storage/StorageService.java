package br.unb.cic.bionimbus.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.Service;
import br.unb.cic.bionimbus.ServiceManager;
import br.unb.cic.bionimbus.client.FileInfo;
import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PEventType;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.P2PMessageEvent;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.p2p.messages.AbstractMessage;
import br.unb.cic.bionimbus.p2p.messages.CloudReqMessage;
import br.unb.cic.bionimbus.p2p.messages.CloudRespMessage;
import br.unb.cic.bionimbus.p2p.messages.GetReqMessage;
import br.unb.cic.bionimbus.p2p.messages.GetRespMessage;
import br.unb.cic.bionimbus.p2p.messages.ListRespMessage;
import br.unb.cic.bionimbus.p2p.messages.StoreAckMessage;
import br.unb.cic.bionimbus.p2p.messages.StoreReqMessage;
import br.unb.cic.bionimbus.p2p.messages.StoreRespMessage;
import br.unb.cic.bionimbus.plugin.PluginFile;
import br.unb.cic.bionimbus.plugin.PluginInfo;

public class StorageService implements Service, P2PListener, Runnable {

	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(1, new BasicThreadFactory.Builder()
					.namingPattern("StorageService-%d").build());
	
	private final Map<String, PluginInfo> cloudMap = new ConcurrentHashMap<String, PluginInfo>();
	
	private final Map<String, PluginFile> savedFiles = new ConcurrentHashMap<String, PluginFile>();

	private P2PService p2p = null;

	public StorageService(ServiceManager manager) {
		manager.register(this);
	}

	@Override
	public void run() {
		System.out.println("running StorageService...");
		Message msg = new CloudReqMessage(p2p.getPeerNode());
		p2p.broadcast(msg); // TODO isso e' broadcast?
	}

	@Override
	public void start(P2PService p2p) {
		this.p2p = p2p;
		if (p2p != null)
			p2p.addListener(this);

		executorService.scheduleAtFixedRate(this, 0, 30, TimeUnit.SECONDS);
	}

	@Override
	public void shutdown() {
		p2p.remove(this);
		executorService.shutdownNow();
	}

	@Override
	public void getStatus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEvent(P2PEvent event) {
		if (event.getType() != P2PEventType.MESSAGE)
			return;

		P2PMessageEvent msgEvent = (P2PMessageEvent) event;
		Message msg = msgEvent.getMessage();
		if (msg == null)
			return;
		
		PeerNode receiver = null;
		if (msg instanceof AbstractMessage) {
			receiver = ((AbstractMessage) msg).getPeer();
		}
		
		switch (P2PMessageType.of(msg.getType())) {
		case CLOUDRESP:
			CloudRespMessage cloudMsg = (CloudRespMessage) msg;
			for (PluginInfo info : cloudMsg.values()) {
				cloudMap.put(info.getId(), info);
			}
			break;
		case STOREREQ:
			StoreReqMessage storeMsg = (StoreReqMessage) msg;
			sendStoreResp(storeMsg.getFileInfo(), receiver);
			break;
		case STOREACK:
			StoreAckMessage ackMsg = (StoreAckMessage) msg;
			savedFiles.put(ackMsg.getPluginFile().getId(), ackMsg.getPluginFile());
			break;
		case LISTREQ:
			p2p.sendMessage(receiver.getHost(), new ListRespMessage(p2p.getPeerNode(), savedFiles.values()));
			break;
		case GETREQ:
			GetReqMessage getMsg = (GetReqMessage) msg;
			PluginFile file = savedFiles.get(getMsg.getFileId());
			for (PluginInfo plugin : cloudMap.values()) {
				if (plugin.getId().equals(file.getPluginId())) {
					p2p.sendMessage(receiver.getHost(), new GetRespMessage(p2p.getPeerNode(), plugin, file));
					return;
				}
			}
			
			//TODO mensagem de erro?
			break;
		}
	}
	
	public void sendStoreResp(FileInfo info, PeerNode dest) {
		for (PluginInfo plugin : cloudMap.values()) {
			if (info.getSize() < plugin.getFsFreeSize()) {
				StoreRespMessage msg = new StoreRespMessage(p2p.getPeerNode(), plugin, info);
				p2p.sendMessage(dest.getHost(), msg);
				return;
			}
		}
	}

}
