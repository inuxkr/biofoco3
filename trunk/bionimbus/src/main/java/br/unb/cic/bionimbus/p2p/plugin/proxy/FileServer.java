package br.unb.cic.bionimbus.p2p.plugin.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import br.unb.cic.bionimbus.utils.Hashifier;

/**
 * TODO: Snappy compression, zero-copy-transfer (FileInputStream.getChannel().transferTo)
 * @author edward
 *
 */
public class FileServer {
	
	private final ServerSocket server;
	private Socket client;
	private String filename;
	
	public FileServer(int port, String filename) throws IOException {		
		server = new ServerSocket(port);
		server.setReuseAddress(true);
		this.filename = filename;
	}

	public void start() throws IOException, NoSuchAlgorithmException {
		System.out.println("Listening for file bytes...");
		client = server.accept();
		
		transfer(new File(filename));
		
		client.close();
		server.close();
	}
	
	public void transfer(File file) throws IOException, NoSuchAlgorithmException {
		
		System.out.println("Calculating hash");
		System.out.println(Hashifier.hashContent(file));
		
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
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		new FileServer(7171, "/home/edward/Downloads/ext-4.0.7-gpl.zip").start();
	}
	
}
