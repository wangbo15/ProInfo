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
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

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
	 * Set return values to false to skip visiting
	 */
	
	@Override
	public final boolean visit(AnonymousClassDeclaration node) {
		return false;
	}
	
	@Override
	public boolean visit(AssertStatement node) {
		return false;
	}

	@Override
	public boolean visit(Block node) {
		// skip method inner body
		if(node.getLocationInParent() == MethodDeclaration.BODY_PROPERTY) {
			return false;
		}
		return super.visit(node);
	}

	@Override
	public final boolean visit(BlockComment node) {
		return false;
	}

	@Override
	public final boolean visit(BreakStatement node) {
		return false;
	}

	@Override
	public boolean visit(ContinueStatement node) {
		return false;
	}

	@Override
	public boolean visit(Dimension node) {
		return false;
	}

	@Override
	public final boolean visit(DoStatement node) {
		return false;
	}

	@Override
	public final boolean visit(EmptyStatement node) {
		return false;
	}

	@Override
	public final boolean visit(EnhancedForStatement node) {
		return false;
	}

	@Override
	public final boolean visit(ExpressionStatement node) {
		return false;
	}

	@Override
	public final boolean visit(ForStatement node) {
		return false;
	}

	@Override
	public final boolean visit(IfStatement node) {
		return false;
	}

	@Override
	public final boolean visit(Javadoc node) {
		return false;
	}

	@Override
	public final boolean visit(LabeledStatement node) {
		return false;
	}


	@Override
	public final boolean visit(LineComment node) {
		return false;
	}

	@Override
	public boolean visit(ReturnStatement node) {
		return false;
	}

	@Override
	public final boolean visit(SwitchStatement node) {
		return false;
	}

	@Override
	public final boolean visit(SynchronizedStatement node) {
		return false;
	}

	@Override
	public final boolean visit(ThrowStatement node) {
		return false;
	}

	@Override
	public final boolean visit(TryStatement node) {
		return false;
	}
	
	@Override
	public final boolean visit(TypeDeclarationStatement node) {
		return false;
	}

	@Override
	public final boolean visit(VariableDeclarationStatement node) {
		return false;
	}

	@Override
	public final boolean visit(WhileStatement node) {
		return false;
	}
}
