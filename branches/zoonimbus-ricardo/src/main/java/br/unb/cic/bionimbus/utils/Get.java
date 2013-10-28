/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unb.cic.bionimbus.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Metodo para a conexao entre o servidor e o cliente em casos de downloads
 * @author Deric
 */
public class Get {
    
    private JSch jsch = new JSch();
    private Session session = null;
    
    private com.jcraft.jsch.Channel channel;
    
    public boolean startSession(String file, String host) throws JSchException, SftpException {

        String user = null;
        String passw = null;
        int port = 0;
		try {
			user = Propriedades.getProp("ftp.user");
			passw = Propriedades.getProp("ftp.pass");
			port = new Integer(Propriedades.getProp("ftp.port"));
		} catch (IOException ex) {
			Logger.getLogger(Get.class.getName()).log(Level.SEVERE, null, ex);
		}

        String pathHome = System.getProperty("user.dir");
        //String path =  (pathHome.substring(pathHome.length()).equals("/") ? pathHome+"data-folder/" : pathHome+"/data-folder/");
    	String path = "/home/ubuntu/data-folder/";
            try {
            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(passw);
            session.connect();

            com.jcraft.jsch.Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            
            //Verificar se o arquivo existe antes de fazer o download
            try {
            	sftpChannel.ls(path+file);	
            } catch (Exception ex) {
            	System.out.println("Downloading file " + file + " - Arquivo nao disponivel " + Utilities.getDateString());
            	return false;
            }

            System.out.println("Downloading file " + file + " inicio download " + Utilities.getDateString());
            sftpChannel.get(path+file,path);
            System.out.println("Downloading file " + file + " termino download " + Utilities.getDateString());
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException ex) {
        	Logger.getLogger(Get.class.getName()).log(Level.SEVERE, null, ex);
            return false;  
        } catch (SftpException ex) {
        	Logger.getLogger(Get.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;

    }
}
