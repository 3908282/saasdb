package org.saasdb.meta;

public class Field extends Base{

	private String name;
	private String alias;
	private String desc;
	
	private String className;
	
	private int dataType;
	private int length;
	private int precise;
	private String refEntity; //lookup or masterdetail
	
	private boolean nullable;
	private String defaultValue;
	
	private String fieldName;
	private int fieldNum = -1;
	
	private boolean isIndex;
	private boolean isUnique;
	
	private boolean deleted;
	
	private boolean isFlex;
	
	public Field()
	{
		
	}
	
	public Field(String name, String alias, String desc,
			int dataType, int length, int precise, String refId, boolean nullable, String defaultValue, int fieldNum, String fieldName,
			boolean isIndex, boolean isUnique, boolean deleted, boolean isFlex
			)
	{
		//this.tenantId = tenantId;
		//this.entityId = entityId;
		this.name = name;
		this.alias = alias;
		this.desc = desc;
		this.dataType = dataType;
		this.length = length;
		this.precise = precise;
		this.refEntity = refId;
		this.nullable = nullable;
		this.defaultValue = defaultValue;
		this.fieldNum = fieldNum;
		this.fieldName = fieldName;
		this.isIndex = isIndex;
		this.isUnique = isUnique;
		this.deleted = deleted;
		this.isFlex = isFlex;
		
	}
	

	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getPrecise() {
		return precise;
	}
	public void setPrecise(int precise) {
		this.precise = precise;
	}

	
	
	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isIndex() {
		return isIndex;
	}
	public void setIndex(boolean isIndex) {
		this.isIndex = isIndex;
	}
	public boolean isUnique() {
		return isUnique;
	}
	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}
	public String getRefEntity() {
		return refEntity;
	}
	public void setRefEntity(String refEntity) {
		this.refEntity = refEntity;
	}
	
	
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public int getFieldNum() {
		return fieldNum;
	}
	public void setFieldNum(int fieldNum) {
		this.fieldNum = fieldNum;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public boolean isFlex() {
		return isFlex;
	}
	public void setFlex(boolean isFlex) {
		this.isFlex = isFlex;
	}
	
	public boolean isSystemField()
	{
		return !isFlex;
	}
	
}
