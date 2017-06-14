package edu.pku.sei.proj;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProInfoTest {

	@Test
	public void test_Chart_1_AbstractCategoryItemRenderer() {
		String srcRoot = "/home/nightwish/workspace/defects4j/src/chart/chart_1_buggy/source";
		String testRoot = null;
		String project = "chart_1";
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot);
		
		proInfo.collectProInfo();
		
		
		ClassRepre clsRepre = proInfo.getProjectRepre().getPackage("org.jfree.chart.renderer.category").getClassRepre("AbstractCategoryItemRenderer");
		assertEquals(clsRepre.getFields().size(), 15);
		assertEquals(clsRepre.getMethods().size(), 66);
		
	}

}
