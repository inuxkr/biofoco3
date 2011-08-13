package br.unb.cic.bionimbus.p2p.rpc;

public interface AsyncOpListener<T> {

	void onFinished(T result);

	void onTimeoutException();

	void onInterruptedException();

}
