package edu.pku.sei.proj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ProInfo {
	private String proName;
	private String srcRoot;
	private String testRoot;

	private ProjectRepre projectRepre;
	
	public static Set<String> javaDotLangClasses = new HashSet<>();
	
	static{
		try {
			String path = Class.class.getClass().getResource("/").getPath();
			File root = new File(path);
			root = root.getParentFile();
			FileReader fr = new FileReader(root.getAbsolutePath() + "/java_lang_clazzes.txt");
			BufferedReader br = new BufferedReader(fr);
			String curLine = null;
			while((curLine = br.readLine()) != null){
				javaDotLangClasses.add(curLine.trim());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args){
//		String srcRoot = "/home/nightwish/workspace/defects4j/src/math/math_37_buggy/src/main/java/";
//		String testRoot = "/home/nightwish/workspace/defects4j/src/math/math_37_buggy/src/test/java";
//		String project = "math_37";
		
		String srcRoot = "/home/nightwish/workspace/defects4j/src/chart/chart_1_buggy/source";
		String testRoot = null;
		String project = "chart_1";
//		if(args.length != 3){
//			System.err.println("ERR ARGS NUM");
//			return;
//		}
//		String project = args[0];
//		String srcRoot = args[1];
//		String testRoot = args[2];
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot);
		
		proInfo.collectProInfo();
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

	public void collectProInfo(){
		File rootFile = new File(srcRoot);
		this.traverseSrcFolderFirstLoop(rootFile, "");
		this.traverseSrcFolderSecondLoop(rootFile, "");
		this.cleanUp();
		this.mergeUntilFix();
		System.out.println("PROJECT INFO FINISHED FOR " + proName);
	}
	
	private void cleanUp(){
		projectRepre.removeEmptyPkgs();

	}
	
	private void mergeUntilFix(){
		Set<ClassRepre> holdSuperCls = new HashSet<>();
		for(ClassRepre cls : projectRepre.fullNameToClazzesMap.values()){
			if(cls.getFatherCls() != null){
				holdSuperCls.add(cls);
			}
		}

		boolean changed = false;
		do{
			changed = false;
			
			for(ClassRepre cls : holdSuperCls){
				
				ClassRepre father = cls.getFatherCls();
				
				if(addFatherFldAndMtd(cls, father)){
					changed = true;
				}
				
			}
		}while(changed);
		
	}
	
	
	private boolean addFatherFldAndMtd(ClassRepre child, ClassRepre father){
		
		boolean changed = false;
		
		for(FieldRepre fatherFld : father.getFields()){
			if(fatherFld.isInherable()){
				boolean alreadyHas = false;
				for(FieldRepre cldFld : child.getFields()){
					//cld has the field already
					if(cldFld.getName().equals(fatherFld.getName())){
						alreadyHas = true;
						break;
					}
				}
				if(!alreadyHas){
					child.getFields().add(fatherFld);
					changed = true;
				}
			}
		}
		for(MethodRepre fatherMtd : father.getMethods()){
			if(fatherMtd.isInherable()){
				boolean alreadyHas = false;
				for(MethodRepre cldMtd : child.getMethods()){
					//cld has the method already, @Override is checked? Need 'cldMtd.isSameTo(fatherMtd)' ?
					if(fatherMtd.isSameTo(cldMtd)){
						alreadyHas = true;
						break;
					}
				}
				if(!alreadyHas){
					child.getMethods().add(fatherMtd);
					changed = true;
				}
				
			}
		}
		
		
		return changed;
	}
	
	private void traverseSrcFolderFirstLoop(File root, String curPkg){
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
				
				traverseSrcFolderFirstLoop(files[i], childPkg);
				
			} else if (fileName.endsWith(".java")) {
				
				CompilationUnit cu = (CompilationUnit) JavaFile.genASTFromSource(JavaFile.readFileToString(files[i]), ASTParser.K_COMPILATION_UNIT);
				
//				System.out.println(curPkg);
//				System.out.println(files[i].getName());

				String mainClsName = fileName.substring(0, fileName.length() - 5);

				ClassRepre mainCls = projectRepre.getOrNewClassRepre(pkgRepre, files[i], mainClsName);

				ClsCollectorVisitor visitor = new ClsCollectorVisitor(files[i], projectRepre, pkgRepre, mainCls);
				
				cu.accept(visitor);
				
			} else {
				continue;
			}
		}
	}
	
	private void traverseSrcFolderSecondLoop(File root, String curPkg){
		File[] files = root.listFiles();
		
		if(files == null){
			return;
		}
		
		PackageRepre pkgRepre = projectRepre.getPackage(curPkg);
		
		
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i].getName();
			
			if (files[i].isDirectory()) {
				
				String childPkg = null;
				if(curPkg.equals("")){
					childPkg = files[i].getName();
				}else{
					childPkg = curPkg + "." + files[i].getName();
				}
				
				traverseSrcFolderSecondLoop(files[i], childPkg);
				
			} else if (fileName.endsWith(".java")) {
				
				CompilationUnit cu = (CompilationUnit) JavaFile.genASTFromSource(JavaFile.readFileToString(files[i]), ASTParser.K_COMPILATION_UNIT);
				
//				System.out.println(curPkg);
//				System.out.println(files[i].getName());

				String mainClsName = fileName.substring(0, fileName.length() - 5);

				ClassRepre mainCls = projectRepre.getClassRepre(pkgRepre, mainClsName);

				assert mainCls != null;
				
				ProjectVisitor visitor = new ProjectVisitor(files[i], projectRepre, pkgRepre, mainCls);
				
				cu.accept(visitor);
				
			} else {
				continue;
			}
		}
	}
}
