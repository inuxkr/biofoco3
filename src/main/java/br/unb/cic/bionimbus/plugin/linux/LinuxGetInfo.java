package br.unb.cic.bionimbus.plugin.linux;

import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginService;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinuxGetInfo implements Callable<PluginInfo> {

	private static final String SERVICE_DIR = "services";
	
	public static final String PATH = "files";

	private final PluginInfo pluginInfo = new PluginInfo();

	@Override
	public PluginInfo call() throws Exception {
		getCpuInfo();
		getDiskInfo();
		getNetworkInfo();
		getServices();

		return pluginInfo;
	}

	private void getCpuInfo() {
		pluginInfo.setNumCores(Runtime.getRuntime().availableProcessors());
		pluginInfo.setNumNodes(1);
		pluginInfo.setNumOccupied(0);
		pluginInfo.setWorkLoad(0);
		pluginInfo.setMemorySize((float) Runtime.getRuntime().totalMemory());
		pluginInfo.setMemoryFreeSize((float) Runtime.getRuntime().freeMemory());
	}

	private void getDiskInfo() {
		File path = new File(PATH);
		for (File root : File.listRoots()) {
			if (path.getAbsolutePath().contains(root.getAbsolutePath())) {
				pluginInfo.setFsFreeSize((float)root.getFreeSpace());
				pluginInfo.setFsSize((float)root.getTotalSpace());
				return;
			}
		}
	}
	
	private void getNetworkInfo() {
		/*
		try {  
            URL url = new URL("http://www.ip2location.com/");  
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            conexao.connect();
            java.io.BufferedReader pagina = new java.io.BufferedReader(new java.io.InputStreamReader(conexao.getInputStream()));
            String str;

            Pattern patternIP = Pattern.compile("[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*");
            Pattern patternLL = Pattern.compile("-?[0-9]*\\.[0-9]*,\\s-?[0-9]*\\.[0-9]*");

            while((str = pagina.readLine()) != null){
            	
            	if (str.contains("chkLatLng")) {
            		Matcher m = patternLL.matcher(str);
            		if (m.find()) {
            			for (int i = 0; i <= m.groupCount(); i++) {
            				String[] ll = m.group(i).split(", ");
            				pluginInfo.setLatitude(Double.valueOf(ll[0]));
            				pluginInfo.setLongitude(Double.valueOf(ll[1]));
            			}
            		}
            	}

            	if (str.contains("ipAddress")) {
            		Matcher m = patternIP.matcher(str);
            		if (m.find()) {
            			for (int i = 0; i <= m.groupCount(); i++) {
            				pluginInfo.setIp(m.group(i));
            			}
            		}
            	}
            }

            pagina.close();
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        */
	}

	private void getServices() throws Exception {
		final List<PluginService> list = new CopyOnWriteArrayList<PluginService>();
		File dir = new File(SERVICE_DIR);

		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.isFile() && file.canRead() && file.getName().endsWith(".json")) {
					ObjectMapper mapper = new ObjectMapper();
					PluginService service = mapper.readValue(file, PluginService.class);
					list.add(service);
				}
			}
		}
			
		pluginInfo.setServices(list);
	}
	
	public static double getDistancia(double latitude, double longitude, double latitudePto, double longitudePto){    
        double dlon, dlat, a, distancia;
        dlon = longitudePto - longitude;
        dlat = latitudePto - latitude;
        a = Math.pow(Math.sin(dlat/2),2) + Math.cos(latitude) * Math.cos(latitudePto) * Math.pow(Math.sin(dlon/2),2);
        distancia = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return 6378.140 * distancia;
	} 


}
