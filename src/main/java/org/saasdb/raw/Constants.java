package org.saasdb.raw;

import org.saasdb.meta.DataType;
import org.saasdb.meta.Field;

public class Constants {
	public static class SystemTableNames
	{
		public static String TENANT = "T_TENANT";
		
		public static String ENTITY = "T_ENTITY";
		public static String FIELD = "T_FIELD";
		public static String RELATIONSHIP = "T_RELATIONSHIP";
		public static String  INDEX= "T_INDEX";
		public static String UNIQUEINDEX = "T_UNIQUE";	
		public static String DATA = "T_DATA";
		public static String BLOB = "T_BLOB";
		public static String CLOB = "T_CLOB";
	}
	
	public static class SystemFieldNames
	{
		public static final String TETANTID = "ftenantid";
		public static final String UUID = "fuuid";
		public static final String ID = "fid";
		public static final String INSTANCEID = "finstanceid";
		
		
		public static final String ENTITYNAME = "fentityname";
		
		public static final String CREATEDUSER = "fcreateduser";
		public static final String CREATEDTIME = "fcreatedtime";
		public static final String LASTMODIFIEDUSER = "flastmodifieduser";
		public static final String LASTMODIFIEDTIME = "flastmodifiedtime";
		
		public static final String NAME = "fname";
		public static final String ALIAS = "falias";
		public static final String DESC = "fdesc";
		public static final String CLASSNAME = "fclassname";
		public static final String IDFIELDNAME = "fidfieldname";
		public static final String IDFIELDDATATYPE = "fidfielddatatype";
		
		public static final String DATATYPE = "fdatatype";
		public static final String LENGTH = "flength";
		public static final String PRECISE = "fprecise";
		public static final String REFENTITY = "frefentity";
		
		public static final String NULLABLE = "fnullable";
		public static final String DEFAULTVALUE = "fdefaultvalue";
		
		public static final String FIELDNUM = "ffieldnum";
		public static final String FIELDNAME = "ffieldname";
		
		public static final String ISINDEX = "fisindex";
		public static final String ISUNIQUE = "fisunique";
		
		public static final String DELETED = "fdeleted";
		public static final String DELETEDTIME = "fdeletedtime";
	}
	
	public static class SystemKeys
	{
		public static final String TETANTID = "tenantId";

		public static final String UUID = "uuid";
		public static final String ID = "id";
		public static final String INSTANCEID = "instanceId";
		
		public static final String ENTITYNAME = "entityName";
		
		public static final String CREATEDUSER = "createdUser";
		public static final String CREATEDTIME = "createdTime";
		public static final String LASTMODIFIEDUSER = "lastModifiedUser";
		public static final String LASTMODIFIEDTIME = "lastModifiedTime";
		
		public static final String NAME = "name";
		public static final String ALIAS = "alias";
		public static final String DESC = "desc";
		public static final String CLASSNAME = "className";
		public static final String IDFIELDNAME = "idFieldName";
		public static final String IDFIELDDATATYPE = "idFieldDataType";
		
		public static final String DATATYPE = "dataType";
		public static final String LENGTH = "length";
		public static final String PRECISE = "precise";
		public static final String REFENTITY = "refEntity";
		
		public static final String NULLABLE = "nullable";
		public static final String DEFAULTVALUE = "defaultValue";
		
		public static final String FIELDNUM = "fieldNum";
		public static final String FIELDNAME = "fieldName";
		public static final String ISINDEX = "isIndex";
		public static final String ISUNIQUE = "isUnique";
		
		public static final String DELETED = "deleted";
		public static final String DELETEDTIME = "deletedTime";
	}
	
	public static String getIndexTable(Field field)
	{
		String table = field.isUnique()?Constants.SystemTableNames.UNIQUEINDEX:Constants.SystemTableNames.INDEX;
		switch(field.getDataType())
		{
		case DataType.INT:
			table += "_INT";
			break;
		case DataType.NUMERIC:
			table += "_NUMERIC";
			break;
		case DataType.BOOLEAN:
			table += "_BOOLEAN";
			break;	
		case DataType.CHAR:
		case DataType.TEXT:
		case DataType.UUID:
			table += "_STRING";
			break;
		case DataType.DATE:
			table += "_DATE";
			break;
		case DataType.TIME:
		case DataType.DATETIME:
			table += "_TIME";
			break;
			
		}
		return table;
	}
}
