package edu.pku.sei.proj;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;


public class InnerClazzAndInheranceVisitor extends AbsClassVisitor {
	
	public InnerClazzAndInheranceVisitor(File file, ProjectRepre projectRepre, PackageRepre pkgRepre) {
		super(file, projectRepre, pkgRepre);
	}
	
	protected static String getImportedFatherClassName(String imorted) {
		if(imorted.contains(".") == false) {
			return imorted;
		}
		String paths[] = imorted.split("\\.");
		int len = paths.length;
		
		// init from the last name
		String fullClsName = paths[len - 1];
		for(int i = (len - 2); i >=0 ; i--){
			String currentPath = paths[i];
			//for math3's import org.apache.commons.math3.userguide.ExampleUtils.ExampleFrame;
			if(Character.isUpperCase(currentPath.charAt(0))){
				fullClsName = currentPath + "$" + fullClsName;
			}else{
				fullClsName = currentPath + "." + fullClsName;
			}
		}
		return fullClsName;
	}
	
	@Override
	public final boolean visit(CompilationUnit node) {
		PackageDeclaration pkgDecl = node.getPackage();
		String pkgStr = (pkgDecl == null) ? "" : pkgDecl.getName().getFullyQualifiedName();
		
		assert pkgStr.equals(pkgRepre.getPkgName());
		
		for (Iterator it = node.imports().iterator(); it.hasNext(); ){
			ImportDeclaration impDecl = (ImportDeclaration) it.next();
			
			if(impDecl.isOnDemand()){
				
				String loadAllPkg = impDecl.getName().toString();
				
				PackageRepre loadAll = projectRepre.getPackage(loadAllPkg);
				if(loadAll == null) {//package of other project
					continue;
				}
				
				for(ClassRepre cls : loadAll.getClazzesMap().values()){
					int flag = cls.getFlag();
					if(Modifier.isPublic(flag)){
						importedClazzes.add(loadAllPkg + "." + cls.getName());
					}
				}
				
//				System.err.println(loadAllPkg);
				
			}else{
				String imported = impDecl.getName().toString();
				if(projectRepre.fullNameToClazzesMap.containsKey(imported)) {
					importedClazzes.add(imported);
				} else {
					// for junit3 test cases
					if(imported.startsWith("junit.framework.")) {
						
						int idx = imported.lastIndexOf(".");
						String pkg = imported.substring(0, idx);
						String name = imported.substring(idx + 1);
						
						PackageRepre pr = projectRepre.getOrNewPackage(pkg);
						ClassRepre libClazz = pr.getOrNewClassRepre(null, -1, name);
						libClazz.setLibaryClz(true);
						projectRepre.fullNameToClazzesMap.put(imported, libClazz);
					}
					
				}
			}
		}
		
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		
		if(node.getParent() instanceof Block){
			return false;
		}
		
		String className = typeDeclToClassName(node);
		
		ClassRepre currentCls = this.pkgRepre.getClassRepre(className);
		
		assert currentCls != null;
		
		int flag = node.getModifiers();
		
		currentCls.setFlag(flag);
		currentCls.setInterface(node.isInterface());
		
		processExtends(node, currentCls);
		
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
				
				String fullTp = tpStr;
				for(int d = 0; d < frag.getExtraDimensions(); d++) {
					fullTp = fullTp + "[]";
				}
				
				FieldRepre field = new FieldRepre(clsRepre, frag.getName().toString(), fullTp, flag);
				clsRepre.insertFieldRepre(field);
				
				if(frag.getInitializer() != null){
					String init = frag.getInitializer().toString();
					field.setIninalValue(init);
				}
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

	
	protected final void processExtends(TypeDeclaration node, ClassRepre currentCls) {
		Type superTp = 	node.getSuperclassType();
		
		if(superTp == null) {
			return;
		}
		
		if(superTp instanceof ParameterizedType){
			superTp = ((ParameterizedType) superTp).getType();
		}
		
		String superTpStr = superTp.toString();

		if(superTp instanceof QualifiedType){
			Type qualifier = ((QualifiedType) superTp).getQualifier();
			if(qualifier instanceof ParameterizedType){
				superTpStr = ((ParameterizedType) qualifier).getType().toString() + "." + ((QualifiedType) superTp).getName().getIdentifier();
			}
		}
		
		if(superTpStr.contains(".")){
			superTpStr = superTpStr.replace(".", "$");
		}
		
		String fullClsName = null;
		//1. search from imported classes
		for(String imp : importedClazzes){
			if(imp.endsWith(superTpStr) && (imp.lastIndexOf(".") + 1 + superTpStr.length() == imp.length())){
				fullClsName = getImportedFatherClassName(imp);
				
				assert !fullClsName.endsWith(".null");
				
				if(fullClsName != null) {
					break;
				}
			}
		}
		//2. if not found, serach from the same package
		if(fullClsName == null){
			ClassRepre fatherCls = pkgRepre.getClassRepre(superTpStr);
			
			if(fatherCls != null){
				currentCls.setFatherCls(fatherCls);
			}else{
				for(ClassRepre brother : pkgRepre.getClazzesMap().values()){
					String brotherName = brother.getName();
					if(brotherName.contains("$")){
						int idx = brotherName.lastIndexOf("$");
						brotherName = brotherName.substring(idx + 1);
						if(brotherName.equals(superTpStr)){
							fatherCls = brother;
						}
					}
				}
				if(fatherCls != null){
					currentCls.setFatherCls(fatherCls);
				}
			}
		}else{
			ClassRepre fatherCls = projectRepre.fullNameToClazzesMap.get(fullClsName);
			
			if(fatherCls != null){
				currentCls.setFatherCls(fatherCls);
			}
		}
	}
	
}
