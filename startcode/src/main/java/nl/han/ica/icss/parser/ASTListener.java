package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}
    public AST getAST() {
        return ast;
    }

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		currentContainer.push(new Stylesheet());
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		ast.setRoot((Stylesheet) currentContainer.pop());
	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		currentContainer.push(new Stylerule());
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		ASTNode rule = currentContainer.pop();
		currentContainer.peek().addChild(rule);
	}

	@Override
	public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
		currentContainer.push(new TagSelector(ctx.getText()));
	}

	@Override
	public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
		ASTNode selector = currentContainer.pop();
		currentContainer.peek().addChild(selector);
	}

	@Override
	public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
		currentContainer.peek().addChild(new IdSelector(ctx.getText()));
	}

	@Override
	public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
		currentContainer.peek().addChild(new ClassSelector(ctx.getText()));
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.push(new Declaration());
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		ASTNode declaration = currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}

	@Override
	public void exitPropertyName(ICSSParser.PropertyNameContext ctx) {
		currentContainer.peek().addChild(new PropertyName(ctx.getText()));
	}

	@Override
	public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		currentContainer.peek().addChild(new ColorLiteral(ctx.getText()));
	}

	@Override
	public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		int value = Integer.parseInt(ctx.getText().replace("px", ""));
		currentContainer.peek().addChild(new PixelLiteral(value));
	}

	@Override
	public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		int value = Integer.parseInt(ctx.getText().replace("%", ""));
		currentContainer.peek().addChild(new PercentageLiteral(value));
	}

	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		currentContainer.push(new VariableAssignment());
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		ASTNode assignment = currentContainer.pop();
		currentContainer.peek().addChild(assignment);
	}

	@Override
	public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
		currentContainer.peek().addChild(new VariableReference(ctx.getText()));
	}

	@Override
	public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		currentContainer.peek().addChild(new ScalarLiteral(Integer.parseInt(ctx.getText())));
	}

	@Override
	public void exitTrueLiteral(ICSSParser.TrueLiteralContext ctx) {
		currentContainer.peek().addChild(new BoolLiteral(true));
	}

	@Override
	public void exitFalseLiteral(ICSSParser.FalseLiteralContext ctx) {
		currentContainer.peek().addChild(new BoolLiteral(false));
	}

	@Override
	public void enterMultiplyOperation(ICSSParser.MultiplyOperationContext ctx) {
		currentContainer.push(new MultiplyOperation());
	}

	@Override
	public void exitMultiplyOperation(ICSSParser.MultiplyOperationContext ctx) {
		ASTNode operation = currentContainer.pop();
		currentContainer.peek().addChild(operation);
	}

	@Override
	public void enterAddOrSubtractOperation(ICSSParser.AddOrSubtractOperationContext ctx) {
		if (ctx.PLUS() != null) {
			currentContainer.push(new AddOperation());
		} else {
			currentContainer.push(new SubtractOperation());
		}
	}

	@Override
	public void exitAddOrSubtractOperation(ICSSParser.AddOrSubtractOperationContext ctx) {
		ASTNode operation = currentContainer.pop();
		currentContainer.peek().addChild(operation);
	}

	@Override
	public void enterIfClause(ICSSParser.IfClauseContext ctx) {
		currentContainer.push(new IfClause());
	}

	@Override
	public void exitIfClause(ICSSParser.IfClauseContext ctx) {
		ASTNode ifClause = currentContainer.pop();
		currentContainer.peek().addChild(ifClause);
	}

	@Override
	public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
		currentContainer.push(new ElseClause());
	}

	@Override
	public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
		ASTNode elseClause = currentContainer.pop();
		currentContainer.peek().addChild(elseClause);
	}

}