package br.unb.cic.bionimbus.plugin.hadoop;

import br.unb.cic.bionimbus.plugin.Plugin;

public class HadoopPlugin extends Thread implements Plugin {
	
	public HadoopPlugin() {
		super("HadoopPlugin");
	}

	@Override
	public void run() {
		while (true) {
			System.out.println("running Plugin loop...");
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void start() {
		System.out.println("starting Hadoop plugin...");
		super.start();
	}

	@Override
	public void setP2P() {
		// TODO Auto-generated method stub

	}

}
