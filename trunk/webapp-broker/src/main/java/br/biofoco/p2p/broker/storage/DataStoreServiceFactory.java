package br.biofoco.p2p.broker.storage;

public class DataStoreServiceFactory {
	
	private static DatastoreService DATASTORE;

	public static synchronized DatastoreService getDatastoreServie() throws DatastoreException {
		if (DATASTORE == null){
			DATASTORE = new MemoryDatastore();
		}
		return DATASTORE;
	}

}
