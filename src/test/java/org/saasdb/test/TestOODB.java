package org.saasdb.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.saasdb.convert.UUIDGenerator;
import org.saasdb.dd.DDOperatorImpl;
import org.saasdb.dd.DMOperatorImpl;
import org.saasdb.dd.SystemField;
import org.saasdb.meta.DataType;
import org.saasdb.meta.Entity;
import org.saasdb.meta.Field;

public class TestOODB {

	DDOperatorImpl ddo;
	DMOperatorImpl dmo;
	String tenantId;
	String user;

	@Before
	public void setUp() throws Exception {
		ddo = new DDOperatorImpl();
		dmo = new DMOperatorImpl();
		ddo.setDmOperator(dmo);
		dmo.setDDOperator(ddo);
		this.tenantId = "0";
		this.user = "admin";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAll() {
		testDeleteEntity();
		testCreateEntity();
		testAddField();
		testInsertData();
		
		testQueryData();
	}

	public void testDeleteEntity() {
		Entity entity = new Entity();
		entity.setName("Student");
		
		ddo.deleteEntity(tenantId, entity);

		entity = new Entity();
		entity.setName("School");
		
		ddo.deleteEntity(tenantId, entity);
	}

	public void testCreateEntity() {
		testCreateSchool();
		testCreateStudent();
	}
	
	public void testCreateSchool() {
		Entity entity = new Entity();
		entity.setName("School");
		entity.setAlias("School");
		entity.setDesc("School desc");
		entity.setCreatedUser(user);
		entity.setLastModifiedUser(user);

		ddo.createEntity(tenantId, entity);
	}
	
	public void testCreateStudent() {
		Entity entity = new Entity();
		entity.setName("Student");
		entity.setAlias("Student");
		entity.setDesc("Student desc");
		entity.setCreatedUser(user);
		entity.setLastModifiedUser(user);

		ddo.createEntity(tenantId, entity);
	}

	public void testAddField() {
		
		testAddSchoolField();
		testAddStudentField();
		
	}
	
	public void testAddSchoolField() {
		Field field = new Field();
		field.setName("desc");
		field.setAlias("描述");
		field.setDesc("描述");
		field.setDefaultValue(null);
		field.setDataType(DataType.TEXT);
		field.setIndex(false);
		field.setNullable(false);
		field.setCreatedUser(user);
		field.setLastModifiedUser(user);
		ddo.createFlexField(tenantId, "School", field);
		field = new Field();
		field.setName("name");
		field.setAlias("名称");
		field.setDesc("");
		field.setDataType(DataType.TEXT);
		field.setIndex(true);
		field.setUnique(true);
		field.setNullable(false);
		field.setCreatedUser(user);
		field.setLastModifiedUser(user);
		ddo.createFlexField(tenantId, "School", field);
	}
	
	public void testAddStudentField() {
		Field field = new Field();
		field.setName("gender");
		field.setAlias("性别");
		field.setDesc("性别");
		field.setDefaultValue("1");
		field.setDataType(DataType.INT);
		field.setIndex(true);
		field.setNullable(false);
		field.setCreatedUser(user);
		field.setLastModifiedUser(user);
		ddo.createFlexField(tenantId, "Student", field);
		field = new Field();
		field.setName("name");
		field.setAlias("名称");
		field.setDesc("");
		field.setDataType(DataType.TEXT);
		field.setIndex(true);
		field.setUnique(true);
		field.setNullable(false);
		field.setCreatedUser(user);
		field.setLastModifiedUser(user);
		ddo.createFlexField(tenantId, "Student", field);
		field = new Field();
		field.setName("age");
		field.setAlias("年龄");
		field.setDesc("");
		field.setDataType(DataType.INT);
		field.setIndex(true);
		field.setUnique(false);
		field.setNullable(false);
		field.setCreatedUser(user);
		field.setLastModifiedUser(user);
		ddo.createFlexField(tenantId, "Student", field);
		field = new Field();
		field.setName("score");
		field.setAlias("成绩");
		field.setDesc("");
		field.setDataType(DataType.NUMERIC);
		field.setIndex(true);
		field.setUnique(false);
		field.setNullable(true);
		field.setCreatedUser(user);
		field.setLastModifiedUser(user);
		ddo.createFlexField(tenantId, "Student", field);
		field = new Field();
		field.setName("school");
		field.setAlias("学校");
		field.setDesc("学校");
		field.setDataType(DataType.LOOKUP);
		field.setRefEntity("School");
		field.setIndex(false);
		field.setNullable(true);
		field.setCreatedUser(user);
		field.setLastModifiedUser(user);

		ddo.createFlexField(tenantId, "Student", field);
	}

	public void testInsertData() {
		
		Map<String, Object> value = new HashMap<>();
		
		Map<String, Object> school1 = new HashMap<>();
		school1.put("id", UUIDGenerator.newUUID(tenantId, "School"));
		school1.put("name", "school1");
		school1.put("desc", "sz university.");
		school1.put(SystemField.lastModifiedUser.getName(), "admin");
		school1.put(SystemField.createdUser.getName(), "admin");
		dmo.insertData(tenantId, "School", school1);

		Map<String, Object> school2 = new HashMap<>();
		school2.put("id", UUIDGenerator.newUUID(tenantId, "School"));
		school2.put("name", "school2");
		school2.put("desc", "school2 university.");
		school2.put(SystemField.lastModifiedUser.getName(), "admin");
		school2.put(SystemField.createdUser.getName(), "admin");
		dmo.insertData(tenantId, "School", school2);
		
		value = new HashMap<>();
		value.put("id", UUIDGenerator.newUUID(tenantId, "Student"));
		value.put("name", "lzx");
		value.put("gender", 1);
		value.put("age", 30);
		value.put("score", 99.5);
		value.put("school", school1);
		value.put(SystemField.lastModifiedUser.getName(), "admin");
		value.put(SystemField.createdUser.getName(), "admin");
		dmo.insertData(tenantId, "Student", value);
		
		value = new HashMap<>();
		value.put("id", UUIDGenerator.newUUID(tenantId, "Student"));
		value.put("name", "natasha");
		value.put("gender", 0);
		value.put("age", 30);
		value.put("score", 90.5);
		value.put("school", school2);
		value.put(SystemField.lastModifiedUser.getName(), "admin");
		value.put(SystemField.createdUser.getName(), "admin");
		dmo.insertData(tenantId, "Student", value);

		value = new HashMap<>();
		value.put("id", UUIDGenerator.newUUID(tenantId, "Student"));
		value.put("name", "chong");
		value.put("gender", 0);
		value.put("age", 7);
		value.put("score", 90.5);
		value.put("school", school2);
		value.put(SystemField.lastModifiedUser.getName(), "admin");
		value.put(SystemField.createdUser.getName(), "admin");
		dmo.insertData(tenantId, "Student", value);
	}

	public void testGetData() {
		
	}

	public void testQueryData() {
		String[] selectFields = new String[]{"name","gender","age","score","school"};
		Map<String, Object> whereMap = new HashMap<>();
		whereMap.put("age", 30);
		Iterator<Map<String, Object>> result = null;//dmo.queryData(tenantId, "Student", whereMap, selectFields);
		//printResult(result);
		whereMap = new HashMap<>();
		whereMap.put("age", 30);
		whereMap.put("gender", 1);
		result = dmo.queryData(tenantId, "Student", whereMap, selectFields);
		printResult(result);
	}
	
	private void printResult(Iterator<Map<String, Object>> result)
	{
		while(result.hasNext())
			System.out.println(result.next());
	}
}
