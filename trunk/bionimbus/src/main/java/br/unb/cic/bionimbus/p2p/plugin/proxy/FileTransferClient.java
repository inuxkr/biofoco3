package br.unb.cic.bionimbus.p2p.plugin.proxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

import br.unb.cic.bionimbus.utils.Hashifier;

public class FileTransferClient {
	
	private final Socket server;
	
	public FileTransferClient(String address, int port, String filename, long size) throws UnknownHostException, IOException, NoSuchAlgorithmException {
		
		server = new Socket(address, port);
		
		InputStream is = server.getInputStream();
		
		FileOutputStream fos = new FileOutputStream("/tmp/" + filename);
		
		byte[] buf = new byte[64 * 1024];
		
		long count = 0;		
		int read = 0;
		while ((read= is.read(buf)) != -1) {			
				fos.write(buf, 0, read);
				fos.flush();
				count += read;
		}
		
		if (count != size) {
			System.out.println("conteudo diferente!");
		}
		
		fos.close();
		server.close();
		
		System.out.println(Hashifier.hashContent(new File("/tmp/" + filename)));
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, NoSuchAlgorithmException {
		new FileTransferClient("localhost", 7171, "teste.txt", 41665162L);
	}

}
