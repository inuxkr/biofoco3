package br.unb.cic.bionimbus.plugin.hadoop;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginService;

public class HadoopGetInfo implements Callable<PluginInfo> {

	private static final String nameNode = "http://localhost:50070/dfshealth.jsp";
	private static final String jobTracker = "http://localhost:50030/jobtracker.jsp";
	private static final String nodes = "<a href=\"machines.jsp?type=active\">";
	private static final String serviceDir = "services";

	private void getNameNodeInfo(PluginInfo info) throws Exception {
		
		URL url = new URL(nameNode);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String s = "";

		while ((s = br.readLine()) != null) {
			if (s.contains("Configured Capacity")) {
				int i = 0;
				StringTokenizer st = new StringTokenizer(s, "<>");

				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					String[] split = token.split(" ");
					String unit;
					float value;
					if (i == 7) {
						value = Float.parseFloat(split[1]);
						unit = split[2];
						if (unit.equals("GB"))
							value *= (1024*1024*1024);
						else if (unit.equals("MB"))
							value *= (1024*1024);
						else if (unit.equals("MB"))
							value *= 1024;
						info.setFsSize(value);
					} else if (i == 31) {
						value = Float.parseFloat(split[1]);
						unit = split[2];
						if (unit.equals("GB"))
							value *= (1024*1024*1024);
						else if (unit.equals("MB"))
							value *= (1024*1024);
						else if (unit.equals("MB"))
							value *= 1024;
						info.setFsFreeSize(value);
					}
					i++;
					if (i > 31)
						break;
				}
			}
		}

		br.close();
		conn.disconnect();
	}

	private void getJobTrackerInfo(PluginInfo info) throws Exception {
		URL url = new URL(jobTracker);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String s = "";

		while ((s = br.readLine()) != null) {
			int index = -1;

			if ((index = s.indexOf(nodes)) > 0) {
				index += nodes.length();
				String[] tokens = s.substring(index).split("<");
				info.setNumNodes(Integer.parseInt(tokens[0]));
				info.setNumCores(Integer.parseInt(tokens[11].substring(tokens[11].indexOf('>') + 1)));
			}
		}

		br.close();
		conn.disconnect();
	}
	
	private void loadServices(PluginInfo info) throws Exception {
		List<PluginService> list = new CopyOnWriteArrayList<PluginService>();
		System.out.println("serviceDir = " + serviceDir);
		File dir = new File(serviceDir);
		
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.isFile() && file.canRead() && file.getName().endsWith(".json")) {
					ObjectMapper mapper = new ObjectMapper();
					PluginService service = mapper.readValue(file, PluginService.class);
					list.add(service);
				}
			}
		}
		info.setServices(list);
	}

	@Override
	public PluginInfo call() throws Exception {
		PluginInfo info = new PluginInfo();
		getNameNodeInfo(info);
		getJobTrackerInfo(info);
		loadServices(info);
		return info;
	}

}
