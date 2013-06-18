/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package br.unb.cic.bionimbus.avro.gen;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public interface BioProto {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"BioProto\",\"namespace\":\"br.unb.cic.bionimbus.avro.gen\",\"types\":[{\"type\":\"enum\",\"name\":\"NodeState\",\"symbols\":[\"STARTING\",\"ACTIVE\",\"CLOSING\",\"IDLE\",\"ERROR\",\"DECOMMISSIONED\"]},{\"type\":\"record\",\"name\":\"JobCancel\",\"fields\":[{\"name\":\"jobID\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"files\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}}]},{\"type\":\"record\",\"name\":\"NodeInfo\",\"fields\":[{\"name\":\"peerId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"address\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"freesize\",\"type\":\"float\"},{\"name\":\"latency\",\"type\":\"double\"}]},{\"type\":\"record\",\"name\":\"FileInfo\",\"fields\":[{\"name\":\"fileId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"size\",\"type\":\"long\"},{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}],\"messages\":{\"ping\":{\"doc\":\"Pings a node, e.g. to measure latency\",\"request\":[],\"response\":\"boolean\"},\"listFiles\":{\"request\":[],\"response\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}},\"listServices\":{\"request\":[],\"response\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}},\"startJob\":{\"request\":[{\"name\":\"jobID\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}],\"response\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},\"cancelJob\":{\"request\":[{\"name\":\"jobID\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}],\"response\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},\"setFileInfo\":{\"request\":[{\"name\":\"file\",\"type\":\"FileInfo\"}],\"response\":\"null\"},\"getPeersNode\":{\"request\":[],\"response\":{\"type\":\"array\",\"items\":\"NodeInfo\"}},\"setNodes\":{\"request\":[{\"name\":\"list\",\"type\":{\"type\":\"array\",\"items\":\"NodeInfo\"}}],\"response\":\"null\"},\"fileSent\":{\"request\":[{\"name\":\"fileSucess\",\"type\":\"FileInfo\"},{\"name\":\"dest\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}],\"response\":\"null\"},\"callStorage\":{\"request\":[],\"response\":{\"type\":\"array\",\"items\":\"NodeInfo\"}}}}");
  /** Pings a node, e.g. to measure latency */
  boolean ping() throws org.apache.avro.AvroRemoteException;
  java.util.List<java.lang.String> listFiles() throws org.apache.avro.AvroRemoteException;
  java.util.List<java.lang.String> listServices() throws org.apache.avro.AvroRemoteException;
  java.lang.String startJob(java.lang.String jobID) throws org.apache.avro.AvroRemoteException;
  java.lang.String cancelJob(java.lang.String jobID) throws org.apache.avro.AvroRemoteException;
  java.lang.Void setFileInfo(br.unb.cic.bionimbus.avro.gen.FileInfo file) throws org.apache.avro.AvroRemoteException;
  java.util.List<br.unb.cic.bionimbus.avro.gen.NodeInfo> getPeersNode() throws org.apache.avro.AvroRemoteException;
  java.lang.Void setNodes(java.util.List<br.unb.cic.bionimbus.avro.gen.NodeInfo> list) throws org.apache.avro.AvroRemoteException;
  java.lang.Void fileSent(br.unb.cic.bionimbus.avro.gen.FileInfo fileSucess, java.lang.String dest) throws org.apache.avro.AvroRemoteException;
  java.util.List<br.unb.cic.bionimbus.avro.gen.NodeInfo> callStorage() throws org.apache.avro.AvroRemoteException;

  @SuppressWarnings("all")
  public interface Callback extends BioProto {
    public static final org.apache.avro.Protocol PROTOCOL = br.unb.cic.bionimbus.avro.gen.BioProto.PROTOCOL;
    /** Pings a node, e.g. to measure latency */
    void ping(org.apache.avro.ipc.Callback<java.lang.Boolean> callback) throws java.io.IOException;
    void listFiles(org.apache.avro.ipc.Callback<java.util.List<java.lang.String>> callback) throws java.io.IOException;
    void listServices(org.apache.avro.ipc.Callback<java.util.List<java.lang.String>> callback) throws java.io.IOException;
    void startJob(java.lang.String jobID, org.apache.avro.ipc.Callback<java.lang.String> callback) throws java.io.IOException;
    void cancelJob(java.lang.String jobID, org.apache.avro.ipc.Callback<java.lang.String> callback) throws java.io.IOException;
    void setFileInfo(br.unb.cic.bionimbus.avro.gen.FileInfo file, org.apache.avro.ipc.Callback<java.lang.Void> callback) throws java.io.IOException;
    void getPeersNode(org.apache.avro.ipc.Callback<java.util.List<br.unb.cic.bionimbus.avro.gen.NodeInfo>> callback) throws java.io.IOException;
    void setNodes(java.util.List<br.unb.cic.bionimbus.avro.gen.NodeInfo> list, org.apache.avro.ipc.Callback<java.lang.Void> callback) throws java.io.IOException;
    void fileSent(br.unb.cic.bionimbus.avro.gen.FileInfo fileSucess, java.lang.String dest, org.apache.avro.ipc.Callback<java.lang.Void> callback) throws java.io.IOException;
    void callStorage(org.apache.avro.ipc.Callback<java.util.List<br.unb.cic.bionimbus.avro.gen.NodeInfo>> callback) throws java.io.IOException;
  }
}