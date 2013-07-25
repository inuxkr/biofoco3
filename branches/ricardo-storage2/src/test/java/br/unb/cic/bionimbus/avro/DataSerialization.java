package br.unb.cic.bionimbus.avro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;

/**
 * http://avro.apache.org/docs/current/spec.html
 * 
 * @author edward
 * 
 */
public class DataSerialization {

	public static void main(String[] args) throws IOException {

		final String schemaString = readSchema("schemas" + File.separator + "credit.avr");

		System.out.println(schemaString);

		// PARSING SCHEMA
		Schema schema = new Schema.Parser().parse(schemaString);

		// LOADING SCHEMA AND INSERTING DATA
		GenericRecord datum = new GenericData.Record(schema);
		datum.put("username", "Bob");
		datum.put("credit", 15000.0);

		// SERIALIZING TO FILE
		File file = new File("data/credit.avro");

		DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(
				schema);
		DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(
				writer);
		dataFileWriter.create(schema, file);

		dataFileWriter.append(datum);

		datum.put("username", "Mary");
		datum.put("credit", 50000.0);
		datum.put("approved", true);
		
		dataFileWriter.append(datum);	

		dataFileWriter.close();

		// DESERIALIZING FROM FILE
		System.out.println("Written to avro data file");

		DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>();
		DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(
				file, reader);

		while (dataFileReader.hasNext()) {
			GenericRecord result = dataFileReader.next();
			System.out.println("Customer " + result.get("username") + " has "
					+ result.get("credit") + " worth of credit. Aproved:"
					+ result.get("approved"));
		}
	}

	private static String readSchema(String file) throws IOException {

		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));

		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		return sb.toString();
	}
}
