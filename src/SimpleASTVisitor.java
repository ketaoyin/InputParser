import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class SimpleASTVisitor extends ASTVisitor{
	
	private final CompilationUnit cu;
	private String filename;
	
	public SimpleASTVisitor(char[] arr,String file_name) {
		
		this.filename = file_name;
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(arr);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		cu = (CompilationUnit) parser.createAST(null);
		
	}
		
	public boolean visit(MethodInvocation  node) {
		/*
		 * Printing only method names
		 */
		System.out.println(node.getName());
		
		return true;
	}

	public void parse() {
		cu.accept(this);
	}
}

