/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package br.unb.cic.bionimbus.avro.gen;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class PingRespMessage extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"PingRespMessage\",\"namespace\":\"br.unb.cic.bionimbus.avro.gen\",\"fields\":[{\"name\":\"nodeInfo\",\"type\":{\"type\":\"record\",\"name\":\"NodeInfo\",\"fields\":[{\"name\":\"peerId\",\"type\":\"string\"},{\"name\":\"address\",\"type\":\"string\"},{\"name\":\"nodeState\",\"type\":[{\"type\":\"enum\",\"name\":\"NodeState\",\"symbols\":[\"STARTING\",\"ACTIVE\",\"CLOSING\",\"IDLE\",\"ERROR\",\"DECOMMISSIONED\"]},\"null\"]}]}},{\"name\":\"timestamp\",\"type\":\"long\",\"default\":0}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public br.unb.cic.bionimbus.avro.gen.NodeInfo nodeInfo;
  @Deprecated public long timestamp;

  /**
   * Default constructor.
   */
  public PingRespMessage() {}

  /**
   * All-args constructor.
   */
  public PingRespMessage(br.unb.cic.bionimbus.avro.gen.NodeInfo nodeInfo, java.lang.Long timestamp) {
    this.nodeInfo = nodeInfo;
    this.timestamp = timestamp;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return nodeInfo;
    case 1: return timestamp;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: nodeInfo = (br.unb.cic.bionimbus.avro.gen.NodeInfo)value$; break;
    case 1: timestamp = (java.lang.Long)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'nodeInfo' field.
   */
  public br.unb.cic.bionimbus.avro.gen.NodeInfo getNodeInfo() {
    return nodeInfo;
  }

  /**
   * Sets the value of the 'nodeInfo' field.
   * @param value the value to set.
   */
  public void setNodeInfo(br.unb.cic.bionimbus.avro.gen.NodeInfo value) {
    this.nodeInfo = value;
  }

  /**
   * Gets the value of the 'timestamp' field.
   */
  public java.lang.Long getTimestamp() {
    return timestamp;
  }

  /**
   * Sets the value of the 'timestamp' field.
   * @param value the value to set.
   */
  public void setTimestamp(java.lang.Long value) {
    this.timestamp = value;
  }

  /** Creates a new PingRespMessage RecordBuilder */
  public static br.unb.cic.bionimbus.avro.gen.PingRespMessage.Builder newBuilder() {
    return new br.unb.cic.bionimbus.avro.gen.PingRespMessage.Builder();
  }
  
  /** Creates a new PingRespMessage RecordBuilder by copying an existing Builder */
  public static br.unb.cic.bionimbus.avro.gen.PingRespMessage.Builder newBuilder(br.unb.cic.bionimbus.avro.gen.PingRespMessage.Builder other) {
    return new br.unb.cic.bionimbus.avro.gen.PingRespMessage.Builder(other);
  }
  
  /** Creates a new PingRespMessage RecordBuilder by copying an existing PingRespMessage instance */
  public static br.unb.cic.bionimbus.avro.gen.PingRespMessage.Builder newBuilder(br.unb.cic.bionimbus.avro.gen.PingRespMessage other) {
    return new br.unb.cic.bionimbus.avro.gen.PingRespMessage.Builder(other);
  }
  
  /**
   * RecordBuilder for PingRespMessage instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<PingRespMessage>
    implements org.apache.avro.data.RecordBuilder<PingRespMessage> {

    private br.unb.cic.bionimbus.avro.gen.NodeInfo nodeInfo;
    private long timestamp;

    /** Creates a new Builder */
    private Builder() {
      super(br.unb.cic.bionimbus.avro.gen.PingRespMessage.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(br.unb.cic.bionimbus.avro.gen.PingRespMessage.Builder other) {
      super(other);
    }
    
    /** Creates a Builder by copying an existing PingRespMessage instance */
    private Builder(br.unb.cic.bionimbus.avro.gen.PingRespMessage other) {
            super(br.unb.cic.bionimbus.avro.gen.PingRespMessage.SCHEMA$);
      if (isValidValue(fields()[0], other.nodeInfo)) {
        this.nodeInfo = data().deepCopy(fields()[0].schema(), other.nodeInfo);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.timestamp)) {
        this.timestamp = data().deepCopy(fields()[1].schema(), other.timestamp);
        fieldSetFlags()[1] = true;
      }
    }

    /** Gets the value of the 'nodeInfo' field */
    public br.unb.cic.bionimbus.avro.gen.NodeInfo getNodeInfo() {
      return nodeInfo;
    }
    
    /** Sets the value of the 'nodeInfo' field */
    public br.unb.cic.bionimbus.avro.gen.PingRespMessage.Builder setNodeInfo(br.unb.cic.bionimbus.avro.gen.NodeInfo value) {
      validate(fields()[0], value);
      this.nodeInfo = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'nodeInfo' field has been set */
    public boolean hasNodeInfo() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'nodeInfo' field */
    public br.unb.cic.bionimbus.avro.gen.PingRespMessage.Builder clearNodeInfo() {
      nodeInfo = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'timestamp' field */
    public java.lang.Long getTimestamp() {
      return timestamp;
    }
    
    /** Sets the value of the 'timestamp' field */
    public br.unb.cic.bionimbus.avro.gen.PingRespMessage.Builder setTimestamp(long value) {
      validate(fields()[1], value);
      this.timestamp = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'timestamp' field has been set */
    public boolean hasTimestamp() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'timestamp' field */
    public br.unb.cic.bionimbus.avro.gen.PingRespMessage.Builder clearTimestamp() {
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    public PingRespMessage build() {
      try {
        PingRespMessage record = new PingRespMessage();
        record.nodeInfo = fieldSetFlags()[0] ? this.nodeInfo : (br.unb.cic.bionimbus.avro.gen.NodeInfo) defaultValue(fields()[0]);
        record.timestamp = fieldSetFlags()[1] ? this.timestamp : (java.lang.Long) defaultValue(fields()[1]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}