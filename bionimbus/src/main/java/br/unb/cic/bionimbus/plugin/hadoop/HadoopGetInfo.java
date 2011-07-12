package br.unb.cic.bionimbus.plugin.hadoop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

import br.unb.cic.bionimbus.plugin.PluginInfo;

public class HadoopGetInfo implements Callable<PluginInfo> {

	private static final String nameNode = "http://localhost:50070/dfshealth.jsp";

	private static final String jobTracker = "http://localhost:50030/jobtracker.jsp";

	private static final String nodes = "<a href=\"machines.jsp?type=active\">";

	private void getNameNodeInfo(PluginInfo info) throws Exception {
		URL url = new URL(nameNode);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();

		BufferedReader br = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String s = "";

		while ((s = br.readLine()) != null) {
			if (s.contains("Configured Capacity")) {
				int i = 0;
				StringTokenizer st = new StringTokenizer(s, "<>");

				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (i == 7) {
						info.setFsSize(Float.parseFloat(token.split(" ")[1]));
					} else if (i == 23) {
						info.setFsFreeSize(Float.parseFloat(token.split(" ")[1]));
					}
					i++;
					if (i > 23)
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

		BufferedReader br = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String s = "";

		while ((s = br.readLine()) != null) {
			int index = -1;

			if ((index = s.indexOf(nodes)) > 0) {
				index += nodes.length();
				String[] tokens = s.substring(index).split("<");
				info.setNumNodes(Integer.parseInt(tokens[0]));
				info.setNumCores(Integer.parseInt(tokens[11]
						.substring(tokens[11].indexOf('>') + 1)));
			}
		}

		br.close();
		conn.disconnect();
	}

	@Override
	public PluginInfo call() throws Exception {
		PluginInfo info = new PluginInfo();
		getNameNodeInfo(info);
		getJobTrackerInfo(info);
		//TODO pegar informacoes das aplicacoes disponiveis
		return info;
	}

}
