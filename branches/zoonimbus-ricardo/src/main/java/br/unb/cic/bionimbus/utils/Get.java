/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unb.cic.bionimbus.utils;

import java.util.Vector;

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
    private String USER = "ubuntu";
    private String PASSW = "ubuntu";
    private int PORT = 22;
    private com.jcraft.jsch.Channel channel;
    
    public boolean startSession(String file, String host) throws JSchException, SftpException {
        String pathHome = System.getProperty("user.dir");
        String path =  (pathHome.substring(pathHome.length()).equals("/") ? pathHome+"data-folder/" : pathHome+"/data-folder/");
            try {
            session = jsch.getSession(USER, host, PORT);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(PASSW);
            session.connect();

            com.jcraft.jsch.Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            Vector v = sftpChannel.ls(path+file);
            if (v.size() > 0) {
                System.out.println("Downloading file " + file + " inicio download " + Utilities.getDateString());
                sftpChannel.get(path+file,path);
                System.out.println("Downloading file " + file + " termino download " + Utilities.getDateString());
            } else {
                System.out.println("Downloading file " + file + " - Arquivo não disponível " + Utilities.getDateString());
            }
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
        	e.printStackTrace();
            return false;  
        } catch (SftpException e) {
        	e.printStackTrace();
            return false;
        }
        return true;

    }
}
