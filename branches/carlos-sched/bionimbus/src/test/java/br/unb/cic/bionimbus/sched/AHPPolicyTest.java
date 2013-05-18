package br.unb.cic.bionimbus.sched;

import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.sched.policy.impl.AHPPolicy;

public class AHPPolicyTest {

	public static void main(String [] args) throws Exception {
		//comparePluginInfoTest();
		testAlgo();
	}
	
	public static void testAlgo() throws SchedException {
		List<PluginInfo> pis = new ArrayList<PluginInfo>();
		for (int i = 0; i < 3; ++i) {
			PluginInfo pi = new PluginInfo();
			pi.setId(String.valueOf(i));
			pi.setLatency(Math.round(Math.random() * 1000));
			pi.setUptime(Math.round(Math.random() * 10));
			pis.add(pi);
		}
		
		PluginInfo pinfo = AHPPolicy.getBestService(pis);
		System.out.println(pinfo.getId());
	}
	
	public static void comparePluginInfoTest() throws SchedException {
		List<PluginInfo> pis = new ArrayList<PluginInfo>();
		for (int i = 0; i < 3; ++i) {
			PluginInfo pi = new PluginInfo();
			pi.setId(String.valueOf(i));
			pi.setLatency(Math.round(Math.random() * 1000));
			pis.add(pi);
		}
		
		for (PluginInfo p: pis) {
			p.setUptime(Math.round(Math.random() * 10));
		}
		
		for (PluginInfo p : pis) {
			System.out.println(p.getLatency());
		}
		
		Matrix m = AHPPolicy.generateComparisonMatrix(pis, "latency");
		m.print(pis.size(), pis.size());

		for (double d: AHPPolicy.getPrioritiesOnMatrix(m)) {
			System.out.println(d);
		}
		
		for (PluginInfo p: AHPPolicy.getServiceOrderedByPriority(pis)) {
			System.out.println(p.getLatency()); 
		}
	}
}