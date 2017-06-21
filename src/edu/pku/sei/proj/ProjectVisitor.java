package edu.pku.sei.proj;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;


public class ProjectVisitor extends ASTVisitor {
	
	private File file;
	private ProjectRepre projectRepre;
	private PackageRepre pkgRepre;

	
	private Set<String> importedClazzes = new HashSet<>();

	
	public ProjectVisitor(File file, ProjectRepre projectRepre, PackageRepre pkgRepre) {
		super();
		this.file = file;
		this.projectRepre = projectRepre;
		this.pkgRepre = pkgRepre;
	}


	@Override
	public boolean visit(CompilationUnit node) {
		PackageDeclaration pkgDecl = node.getPackage();
		String pkgStr = (pkgDecl == null) ? "" : pkgDecl.getName().getFullyQualifiedName();
		
		assert pkgStr.equals(pkgRepre.getPkgName());
		
		for (Iterator it = node.imports().iterator(); it.hasNext(); ){
			ImportDeclaration impDecl = (ImportDeclaration) it.next();
			
			if(impDecl.isOnDemand()){
				
				String loadAllPkg = impDecl.getName().toString();
				
				if(PackageRepre.isJdkPackage(loadAllPkg)){
					continue;
				}
				
				PackageRepre loadAll = projectRepre.getPackage(loadAllPkg);
				
				for(ClassRepre cls : loadAll.getClazzesMap().values()){
					int flag = cls.getFlag();
					if(Modifier.isPublic(flag)){
						importedClazzes.add(loadAllPkg + "." + cls.getName());
					}
				}
				
//				System.err.println(loadAllPkg);
				
			}else{
				Name nm = impDecl.getName();
				importedClazzes.add(nm.toString());
			}
		}
		
		
		return super.visit(node);
	}


	@Override
	public boolean visit(TypeDeclaration node) {

		String className = ClsCollectorVisitor.typeDeclToClassName(node);
		
		ClassRepre currentCls = this.pkgRepre.getClassRepre(className);
		
		assert currentCls != null;
		
		int flag = node.getModifiers();
		
		currentCls.setFlag(flag);
		currentCls.setInterface(node.isInterface());
		
		Type superTp = 	node.getSuperclassType();
		
		if(superTp instanceof ParameterizedType){
			superTp = ((ParameterizedType) superTp).getType();
		}

		if (superTp != null) {
			String superTpStr = superTp.toString();
			//TODO:: differnt type class
			String fullClsName = null;
			
			//1. search from imported classes
			for(String imp : importedClazzes){
				if(imp.endsWith(superTpStr) && (imp.lastIndexOf(".") + 1 + superTpStr.length() == imp.length())){
					fullClsName = imp;
					break;
				}
			}
			//2. if not found, serach from the same package
			if(fullClsName == null){
				ClassRepre fatherCls = pkgRepre.getClassRepre(superTpStr);
								
				if(fatherCls != null){
					currentCls.setFatherCls(fatherCls);
				}else{
					//omit java.lang.*, java.* and junit.framework.TestCase
					if(! ProInfo.javaDotLangClasses.contains(superTpStr) && !superTpStr.startsWith("java.")  && !superTpStr.equals("TestCase")){
						
						//search from the same file
						int idx = className.lastIndexOf("$");
						if(idx < 0){
							throw new Error(className + " EXTENDS " + superTpStr);
						}
						String superName = className.substring(0, idx + 1) + superTpStr;
						
						fatherCls = pkgRepre.getClassRepre(superName);
						if(fatherCls != null){
							currentCls.setFatherCls(fatherCls);
						}else{
							throw new Error(className + " EXTENDS " + superTpStr);
						}
					}
				}
			}else{
				ClassRepre fatherCls = projectRepre.fullNameToClazzesMap.get(fullClsName);
				
				if(fatherCls != null){
					currentCls.setFatherCls(fatherCls);
				}else if(!PackageRepre.isJdkPackage(fullClsName)){
					throw new Error(fullClsName);
				}
				
			}
		}
		
//		System.out.println("INSERT CLS: " + currentCls);
		
		for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext();){
			
			ASTNode body = (ASTNode) it.next();
			if(body instanceof FieldDeclaration || body instanceof MethodDeclaration){
				FieldOrMethodVisitor fldVisitor = new FieldOrMethodVisitor(currentCls);
				body.accept(fldVisitor);
			}
		}	
		
		return super.visit(node);
	}
	
	private class FieldOrMethodVisitor extends ASTVisitor{
		private ClassRepre clsRepre;
		
		public FieldOrMethodVisitor(ClassRepre clsRepre){
			this.clsRepre = clsRepre;
		}
		
		@Override
		public boolean visit(FieldDeclaration node) {
			
			int flag = node.getModifiers();
			String tpStr = node.getType().toString();
			
			for (Iterator it = node.fragments().iterator();it.hasNext();){
				VariableDeclarationFragment frag = (VariableDeclarationFragment) it.next();
				
				FieldRepre field = new FieldRepre(clsRepre, frag.getName().toString(), node.getType().toString(), flag);
				clsRepre.insertFieldRepre(field);
//				System.out.println("INSERT FLD: " + field);

			}
			
			return false;
		}
	
		
		@Override
		public boolean visit(MethodDeclaration node) {
			int flag = node.getModifiers();
			boolean isConstructor = node.isConstructor();
			String retTp = null;
			if(! isConstructor){
				retTp = node.getReturnType2() == null ? "void" : node.getReturnType2().toString();
			}
			MethodRepre mtdRepre = new MethodRepre(clsRepre, flag, node.getName().getIdentifier(), retTp, isConstructor);
			
			clsRepre.insertMethodRepre(mtdRepre);
			
//			System.out.println("INSERT MTD: " + mtdRepre);
			
			List params = node.parameters();
			if(params != null && params.size() > 0){
				for (Iterator it = node.parameters().iterator(); it.hasNext(); ){
					SingleVariableDeclaration frag = (SingleVariableDeclaration) it.next();
					int paraFlag = frag.getModifiers();
					LocalRepre param = new LocalRepre(clsRepre, frag.getName().getIdentifier(), frag.getType().toString(), mtdRepre, paraFlag);
					mtdRepre.insertParam(param);
				}
				
			}
			
			return false;
		}
	}

}
