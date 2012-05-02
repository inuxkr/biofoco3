/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package br.unb.cic.bionimbus.avro.proto;  
@SuppressWarnings("all")
public class InfoRecord extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"InfoRecord\",\"namespace\":\"br.unb.cic.bionimbus.avro.proto\",\"fields\":[{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"data\",\"type\":\"string\"}]}");
  @Deprecated public long timestamp;
  @Deprecated public java.lang.CharSequence data;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return timestamp;
    case 1: return data;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: timestamp = (java.lang.Long)value$; break;
    case 1: data = (java.lang.CharSequence)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
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

  /**
   * Gets the value of the 'data' field.
   */
  public java.lang.CharSequence getData() {
    return data;
  }

  /**
   * Sets the value of the 'data' field.
   * @param value the value to set.
   */
  public void setData(java.lang.CharSequence value) {
    this.data = value;
  }

  /** Creates a new InfoRecord RecordBuilder */
  public static br.unb.cic.bionimbus.avro.proto.InfoRecord.Builder newBuilder() {
    return new br.unb.cic.bionimbus.avro.proto.InfoRecord.Builder();
  }
  
  /** Creates a new InfoRecord RecordBuilder by copying an existing Builder */
  public static br.unb.cic.bionimbus.avro.proto.InfoRecord.Builder newBuilder(br.unb.cic.bionimbus.avro.proto.InfoRecord.Builder other) {
    return new br.unb.cic.bionimbus.avro.proto.InfoRecord.Builder(other);
  }
  
  /** Creates a new InfoRecord RecordBuilder by copying an existing InfoRecord instance */
  public static br.unb.cic.bionimbus.avro.proto.InfoRecord.Builder newBuilder(br.unb.cic.bionimbus.avro.proto.InfoRecord other) {
    return new br.unb.cic.bionimbus.avro.proto.InfoRecord.Builder(other);
  }
  
  /**
   * RecordBuilder for InfoRecord instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<InfoRecord>
    implements org.apache.avro.data.RecordBuilder<InfoRecord> {

    private long timestamp;
    private java.lang.CharSequence data;

    /** Creates a new Builder */
    private Builder() {
      super(br.unb.cic.bionimbus.avro.proto.InfoRecord.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(br.unb.cic.bionimbus.avro.proto.InfoRecord.Builder other) {
      super(other);
    }
    
    /** Creates a Builder by copying an existing InfoRecord instance */
    private Builder(br.unb.cic.bionimbus.avro.proto.InfoRecord other) {
            super(br.unb.cic.bionimbus.avro.proto.InfoRecord.SCHEMA$);
      if (isValidValue(fields()[0], other.timestamp)) {
        this.timestamp = (java.lang.Long) data().deepCopy(fields()[0].schema(), other.timestamp);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.data)) {
        this.data = (java.lang.CharSequence) data().deepCopy(fields()[1].schema(), other.data);
        fieldSetFlags()[1] = true;
      }
    }

    /** Gets the value of the 'timestamp' field */
    public java.lang.Long getTimestamp() {
      return timestamp;
    }
    
    /** Sets the value of the 'timestamp' field */
    public br.unb.cic.bionimbus.avro.proto.InfoRecord.Builder setTimestamp(long value) {
      validate(fields()[0], value);
      this.timestamp = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'timestamp' field has been set */
    public boolean hasTimestamp() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'timestamp' field */
    public br.unb.cic.bionimbus.avro.proto.InfoRecord.Builder clearTimestamp() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'data' field */
    public java.lang.CharSequence getData() {
      return data;
    }
    
    /** Sets the value of the 'data' field */
    public br.unb.cic.bionimbus.avro.proto.InfoRecord.Builder setData(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.data = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'data' field has been set */
    public boolean hasData() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'data' field */
    public br.unb.cic.bionimbus.avro.proto.InfoRecord.Builder clearData() {
      data = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    public InfoRecord build() {
      try {
        InfoRecord record = new InfoRecord();
        record.timestamp = fieldSetFlags()[0] ? this.timestamp : (java.lang.Long) defaultValue(fields()[0]);
        record.data = fieldSetFlags()[1] ? this.data : (java.lang.CharSequence) defaultValue(fields()[1]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
