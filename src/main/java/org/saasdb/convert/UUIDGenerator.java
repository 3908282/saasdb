package org.saasdb.convert;

import java.util.UUID;

public class UUIDGenerator {
	public static String newUUID(String tenantId, String entityName)
	{
		return UUID.randomUUID().toString();
	}
}
