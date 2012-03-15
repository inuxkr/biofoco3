package br.unb.cic.bionimbus.client.experiments;

import br.unb.cic.bionimbus.client.JobInfo;

public interface Pipeline {

	String getCurrentOutput();

	JobInfo nextJob();

	String getInput();
}
