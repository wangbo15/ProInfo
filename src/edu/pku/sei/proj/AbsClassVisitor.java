package edu.pku.sei.proj;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;

public abstract class AbsClassVisitor extends ASTVisitor {

	protected File file;
	protected ProjectRepre projectRepre;
	protected PackageRepre pkgRepre;
	
	protected Set<String> importedClazzes = new HashSet<>();

	
	public AbsClassVisitor(File file, ProjectRepre projectRepre, PackageRepre pkgRepre) {
		this.file = file;
		this.projectRepre = projectRepre;
		this.pkgRepre = pkgRepre;
	}
	
	protected static String typeDeclToClassName(TypeDeclaration node){
		
		String className = "";
		ASTNode father = null;
		ASTNode curNode = node;
		while((father  = curNode.getParent()) != null){
			if(curNode instanceof TypeDeclaration){
				TypeDeclaration curNodeTD = (TypeDeclaration) curNode;
				className = curNodeTD.getName().getIdentifier() + "$" + className;
			}else if(curNode instanceof AnnotationTypeDeclaration){
				AnnotationTypeDeclaration curNodeTD = (AnnotationTypeDeclaration) curNode;
				className = curNodeTD.getName().getIdentifier() + "$" + className;
			}else if(curNode instanceof EnumDeclaration){
				EnumDeclaration curNodeTD = (EnumDeclaration) curNode;
				className = curNodeTD.getName().getIdentifier() + "$" + className;
			}else {
				throw new Error(curNode.getClass().getName());
			}
			curNode = father;
		}
		
		return className.substring(0, className.length() - 1);
	}
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public ProjectRepre getProjectRepre() {
		return projectRepre;
	}
	public void setProjectRepre(ProjectRepre projectRepre) {
		this.projectRepre = projectRepre;
	}
	public PackageRepre getPkgRepre() {
		return pkgRepre;
	}
	public void setPkgRepre(PackageRepre pkgRepre) {
		this.pkgRepre = pkgRepre;
	}
	
	/**
	 * Omit anonymous class
	 */
	@Override
	public final boolean visit(AnonymousClassDeclaration node) {
		return false;
	}
	@Override
	public final boolean visit(TypeDeclarationStatement node) {
		return false;
	}
	
}
