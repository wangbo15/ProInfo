package edu.pku.sei.proj;

import java.io.File;
import java.lang.reflect.Modifier;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ClsCollectorVisitor extends AbsClassVisitor {
	
	
	public ClsCollectorVisitor(File file, ProjectRepre projectRepre, PackageRepre pkgRepre) {
		super(file, projectRepre, pkgRepre);
	}
	
	private ClsCollectorVisitor(){
		super(null, null, null);
	}

	public static String typeDeclToClassName(CompilationUnit cu,TypeDeclaration td){
		String res;
		TDVisitor tdvisitor = new ClsCollectorVisitor().new TDVisitor(td);
		cu.accept(tdvisitor);
		//A BUG OF JDT
		int flag = td.getModifiers();
		if(Modifier.isStatic(flag) && !Modifier.isPublic(flag) && td.getRoot() == td.getParent()){
			res = tdvisitor.mainClsName + "$" + tdvisitor.name;
		}else{
			res = tdvisitor.name;
		}
		return res;
	}
	
	private void setContainterCls(ClassRepre currentCls, TypeDeclaration node){
		ASTNode curNode = node;
		while(true){
			ASTNode father = curNode.getParent();
			if(father instanceof TypeDeclaration){
				String className = typeDeclToClassName((TypeDeclaration) father);
				ClassRepre container = this.pkgRepre.getClassRepre(className);
				assert container != null;
				
				if(currentCls.getContainerCls() != null){
					curNode = father;
					continue;
				}
				currentCls.setContainerCls(container);
				curNode = father;
			}else{
				break;
			}
		}
	}
	
	@Override
	public boolean visit(EnumDeclaration node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		if(node.getParent() instanceof Block){
			return false;
		}
		
		String className = typeDeclToClassName(node);
		
//		String fileName = file.getName();
//		String mainClsName = fileName.substring(0, fileName.length() - 5);
		
//		System.out.println(">>>>>>>>" + file.getName() + "  " + node.getName());
//		System.out.println("\t\t\t" +  className);
		
		ClassRepre currentCls = this.pkgRepre.getOrNewClassRepre(file, node.getStartPosition(), className);

		currentCls.setFlag(node.getModifiers());
		currentCls.setInterface(node.isInterface());
		
		setContainterCls(currentCls, node);
		
		String fullName = pkgRepre.getPkgName() + "." + className;
		projectRepre.fullNameToClazzesMap.put(fullName, currentCls);
		
		return super.visit(node);
	}

	/**
	 * Visitor for TypeDeclaration  
	 */
	private class TDVisitor extends ASTVisitor{
		public String name = null;
		public String mainClsName = null;
		
		private TypeDeclaration td;
		
		public TDVisitor(TypeDeclaration td){
			this.td = td;
		}
		@Override
		public boolean visit(TypeDeclaration node) {
			if(node.getStartPosition() == td.getStartPosition()){
				name = typeDeclToClassName(node);
			}
			if(Modifier.isPublic(node.getModifiers()) && node.getRoot() == node.getParent()){
				mainClsName = node.getName().getIdentifier();
			}
			
			return super.visit(node);
		}
	}
}
