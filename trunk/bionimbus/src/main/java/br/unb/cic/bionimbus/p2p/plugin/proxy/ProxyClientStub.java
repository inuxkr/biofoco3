package br.unb.cic.bionimbus.p2p.plugin.proxy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.plugin.PluginFile;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.linux.LinuxGetInfo;

public class ProxyClientStub implements Proxy {

	private boolean binded;
	private Socket server;
	private final String address;
	private final int port;

	private final ScheduledExecutorService executor;

	public static final int INTERVAL = 30;

	public ProxyClientStub(String address, int port,
			ScheduledExecutorService executor) {
		this.address = address;
		this.port = port;
		this.executor = executor;
	}

	/**
	 * Connects to ProxyServerStub
	 */
	public void bind() throws IOException {
		server = new Socket(InetAddress.getByName(address), port);
		binded = true;
	}

	public void eventLoop() throws Exception {

		int count = 0;

		while (true) {

			if (count == 3)
				break;

			try {

				bind();
				String command = read();
				String result = execute(command);
				write(result);

				sleep(TimeUnit.SECONDS, 1);

				unbind();

			} catch (Exception e) {
				e.printStackTrace();
				count++;
			}
		}
	}

	private void sleep(TimeUnit timeUnit, int amount) {
		try {
			timeUnit.sleep(amount);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private String execute(String command) throws Exception {

		if (command.equals("INFO")) {

			PluginInfo info = new LinuxGetInfo().call();
			ObjectMapper mapper = new ObjectMapper();

			return "INFO#" + mapper.writeValueAsString(info);
		}

		if (command.startsWith("SAVE-FILE")) {

			String[] split = command.split(":");
			String filePath = split[1];

			File file = new File(filePath);

			PluginFile pFile = new PluginFile();
			pFile.setPath(file.getName());
			pFile.setSize(file.length());
			String absolutePath = new File(LinuxGetInfo.PATH).getAbsolutePath();
			file.renameTo(new File(absolutePath + File.separator
					+ file.getName()));

			ObjectMapper mapper = new ObjectMapper();
			return "SAVE-FILE#" + mapper.writeValueAsString(pFile);
		}
		
		if (command.startsWith("GET-FILE")) {

//			String absolutePath = new File(LinuxGetInfo.PATH).getAbsolutePath();
//			FileUtils.copyFile(new File(absolutePath + File.separator
//					+ getFile.getPluginFile().getPath()), new File(serverPath
//					+ File.separator + getFile.getPluginFile().getPath()));
//			return getFile;
		}
		
		if (command.startsWith("RUN-TASK")) {
			// return executor.submit(new PluginTaskRunner(this, task, service,
			// getP2P().getConfig().getServerPath()));
		}

		return "NO DEFINED";

	}

	public String read() throws IOException {

		if (!binded || server.isClosed())
			throw new IllegalStateException("Stub is not binded!");

		DataInputStream input = new DataInputStream(server.getInputStream());
		return input.readUTF();
	}

	public void write(String message) throws IOException {

		if (!binded || server.isClosed())
			throw new IllegalStateException("Stub is not binded!");

		DataOutputStream output = new DataOutputStream(server.getOutputStream());
		output.writeUTF(message);
		output.flush();
	}

	public void unbind() throws IOException {
		binded = false;
		server.close();
	}
}
