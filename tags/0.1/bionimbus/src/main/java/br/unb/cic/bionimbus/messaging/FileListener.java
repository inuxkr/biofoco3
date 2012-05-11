package br.unb.cic.bionimbus.messaging;

import java.io.File;
import java.util.Map;

public interface FileListener {
	
	void onFileRecvd(File file, Map<String, String> parms);

}
