package edu.pku.sei.proj;


import static org.junit.Assert.*;

import java.util.List;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.Modifier;
import org.junit.Test;
import org.junit.Ignore;;


public class ProInfo2Test {

	@Test
	public void test_Chart_1(){
		final String javaVersion = JavaCore.VERSION_1_7;
		String srcRoot = "/home/nightwish/workspace/defects4j/src/chart/chart_1_buggy/source";
		String testRoot = null;
		String project = "chart_1";
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, javaVersion);
		proInfo.collectProInfo2();
		ProjectRepre proRe = proInfo.getProjectRepre();
		
		//chart-1 has 40 packages
		assertEquals(40, proRe.allPackageMap.size());
		
		//chart-1 has 654 java files, while 656 class. 
		//Two un-public classes are not main cls of the java file: WindDataItem and JFreeChartInfo
		int clientClsNum = 0;
		for(ClassRepre cls : proRe.fullNameToClazzesMap.values()) {
			if(cls.isLibaryClz() == false && cls.getName().contains("$") == false) {
				clientClsNum++;
			}
		}
		
		assertEquals(656, clientClsNum);
	}
	
	@Test
	public void test_Chart_1_AbstractCategoryItemRenderer() {
		final String javaVersion = JavaCore.VERSION_1_7;
		String srcRoot = "/home/nightwish/workspace/defects4j/src/chart/chart_1_buggy/source";
		String testRoot = null;
		String project = "chart_1";
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, javaVersion);
		
		proInfo.collectProInfo2();
		
		PackageRepre pkgRepre = proInfo.getProjectRepre().getPackage("org.jfree.chart.renderer.category");
		ClassRepre clsRepre = pkgRepre.getClassRepre("AbstractCategoryItemRenderer");
		
		int fileFlds = 0;
		for(FieldRepre fld : clsRepre.getFields()){
			if(fld.getCls() == clsRepre){
				fileFlds++;
			}
		}
		assertEquals(fileFlds, 15);
		
		int fileMtd = 0;
		for(MethodRepre mtd : clsRepre.getMethods()){
			if(mtd.getCls() == clsRepre){
				fileMtd++;
			}
		}
		assertEquals(fileMtd, 66);
		//public boolean isSeriesVisibleInLegend(int series), decleared in the father class AbstractRenderer
		List<MethodRepre> res = clsRepre.getMethodRepreByName("isSeriesVisibleInLegend");
		assertEquals(1, res.size());
		MethodRepre mtd0 = res.get(0);
		assertEquals("AbstractRenderer", mtd0.getCls().getName());
		assertFalse(Modifier.isStatic(mtd0.getFlag()));
		assertEquals(1, mtd0.getParams().size());
		assertEquals("series", mtd0.getParams().get(0).getName());
		
		//baseLegendTextFont is a private fld of father cls
		assertNull(clsRepre.getFieldRepreByName("baseLegendTextFont"));
		assertEquals(clsRepre.getFatherCls().getFieldRepreByName("baseLegendTextFont").getType(), "Font");
	}
	
	@Test
	public void test_Math1_GaussNewtonOptimizer(){
		final String javaVersion = JavaCore.VERSION_1_7;

		String srcRoot = "/home/nightwish/workspace/defects4j/src/math/math_1_buggy/src/main/java";
		String testRoot = null;
		String project = "math_1";
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, javaVersion);
		proInfo.collectProInfo2();
		
		ClassRepre clsRepre = proInfo.getProjectRepre().getPackage("org.apache.commons.math3.fitting.leastsquares").getClassRepre("GaussNewtonOptimizer");
		
		assertEquals(clsRepre.getFatherCls().getName(), "AbstractLeastSquaresOptimizer");
		
		List<MethodRepre> methods = clsRepre.getMethodRepreByName("doOptimize");
		assertEquals(methods.size(), 1);
		MethodRepre mtd0 = methods.get(0);
		assertEquals(mtd0.getParams().size(), 0);
	}

	@Test
	public void test_Math_37(){
		String srcRoot = "/home/nightwish/workspace/defects4j/src/math/math_37_buggy/src/main/java";
		String testRoot = "/home/nightwish/workspace/defects4j/src/math/math_37_buggy/src/test/java";
		String project = "math_37";
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, null);
		proInfo.collectProInfo2();
		
		ProjectRepre proj = proInfo.getProjectRepre();
		
//		for(ClassRepre cls : proj.fullNameToClazzesMap.values()){
//			System.out.println(cls);
//		}
		
		assertFalse(proj.fullNameToClazzesMap.get("org.apache.commons.math.util.FastMath") == null);
	}
	
	@Test
	public void test_Math_37_DfpDec(){
		String srcRoot = "/home/nightwish/workspace/defects4j/src/math/math_37_buggy/src/main/java";
		String testRoot = "/home/nightwish/workspace/defects4j/src/math/math_37_buggy/src/test/java";
		String project = "math_37";
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, null);
		proInfo.collectProInfo2();
		
		ProjectRepre proj = proInfo.getProjectRepre();
		
//		for(ClassRepre cls : proj.fullNameToClazzesMap.values()){
//			System.out.println(cls);
//		}
		
		ClassRepre cls = proj.fullNameToClazzesMap.get("org.apache.commons.math.dfp.DfpDec");
		
		assertTrue(cls.getFieldRepreByName("mant") != null);
		System.out.println(cls.getFieldRepreByName("mant"));
		
	}
	
	@Test
	public void test_Math_75(){
		String srcRoot = "/home/nightwish/workspace/defects4j/src/math/math_75_buggy/src/main/java";
		String testRoot = "/home/nightwish/workspace/defects4j/src/math/math_75_buggy/src/test/java";
		String project = "math_75";
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, null);
		proInfo.collectProInfo2();
		
		ProjectRepre proj = proInfo.getProjectRepre();
		
//		for(ClassRepre cls : proj.fullNameToClazzesMap.values()){
//			System.out.println(cls);
//		}
		
		ClassRepre cls = proj.fullNameToClazzesMap.get("org.apache.commons.math.stat.descriptive.ListUnivariateImpl");
		assertNotNull(cls);
				
	}
	
	@Deprecated
	@Ignore
	@Test
	public void test_Lang_34(){
		String srcRoot = "/home/nightwish/workspace/defects4j/src/lang/lang_34_buggy/src/main/java/";
		String testRoot = "/home/nightwish/workspace/defects4j/src/lang/lang_34_buggy/src/test/java/";
		String project = "lang_34";
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, null);
		proInfo.collectProInfo2();
		
		ProjectRepre proj = proInfo.getProjectRepre();
		
//		for(ClassRepre cls : proj.fullNameToClazzesMap.values()){
//			System.out.println(cls);
//		}
		
		ClassRepre cls = proj.fullNameToClazzesMap.get("org.apache.commons.lang3.builder.ToStringStyle");
		assertNotNull(cls);
		
		assertTrue(cls.getFieldRepreByName("REGISTRY") != null);
		System.out.println(cls.getFieldRepreByName("REGISTRY"));
	}
	
	
	@Test
	public void test_JSci(){
		String srcRoot = "/home/nightwish/workspace/gitrepos/math_relative/JSci";
		String testRoot = null;
		String project = "JSci";
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, null);
		proInfo.collectProInfo2();
		
		assertFalse(proInfo.getProjectRepre().allPackageMap.isEmpty());
		
		assertNotNull(proInfo.getProjectRepre().fullNameToClazzesMap.get("JSci.astro.telescope.LX200DebugServer"));
		
		for (ClassRepre cls : proInfo.getProjectRepre().fullNameToClazzesMap.values()) {
			System.out.println(cls);
		}
	}
	
	@Test
	public void test_JDK7_Math(){
		String srcRoot = "/home/nightwish/workspace/gitrepos/math_relative/jdk7_math";
		String testRoot = null;
		String project = "jdk7_math";
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, null);
		proInfo.collectProInfo2();
		
		assertFalse(proInfo.getProjectRepre().allPackageMap.isEmpty());
		
		for (ClassRepre cls : proInfo.getProjectRepre().fullNameToClazzesMap.values()) {
			System.out.println(cls);
		}
		
		assertNotNull(proInfo.getProjectRepre().fullNameToClazzesMap.get("java.lang.String"));
		
		ClassRepre mathCls = proInfo.getProjectRepre().fullNameToClazzesMap.get("java.lang.Math");
		assertNotNull(mathCls);
		
		System.out.println(mathCls.getMethodRepreByName("abs"));
	}
	
	@Test
	public void test_JDK7_Time(){
		String srcRoot = "/home/nightwish/workspace/gitrepos/time_repative/jdk7_time";
		String testRoot = null;
		String project = "jdk7_time";
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, null);
		proInfo.collectProInfo2();
		
		assertFalse(proInfo.getProjectRepre().allPackageMap.isEmpty());
		
		for (ClassRepre cls : proInfo.getProjectRepre().fullNameToClazzesMap.values()) {
			System.out.println(cls);
		}
		ClassRepre dataCls = proInfo.getProjectRepre().fullNameToClazzesMap.get("java.util.Date");
		assertNotNull(dataCls);
		
		for(FieldRepre field: dataCls.getFields()) {
			if(field.getName().equals("wtb")) {
				assertEquals(field.getType(), "String[]");
			}
		}
	}
	
	/**
	 * Make sure the branch is camel-3388
	 */
	@Test
	public void test_Camel_3388() {
		String srcRoot = "/home/nightwish/workspace/bug_repair/bugs-dot-jar/camel/camel-core/src/main/java";
		String testRoot = "/home/nightwish/workspace/bug_repair/bugs-dot-jar/camel/camel-core/src/test/java";
		
		String project = "camel_3388";
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, "1.7");
		proInfo.collectProInfo2();
		
		ClassRepre combinerCls = proInfo.getProjectRepre().fullNameToClazzesMap.get("org.apache.camel.component.bean.BeanWithHeadersAndBodyInject3Test");
		
		assertNotNull(combinerCls);
		assertEquals("ContextTestSupport", combinerCls.getFatherCls().getName());
	
		assertEquals("TestSupport", combinerCls.getFatherCls().getFatherCls().getName());
		
		ClassRepre testCase = combinerCls.getFatherCls().getFatherCls().getFatherCls();
		assertNotNull(testCase);
		assertEquals("TestCase", testCase.getName());
		assertTrue(testCase.isLibaryClz());
	}
	
}
