package edu.pku.sei.proj;


import static org.junit.Assert.*;

import java.util.List;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.Modifier;
import org.junit.Test;

public class ProInfoTest {
	

	@Test
	public void test_Chart_1(){
		final String javaVersion = JavaCore.VERSION_1_7;
		String srcRoot = "/home/nightwish/workspace/defects4j/src/chart/chart_1_buggy/source";
		String testRoot = null;
		String project = "chart_1";
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, javaVersion);
		proInfo.collectProInfo();
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
		
		proInfo.collectProInfo();
		
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
		proInfo.collectProInfo();
		
		ClassRepre clsRepre = proInfo.getProjectRepre().getPackage("org.apache.commons.math3.fitting.leastsquares").getClassRepre("GaussNewtonOptimizer");
		
		assertEquals(clsRepre.getFatherCls().getName(), "AbstractLeastSquaresOptimizer");
		
		List<MethodRepre> methods = clsRepre.getMethodRepreByName("doOptimize");
		assertEquals(methods.size(), 1);
		MethodRepre mtd0 = methods.get(0);
		assertEquals(mtd0.getParams().size(), 0);
		
	}

}
