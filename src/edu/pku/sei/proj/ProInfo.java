package edu.pku.sei.proj;

import java.io.File;

import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ProInfo {
	private String proName;
	private String srcRoot;
	private String testRoot;

	private ProjectRepre projectRepre;
	
	public static void main(String[] args){
		String srcRoot = "/home/nightwish/workspace/defects4j/src/math/math_37_buggy/src/main/java/";
		String testRoot = "/home/nightwish/workspace/defects4j/src/math/math_37_buggy/src/test/java";
		ProInfo proInfo = new ProInfo("math_37", srcRoot, testRoot);
		
		File rootFile = new File(srcRoot);
		proInfo.traverseSrcFolder(rootFile, "");
	}
	
	public ProInfo(String proName, String srcRoot, String testRoot){
		this.proName = proName;
		this.srcRoot = srcRoot;
		this.testRoot = testRoot;
		this.projectRepre = new ProjectRepre(proName, srcRoot, testRoot);
	}
		
	public ProjectRepre getProjectRepre() {
		return projectRepre;
	}

	public void traverseSrcFolder(File root, String curPkg){
		File[] files = root.listFiles();
		
		if(files == null){
			return;
		}
		
		PackageRepre pkgRepre = projectRepre.getOrNewPackage(curPkg);
		
		
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i].getName();
			
			if (files[i].isDirectory()) {
				
				String childPkg = null;
				if(curPkg.equals("")){
					childPkg = files[i].getName();
				}else{
					childPkg = curPkg + "." + files[i].getName();
				}
				
				traverseSrcFolder(files[i], childPkg);
				
			} else if (fileName.endsWith(".java")) {
				
				CompilationUnit cu = (CompilationUnit) JavaFile.genASTFromSource(JavaFile.readFileToString(files[i]), ASTParser.K_COMPILATION_UNIT);
				
//				System.out.println(curPkg);
//				System.out.println(files[i].getName());

				String mainClsName = fileName.substring(0, fileName.length() - 5);

				ClassRepre mainCls = projectRepre.getOrNewClassRepre(pkgRepre, files[i], mainClsName);

				ProjectVisitor visitor = new ProjectVisitor(files[i], projectRepre, pkgRepre, mainCls);
				
				cu.accept(visitor);
				
			} else {
				continue;
			}
		}
	}
}
