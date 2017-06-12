package edu.pku.sei.proj;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ProjectVisitor extends ASTVisitor {
	
	private ProjectRepre project;
	
	private PackageRepre pkgRepre;
	
	private Set<PackageRepre> importedPkgs = new HashSet<>();
	private Set<ClassRepre> importedClazzes = new HashSet<>();

	
	private static Set<String> parsedPackages = new HashSet<>();
	
	private CompilationUnit cu;
	private File file;
	private char[] rawSource;
	
	
	private ClassRepre mainCls;
		
	public ProjectVisitor(CompilationUnit cu, File f, char[] rawSource){
		this.cu = cu;
		this.file = f;
		this.rawSource = rawSource;
		
	}
	
	public static void reset(){
		parsedPackages.clear();
	}
	
	public void setProject(ProjectRepre project) {
		this.project = project;
	}


	@Override
	public boolean visit(CompilationUnit node) {
		PackageDeclaration pkgDecl = node.getPackage();
		String pkgStr = (pkgDecl == null) ? "" : pkgDecl.getName().getFullyQualifiedName();
		
		pkgRepre = project.getPackage(pkgStr);
		
		File parent = file.getParentFile();
		
		if(! parsedPackages.contains(parent.getAbsolutePath())){
					
			parsedPackages.add(parent.getAbsolutePath());
		
			for(File brother : parent.listFiles()){
				if(brother.getName().endsWith(".java")){
					//remove postfix ".java" 
					String fname = brother.getName().substring(0, brother.getName().length() - 5);
					project.getClassRepre(pkgRepre, fname);
				}
			}
		}
		
		for (Iterator it = node.imports().iterator(); it.hasNext(); ){
			ImportDeclaration impDecl = (ImportDeclaration) it.next();
			
			String importedPkg = null;
			String className = null; 

			Name nm = impDecl.getName();
			
			//skip lib packages
			if(!nm.toString().contains(project.getName())){
				continue;
			}
			
			if(nm instanceof QualifiedName){
				importedPkg = ((QualifiedName) nm).getQualifier().getFullyQualifiedName();
				className = ((QualifiedName) nm).getName().getIdentifier();
			}else if(nm instanceof SimpleName){
				importedPkg = "";
				className = nm.toString();
			}else{
				try {
					throw new Exception();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			PackageRepre pkg = project.getPackage(importedPkg);
			
			importedPkgs.add(pkg);
			
			ClassRepre classRepre = project.getClassRepre(importedPkg, className);
			
			importedClazzes.add(classRepre);
		}
		
		
		return super.visit(node);
	}


	@Override
	public boolean visit(TypeDeclaration node) {
		//skip inner clazzes
		if(node.getParent() != node.getRoot()){
			return false;			
		}
		String className = node.getName().getIdentifier();
		ClassRepre currentCls = this.pkgRepre.getClassRepre(className);
		int flag = node.getModifiers();
		
		currentCls.setFlag(flag);
		currentCls.setInterface(node.isInterface());
		
		Type superTp = 	node.getSuperclassType();
		if(superTp != null){
			
		}
		
		
		return super.visit(node);
	}
	
	

}
