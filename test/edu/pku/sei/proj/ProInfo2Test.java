package edu.pku.sei.proj;


import static org.junit.Assert.*;

import java.util.List;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.Modifier;
import org.junit.Test;

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
		assertEquals(656, proRe.fullNameToClazzesMap.size());
	}
	
	@Test
	public void test_Chart_1_AbstractCategoryItemRenderer() {
		final String javaVersion = JavaCore.VERSION_1_7;
		String srcRoot = "/home/nightwish/workspace/defects4j/src/chart/chart_1_buggy/source";
		String testRoot = null;
		String project = "chart_1";
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, javaVersion);
		
		proInfo.collectProInfo2();
		
		ClassRepre clsRepre = proInfo.getProjectRepre().getPackage("org.jfree.chart.renderer.category").getClassRepre("AbstractCategoryItemRenderer");
		
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
		assertEquals(res.size(), 1);
		MethodRepre mtd0 = res.get(0);
		assertEquals(mtd0.getCls().getName(), "AbstractRenderer");
		assertFalse(Modifier.isStatic(mtd0.getFlag()));
		assertEquals(mtd0.getParams().size(), 1);
		assertEquals(mtd0.getParams().get(0).getName(), "series");
		
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
	}
	
}
