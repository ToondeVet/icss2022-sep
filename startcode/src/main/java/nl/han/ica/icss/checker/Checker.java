package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;



public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        checkStylesheet(ast.root);
    }

    // --- scope helpers ---
    private void openScope() {
        variableTypes.addFirst(new HashMap<>());
    }

    private void closeScope() {
        variableTypes.removeFirst();
    }

    private void declareVariable(String name, ExpressionType type) {
        variableTypes.getFirst().put(name, type);
    }

    // innermost scope first; returns null if the variable isn't visible
    private ExpressionType lookupVariable(String name) {
        for (int i = 0; i < variableTypes.getSize(); i++) {
            HashMap<String, ExpressionType> scope = variableTypes.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }

    private void checkStylesheet(Stylesheet sheet) {
        openScope();
        for (ASTNode node : sheet.getChildren()) {
            if (node instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) node);
            } else if (node instanceof Stylerule) {
                checkStylerule((Stylerule) node);
            }
        }
        closeScope();
    }

    private void checkStylerule(Stylerule rule) {
        openScope();
        checkBody(rule.body);
        closeScope();
    }

    // shared by rules, ifs and elses
    private void checkBody(ArrayList<ASTNode> body) {
        for (ASTNode node : body) {
            if (node instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) node);
            } else if (node instanceof Declaration) {
                checkDeclaration((Declaration) node);
            } else if (node instanceof IfClause) {
                checkIfClause((IfClause) node);
            }
        }
    }

    private void checkIfClause(IfClause ifClause) {
        checkExpression(ifClause.conditionalExpression);

        openScope();
        checkBody(ifClause.body);
        closeScope();

        if (ifClause.elseClause != null) {
            openScope();
            checkBody(ifClause.elseClause.body);
            closeScope();
        }
    }

    private void checkDeclaration(Declaration declaration) {
        checkExpression(declaration.expression);
    }

    private void checkVariableAssignment(VariableAssignment assignment) {
        // check the right-hand side BEFORE declaring the variable
        checkExpression(assignment.expression);
        declareVariable(assignment.name.name, getExpressionType(assignment.expression));
    }

    // walks an expression and checks every variable reference in it
    private void checkExpression(Expression expression) {
        if (expression instanceof VariableReference) {
            VariableReference ref = (VariableReference) expression;
            if (lookupVariable(ref.name) == null) {
                ref.setError("Variable " + ref.name + " is not defined or not in scope");
            }
        } else if (expression instanceof Operation) {
            Operation op = (Operation) expression;
            checkExpression(op.lhs);
            checkExpression(op.rhs);
        }
    }

    // minimal version for now; operations are added in CH03
    private ExpressionType getExpressionType(Expression expression) {
        if (expression instanceof PixelLiteral) return ExpressionType.PIXEL;
        if (expression instanceof PercentageLiteral) return ExpressionType.PERCENTAGE;
        if (expression instanceof ScalarLiteral) return ExpressionType.SCALAR;
        if (expression instanceof ColorLiteral) return ExpressionType.COLOR;
        if (expression instanceof BoolLiteral) return ExpressionType.BOOL;
        if (expression instanceof VariableReference) {
            ExpressionType type = lookupVariable(((VariableReference) expression).name);
            return type == null ? ExpressionType.UNDEFINED : type;
        }
        return ExpressionType.UNDEFINED;
    }

}
