package br.unb.cic.bionimbus.p2p.plugin.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

/**
 * TODO: Snappy compression, zero-copy-transfer (FileInputStream.getChannel().transferTo)
 * @author edward
 *
 */
public class FileTransferServer {
	
	private final ServerSocket server;
	private Socket client;
	private String filename;
	private int port;
	
	public FileTransferServer(int port, String filename) throws IOException {		
		server = new ServerSocket(port);
		server.setReuseAddress(true);
		this.port = port;
		this.filename = filename;
	}

	public void send() throws IOException, NoSuchAlgorithmException {
		System.out.println("Listening for file transfer on port " + port);
		client = server.accept();
		
		send(new File(filename));
		
		client.close();
		server.close();
	}
	
	private void send(File file) throws IOException, NoSuchAlgorithmException {
		
		System.out.println("Transfering bytes...");
		
		long size = file.length();
		System.out.println("File length:" + size);
	
		FileInputStream fis = new FileInputStream(file);
		
		byte[] buf = new byte[64 * 1024];
		long transfered = 0;	
		int read = 0;
		OutputStream os = client.getOutputStream();
		while ((read = fis.read(buf)) != -1) {
				os.write(buf, 0, read);
				os.flush();
				transfered += read;
		}
		
		System.out.println("transfered finished!");
		
		if (size != transfered) {
			throw new IOException(String.format("File bytes: %s, total transfered: %s", size, transfered));
		}
	}
	
	public void receive() throws IOException, NoSuchAlgorithmException {
		System.out.println("Listening for file transfer on port " + port);
		client = server.accept();
		
		receive(new File(filename));
		
		client.close();
		server.close();
	}	
	
	private void receive(File file) throws IOException, NoSuchAlgorithmException {
		
		System.out.println("Transfering bytes...");
		
		long size = file.length();
		System.out.println("File length:" + size);
	
		FileOutputStream fos = new FileOutputStream(file + ".sent");
		
		byte[] buf = new byte[64 * 1024];
		long transfered = 0;	
		int read = 0;
		InputStream is = client.getInputStream();
		while ((read = is.read(buf)) != -1) {
				fos.write(buf, 0, read);
				fos.flush();
				transfered += read;
		}
		
		System.out.println("transfered finished!");
		
		fos.close();
		is.close();
		
		if (size != transfered) {
			throw new IOException(String.format("File bytes: %s, total transfered: %s", size, transfered));
		}
	}
}
