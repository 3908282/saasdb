package org.saasdb.meta;

public interface DataType {

	//numeric
	public int INT = 0x01;
	public int NUMERIC = 0x02;
	
	//text
	public int CHAR = 0x11;
	public int TEXT = 0x12;
	public int UUID = 0x13;
	
	//date time
	public int DATETIME = 0x21;
	public int DATE = 0x22;
	public int TIME = 0x23;
	
	//boolean
	public int BOOLEAN = 0x31;
	
	//relation
	public int LOOKUP = 0x41;
	public int MASTERDETAIL = 0x42;
	
	//blob/clob
	public int BLOB = 0X51;
	public int CLOB = 0X52;
}
