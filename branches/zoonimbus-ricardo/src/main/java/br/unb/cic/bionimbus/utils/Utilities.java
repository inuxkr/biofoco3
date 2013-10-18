package br.unb.cic.bionimbus.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {

	public static String getDateString() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		try {
			return sdf.format(new Date());
		} catch (Exception ex) {
		}
		
		return "Erro ao gerar data string";
	}

}
