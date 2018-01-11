package edu.pku.sei.proj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ProInfo implements Serializable  {
	
	private static final long serialVersionUID = -7652134852831173670L;
	
	private String proName;
	private String srcRoot;
	private String testRoot;
	private String javaVersion = JavaCore.VERSION_1_7;
	
	private ProjectRepre projectRepre;
	
	private transient Map<File, CompilationUnit> fileToCuBuffer = new HashMap<>();
	private transient Map<File, PackageRepre> fileToPkgRepBuffer = new HashMap<>();
	
	public static Set<String> javaDotLangClasses = new HashSet<>();
	
	static{
		try {
//			String path = Class.class.getClass().getResource("/").getPath();
//			File root = new File(path);
//			root = root.getParentFile();
			
			//TODO: 
//			String path = "/home/nightwish/workspace/eclipse/ProInfo";
//			String path = System.getProperty("user.dir");
//			FileReader fr = new FileReader(path + "/java_lang_clazzes.txt");
			InputStream inputStream = ProInfo.class.getResourceAsStream("/java_lang_clazzes.txt");  
			
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));  
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
		String srcRoot = "/home/nightwish/workspace/defects4j/src/math/math_84_buggy/src/main/java";
		String testRoot = "/home/nightwish/workspace/defects4j/src/math/math_84_buggy/src/test/java";
		String project = "math_84";
		
//		String srcRoot = "/home/nightwish/workspace/defects4j/src/chart/chart_4_buggy/source/";
//		String testRoot = "/home/nightwish/workspace/defects4j/src/chart/chart_4_buggy/tests/";
//		String project = "chart_4";
		
//		String srcRoot = "/home/nightwish/workspace/defects4j/src/time/time_11_buggy/src/main/java/";
//		String testRoot = "/home/nightwish/workspace/defects4j/src/time/time_11_buggy/src/test/java/";
//		String project = "time_11";

//		String srcRoot = "/home/nightwish/workspace/defects4j/src/lang/lang_48_buggy/src/java/";
//		String testRoot = "/home/nightwish/workspace/defects4j/src/lang/lang_48_buggy/src/test/";
//		String project = "lang_48";
		
//		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, JavaCore.VERSION_1_3);
		
		ProInfo proInfo = new ProInfo(project, srcRoot, testRoot, null);
		proInfo.collectProInfo();
	}
	
	public ProInfo(String proName, String srcRoot, String testRoot, String javaVersion){
		this.proName = proName;
		
		assert srcRoot != null && new File(srcRoot).exists();
		
		this.srcRoot = srcRoot;
		this.testRoot = testRoot;
		if(javaVersion != null && javaVersion.length() > 0){
			this.javaVersion = javaVersion;
		}
		this.projectRepre = new ProjectRepre(proName, srcRoot, testRoot);
	}
		
	public ProjectRepre getProjectRepre() {
		return projectRepre;
	}
	
    /**
     * collect the project-info under <code>srcRoot</code>
     *
     * @deprecated use {@link edu.pku.sei.proj.ProInfo#collectProInfo2()}
     */
	@Deprecated
	public void collectProInfo(){
		System.out.println(">>>> PROJECT INFO BEGIN FOR " + proName);
		
		File rootFile = new File(srcRoot);
		
		this.traverseSrcFolderFirstLoop(rootFile, "");
		this.traverseSrcFolderSecondLoop(rootFile, "");
		
		if(testRoot != null){
			File testRootFile = new File(testRoot);
			this.traverseSrcFolderFirstLoop(testRootFile, "");
			this.traverseSrcFolderSecondLoop(testRootFile, "");
		}
		
		this.cleanUp();
		this.mergeUntilFix();
		System.out.println(">>>> PROJECT INFO FINISHED FOR " + proName);
		
//		for (ClassRepre cls : projectRepre.fullNameToClazzesMap.values()) {
//			System.out.println(cls);
//		}
	}
	
	public void collectProInfo2() {
		assert srcRoot != null;
		System.out.println(">>>> PROJECT INFO BEGIN FOR " + proName);
		
		File rootFile = new File(srcRoot);
		
		List<File> srcFileList = new ArrayList<File>(128);
		getFileList(rootFile, srcFileList);
		travereForPackageInfo(srcFileList);
		travereForClazzInfo(srcFileList);
		travereForInnerClazzInfo(srcFileList);
		
		System.out.println(">>>> PROJECT INFO FINISHED FOR " + proName);
	}
	
	private void travereForPackageInfo(List<File> srcFileList) {
		for(File f : srcFileList) {
			CompilationUnit cu = (CompilationUnit) JavaFile.genASTFromSourceWithType(
					JavaFile.readFileToString(f),
					ASTParser.K_COMPILATION_UNIT, 
					javaVersion, 
					srcRoot, 
					f.getAbsolutePath());
			
			assert fileToCuBuffer.containsKey(f) == false;
			fileToCuBuffer.put(f, cu);
			
			PkgCollectorVisitor pkgVisitor = new PkgCollectorVisitor();
			cu.accept(pkgVisitor);
			String currPkg = pkgVisitor.getCurrentPkg();
			PackageRepre pkgRep = projectRepre.getOrNewPackage(currPkg);
			
			assert fileToPkgRepBuffer.containsKey(f) == false;
			fileToPkgRepBuffer.put(f, pkgRep);
		}
	}
	
	private void travereForClazzInfo(List<File> srcFileList) {
		for(File f : srcFileList) {
			assert fileToCuBuffer.containsKey(f);
			CompilationUnit cu = fileToCuBuffer.get(f);
			
			assert fileToPkgRepBuffer.containsKey(f);
			PackageRepre pkgRep = fileToPkgRepBuffer.get(f);
			ClsCollectorVisitor visitor = new ClsCollectorVisitor(f, projectRepre, pkgRep);
			cu.accept(visitor);
		}
	}
	
	private void travereForInnerClazzInfo(List<File> srcFileList) {
		for(File f : srcFileList) {
			assert fileToCuBuffer.containsKey(f);
			CompilationUnit cu = fileToCuBuffer.get(f);
			
			assert fileToPkgRepBuffer.containsKey(f);
			PackageRepre pkgRep = fileToPkgRepBuffer.get(f);
			InnerClazzVisitor visitor = new InnerClazzVisitor(f, projectRepre, pkgRep);
			cu.accept(visitor);
		}
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
	
	@Deprecated
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
				
				CompilationUnit cu = (CompilationUnit) JavaFile.genASTFromSource(JavaFile.readFileToString(files[i]), ASTParser.K_COMPILATION_UNIT, javaVersion);
				
//				System.out.println(curPkg);
//				System.out.println(files[i].getName());

				ClsCollectorVisitor visitor = new ClsCollectorVisitor(files[i], projectRepre, pkgRepre);
				
				cu.accept(visitor);
				
			} else {
				continue;
			}
		}
	}
	
	@Deprecated
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
				
				CompilationUnit cu = (CompilationUnit) JavaFile.genASTFromSource(JavaFile.readFileToString(files[i]), ASTParser.K_COMPILATION_UNIT, javaVersion);
				
//				System.out.println(curPkg);
//				System.out.println(files[i].getName());

				String mainClsName = fileName.substring(0, fileName.length() - 5);

				
				ProjectVisitor visitor = new ProjectVisitor(files[i], projectRepre, pkgRepre);
				
				cu.accept(visitor);
				
			} else {
				continue;
			}
		}
	}

	private static List<File> getFileList(File root, List<File> filelist) {
		if(root == null || !root.exists() || !root.isDirectory()){
			return filelist;
		}
		
		assert root.isDirectory();
		
		File[] files = root.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();
				if (files[i].isDirectory()) {
					getFileList(files[i], filelist);
				} else if (fileName.endsWith(".java")) {
					if(fileName.equals("package-info.java")) {
						continue;
					}
					
					filelist.add(files[i]);
				} else {
					continue;
				}
			}
		}
		return filelist;
	}

	@Override
	public String toString() {
		return "ProInfo@" + this.hashCode() + ": " + proName + ", srcRoot=" + srcRoot + ", testRoot=" + testRoot + ", javaVersion="
				+ javaVersion;
	}
	
}
