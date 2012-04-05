package br.unb.cic.bionimbus.p2p.plugin.proxy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

public class FileTransferClient {
	
	private final Socket server;
	private final String filename;
	
	public FileTransferClient(String address, int port, String filename) throws UnknownHostException, IOException, NoSuchAlgorithmException {		
		server = new Socket(address, port);
		server.setReuseAddress(true);
		this.filename = filename;
	}
	
	public void receive() throws IOException {
		InputStream is = server.getInputStream();
		
		FileOutputStream fos = new FileOutputStream(filename);
		
		byte[] buf = new byte[64 * 1024];
		
//		long count = 0;		
		int read = 0;
		while ((read= is.read(buf)) != -1) {			
				fos.write(buf, 0, read);
				fos.flush();
//				count += read;
		}
		
//		if (count != size) {
//			System.out.println("conteudo diferente!");
//		}
		
		fos.close();
		server.close();
		
//		System.out.println(Hashifier.hashContent(new File(filename)));
	}

	public void send() throws IOException {
		
		OutputStream os = server.getOutputStream();
		
		FileInputStream fos = new FileInputStream(filename);
		
		byte[] buf = new byte[64 * 1024];
		
//		long count = 0;		
		int read = 0;
		while ((read= fos.read(buf)) != -1) {			
				os.write(buf, 0, read);
				os.flush();
//				count += read;
		}
		
//		if (count != size) {
//			System.out.println("conteudo diferente!");
//		}
		
		fos.close();
		os.close();
		server.close();		
	}
}
