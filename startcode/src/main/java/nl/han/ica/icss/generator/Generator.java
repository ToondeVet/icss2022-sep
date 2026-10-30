package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

public class Generator {

	private static final String INDENT = "  ";

	public String generate(AST ast) {
		StringBuilder css = new StringBuilder();
		for (ASTNode node : ast.root.getChildren()) {
			if (node instanceof Stylerule) {
				generateStylerule((Stylerule) node, 0, css);
			}
		}
		return css.toString();
	}

	private void generateStylerule(Stylerule rule, int level, StringBuilder css) {
		indent(level, css);
		css.append(generateSelectors(rule)).append(" {\n");

		for (ASTNode node : rule.body) {
			if (node instanceof Declaration) {
				generateDeclaration((Declaration) node, level + 1, css);
			}
		}

		indent(level, css);
		css.append("}\n");
	}

	private String generateSelectors(Stylerule rule) {
		StringBuilder selectors = new StringBuilder();
		for (int i = 0; i < rule.selectors.size(); i++) {
			if (i > 0) {
				selectors.append(", ");
			}
			selectors.append(rule.selectors.get(i).toString());
		}
		return selectors.toString();
	}

	private void generateDeclaration(Declaration declaration, int level, StringBuilder css) {
		indent(level, css);
		css.append(declaration.property.name)
				.append(": ")
				.append(generateValue(declaration.expression))
				.append(";\n");
	}

	private String generateValue(Expression expression) {
		if (expression instanceof PixelLiteral) {
			return ((PixelLiteral) expression).value + "px";
		}
		if (expression instanceof PercentageLiteral) {
			return ((PercentageLiteral) expression).value + "%";
		}
		if (expression instanceof ColorLiteral) {
			return ((ColorLiteral) expression).value;
		}
		throw new IllegalStateException(
				"Cannot generate CSS for " + expression.getNodeLabel()
						+ ": the AST must be checked and evaluated first");
	}

	private void indent(int level, StringBuilder css) {
		for (int i = 0; i < level; i++) {
			css.append(INDENT);
		}
	}
}
