package edu.pku.sei.proj;

import java.io.File;
import java.lang.reflect.Modifier;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;

public class ClsCollectorVisitor extends ASTVisitor {
		
	private File file;
	private ProjectRepre projectRepre;
	private PackageRepre pkgRepre;
	
	public ClsCollectorVisitor(File file, ProjectRepre projectRepre, PackageRepre pkgRepre) {
		super();
		this.file = file;
		this.projectRepre = projectRepre;
		this.pkgRepre = pkgRepre;
	}
	
	private ClsCollectorVisitor(){}

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
	
	protected static String typeDeclToClassName(TypeDeclaration node){
		
		String className = "";
		ASTNode father = null;
		ASTNode curNode = node;
		while((father  = curNode.getParent()) != null){
			TypeDeclaration curNodeTD = (TypeDeclaration) curNode;
			className = curNodeTD.getName().getIdentifier() + "$" + className;
			curNode = father;
		}
		
		return className.substring(0, className.length() - 1);
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
		
		String fileName = file.getName();
		String mainClsName = fileName.substring(0, fileName.length() - 5);
		
//		System.out.println(">>>>>>>>" + file.getName() + "  " + node.getName());
//		System.out.println("\t\t\t" +  className);
				
		ClassRepre currentCls = this.pkgRepre.getOrNewClassRepre(file, node.getStartPosition(), className);

		currentCls.setFlag(node.getModifiers());
		
		String fullName = pkgRepre.getPkgName() + "." + className;
		projectRepre.fullNameToClazzesMap.put(fullName, currentCls);
		
		return super.visit(node);
	}

	/**
	 * Omit anonymous class
	 */
	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		return false;
	}
	
	/**
	 * Omit td in methods
	 */
	@Override
	public boolean visit(TypeDeclarationStatement node) {
		return false;
	}



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
