package br.unb.cic.bionimbus.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Propriedades {

	public static String getProp(String propriedade) throws IOException { 
		Properties props = new Properties();
		String pathHome = System.getProperty("user.dir");
		FileInputStream file = new FileInputStream(pathHome+"zoonimbus.properties"); 
		props.load(file); 

		return props.getProperty(propriedade);
	}

}
