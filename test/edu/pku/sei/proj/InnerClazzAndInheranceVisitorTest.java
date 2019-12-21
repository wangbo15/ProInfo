package edu.pku.sei.proj;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class InnerClazzAndInheranceVisitorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetImportedFatherClassName() {
		String imported = "renderer.AbstractRenderer";
		assertEquals(imported, InnerClazzAndInheranceVisitor.getImportedFatherClassName(imported));
		
		imported = "org.jfree.chart.renderer.AbstractRenderer";
		assertEquals(imported, InnerClazzAndInheranceVisitor.getImportedFatherClassName(imported));

		imported = "AbstractRenderer";
		assertEquals(imported, InnerClazzAndInheranceVisitor.getImportedFatherClassName(imported));
		
		imported = "org.apache.commons.math3.userguide.ExampleUtils.ExampleFrame";
		String inner = "org.apache.commons.math3.userguide.ExampleUtils$ExampleFrame";
		assertEquals(inner, InnerClazzAndInheranceVisitor.getImportedFatherClassName(imported));

	}

}
