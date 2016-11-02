package org.saasdb.convert;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.saasdb.DBException;
import org.saasdb.meta.DataType;
import org.saasdb.meta.Entity;

public class FlexValueConvert {
	
	public static String toFlexValue(Entity entity, int dataType, Object value)
	{
		if(value==null)
			return null;
		
		switch(dataType)
		{
		case DataType.CHAR:
		case DataType.TEXT:
		case DataType.UUID:
		case DataType.INT:
		case DataType.NUMERIC:
		case DataType.BOOLEAN:
			return value.toString();
		case DataType.DATE:
		case DataType.TIME:
		case DataType.DATETIME:
			return dateToString(value);
		case DataType.LOOKUP:
			return toId(entity, value);
		default:
			throw new DBException("Unsupported datatype:" + dataType);
		}
	}
	
	private static String toId(Entity entity, Object value)
	{
		int dataType = entity.getIdFieldDataType();
		if(value instanceof String || value instanceof Number)
			return value.toString();
		else
		{
			if(value instanceof Map)
			{
				Object id = ((Map)value).get(entity.getIdFieldName());
				if(id!=null)
					return id.toString();
			}
			else
			{
				try
				{
					Method method = value.getClass().getMethod("getId",new Class[0]);
					if(method!=null)
					{
						Object id = method.invoke(value, new Object[0]);
						if(id!=null)
							return id.toString();
						
					}
					else
					{
						java.lang.reflect.Field field = value.getClass().getDeclaredField(entity.getIdFieldName());
						if(field!=null)
						{
							field.setAccessible(true);
							Object id = field.get(value);
							if(id!=null)
								return id.toString();
						}
						
					}
				}catch(Throwable t){}
				
			}
		}
		return null;
	}
	
	public static Number toNumber(Object value)
	{
		return (Number)value;
	}
	
	public static int toInt(Object value)
	{
		if(value==null)
			return 0;
		if(value instanceof Number)
			return ((Number)value).intValue();
		else
			return Integer.valueOf(value.toString());
			
	}
	
	public static Object toDate(Object value) {
		if(value instanceof Date)
		{
			return new Timestamp(((Date)value).getTime());
		}
		else if(value instanceof Calendar)
		{
			return new Timestamp(((Calendar)value).getTimeInMillis());
		}
		else
			throw new DBException("Not date/time value:" + value.toString());

	}
	
	public static Object fromFlexValue(int dataType, String value)
	{
		if(value==null)
			return null;
		
		switch(dataType)
		{
		case DataType.CHAR:
		case DataType.TEXT:
		case DataType.UUID:
			return value;
		case DataType.INT:
			return Integer.valueOf(value);
		case DataType.NUMERIC:
			return new BigDecimal(value);
		case DataType.BOOLEAN:
			return Boolean.valueOf(value);
		case DataType.DATE:
		case DataType.TIME:
		case DataType.DATETIME:
			return stringToDate(value);
		case DataType.LOOKUP:
			return toLookupObject(value);
		default:
			throw new DBException("Unsupported datatype:" + dataType);
		}
	}
	
	//TODO: to target object
	private static Object toLookupObject(String value) {
		
		return value;
	}

	private static Object stringToDate(String value) {
		return new java.sql.Time(Long.parseLong(value));

	}

	private static String dateToString(Object value) {
		if(value instanceof Date)
		{
			return "" + (((Date)value).getTime());
		}
		else if(value instanceof Calendar)
		{
			return "" + ((Calendar)value).getTimeInMillis();
		}
		else
			throw new DBException("Not date/time value:" + value.toString());
	}

	public static String toUpperCase(String value)
	{
		if(value==null)
			return value;
		
		return value.toUpperCase();
	}


	
}
