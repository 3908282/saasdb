package org.saasdb.dd;

import org.saasdb.meta.DataType;
import org.saasdb.meta.Field;
import org.saasdb.raw.Constants;

public class SystemField extends Field{

	public SystemField(String name, String alias, String desc, int dataType,
			int length, int precise, boolean nullable, String defaultValue, String fieldName, boolean isIndex, boolean isUnique) {
		super(name, alias, desc, dataType, length, precise, null, nullable, defaultValue, -1, fieldName, isIndex, isUnique, false, false);
	}

	//T_DATA表
	public static SystemField tenantId = new SystemField(Constants.SystemKeys.TETANTID,"tenantId","",DataType.UUID, 28, 0, false, null, "FTENANTID", false, true);
	public static SystemField instanceId = new SystemField("instanceId","Object instance Id","",DataType.UUID, 28, 0, false, null, "FINSTANCEID", false, true);
	//public static SystemField name = new SystemField("name","name","Instance Name",DataType.TEXT, 28, 0, false, null, "FNAME", false, true);
	//public static SystemField entityId = new SystemField("entityId","entityId","",DataType.UUID, 28, 0, false, null, "FENTITYID", false, true);
	public static SystemField entityName = new SystemField(Constants.SystemKeys.ENTITYNAME,"entityName","",DataType.TEXT, 28, 0, false, null, "FENTITYNAME", false, true);
	public static SystemField deleted = new SystemField(Constants.SystemKeys.DELETED,"deleted","",DataType.BOOLEAN, 28, 0, false, null, "FDELETED", false, true);
	public static SystemField deletedTime = new SystemField(Constants.SystemKeys.DELETEDTIME,"deletedTime","",DataType.DATETIME, 28, 0, false, null,"FDELETEDTIME", false, true);
	public static SystemField createdUser = new SystemField(Constants.SystemKeys.CREATEDUSER,"createdUser","",DataType.UUID, 28, 0, false, null, "FCREATEDUSER", false, true);
	public static SystemField createdTime = new SystemField(Constants.SystemKeys.CREATEDTIME,"createdTime","",DataType.DATETIME, 28, 0, false, null,"FCREATEDTIME", false, true);
	public static SystemField lastModifiedUser = new SystemField(Constants.SystemKeys.LASTMODIFIEDUSER,"lastModifiedUser","",DataType.UUID, 28, 0, false, null, "FLASTMODIFIEDUSER", false, true);
	public static SystemField lastModifiedTime = new SystemField(Constants.SystemKeys.LASTMODIFIEDTIME,"lastModifiedTime","",DataType.DATETIME, 28, 0, false, null,"FLASTMODIFIEDTIME", false, true);

	public static SystemField[] defaultSystemFields = new SystemField[]{
			tenantId,entityName,instanceId,deleted,deletedTime, createdUser,createdTime,lastModifiedUser,lastModifiedTime
	};
	
	public static SystemField[] getAllSystemFields(){
		return defaultSystemFields;
	}
	
	//T_BLOB,T_CLOB
	public static SystemField blobValue = new SystemField("value","blob value","",DataType.BLOB, 28, 0, false, null, "FBLOB", false, true);
	public static SystemField clobValue = new SystemField("value","clob value","",DataType.CLOB, 28, 0, false, null, "FCLOB", false, true);
	
	//T_INDEX
	public static SystemField fieldNum = new SystemField("fieldNum","fieldNum","",DataType.INT, 0, 0, false, null, "FFieldNum", false, true);
	public static SystemField stringValue = new SystemField("stringValue","string value","",DataType.TEXT, 2000, 0, false, null, "FValue", false, true);
	public static SystemField stringValueU = new SystemField("stringValueU","string value uppercase","",DataType.TEXT, 2000, 0, false, null, "FValueU", false, true);
	public static SystemField booleanValue = new SystemField("booleanValue","boolean value","",DataType.BOOLEAN, 0, 0, false, null, "FValue", false, true);
	public static SystemField intValue = new SystemField("intValue","int value","",DataType.INT, 0, 0, false, null, "FValue", false, true);
	public static SystemField numericValue = new SystemField("numericValue","numeric value","",DataType.NUMERIC, 2000, 0, false, null, "FValue", false, true);
	public static SystemField dateValue = new SystemField("dateValue","date value","",DataType.DATETIME, 2000, 0, false, null, "FValue", false, true);
	public static SystemField timeValue = new SystemField("timeValue","time value","",DataType.DATETIME, 2000, 0, false, null, "FValue", false, true);

	
	//T_INDEX
	public static SystemField relationshipId = new SystemField("relationshipId","relationshipId","",DataType.UUID, 28, 0, false, null, "FRelationshipId", false, true);
	public static SystemField targetEntityName = new SystemField("targetEntityName","targetEntityName","",DataType.TEXT, 28, 0, false, null, "FTargetEntityName", false, true);
	public static SystemField targetInstanceId = new SystemField("targetInstanceId","targetInstanceId","",DataType.UUID, 28, 0, false, null, "FTargetInstanceId", false, true);
	
}
