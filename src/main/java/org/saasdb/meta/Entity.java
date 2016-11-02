package org.saasdb.meta;

public class Entity extends Base{
	
	private String name;
	private String alias;
	private String desc;
	
	private String className;
	
	private String idFieldName = "id";
	private int idFieldDataType = DataType.UUID;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	/*
	public List<String> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<String> fieldList) {
		this.fieldList = fieldList;
	}
	*/
	
	public String getIdFieldName() {
		return idFieldName;
	}

	public void setIdFieldName(String idFieldName) {
		this.idFieldName = idFieldName;
	}

	public int getIdFieldDataType() {
		return idFieldDataType;
	}

	public void setIdFieldDataType(int idFieldDataType) {
		this.idFieldDataType = idFieldDataType;
	}

	

}
