package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;



public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        checkStylesheet(ast.root);
    }

    private void openScope() {
        variableTypes.addFirst(new HashMap<>());
    }

    private void closeScope() {
        variableTypes.removeFirst();
    }

    private void declareVariable(String name, ExpressionType type) {
        variableTypes.getFirst().put(name, type);
    }

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
        ExpressionType type = checkExpression(declaration.expression);
        if (type == ExpressionType.UNDEFINED) {
            return;
        }
        String property = declaration.property.name;
        boolean valid;
        switch (property) {
            case "width":
            case "height":
                valid = type == ExpressionType.PIXEL || type == ExpressionType.PERCENTAGE;
                break;
            case "color":
            case "background-color":
                valid = type == ExpressionType.COLOR;
                break;
            default:
                valid = true;
        }
        if (!valid) {
            declaration.setError("Property " + property + " cannot have a value of type " + type);
        }
    }

    private void checkVariableAssignment(VariableAssignment assignment) {
        ExpressionType type = checkExpression(assignment.expression);
        declareVariable(assignment.name.name, type);
    }

    private ExpressionType checkExpression(Expression expression) {
        if (expression instanceof VariableReference) {
            VariableReference ref = (VariableReference) expression;
            ExpressionType type = lookupVariable(ref.name);
            if (type == null) {
                ref.setError("Variable " + ref.name + " is not defined or not in scope");
                return ExpressionType.UNDEFINED;
            }
            return type;
        }
        if (expression instanceof Operation) {
            return checkOperation((Operation) expression);
        }
        return getLiteralType(expression);
    }

    private ExpressionType getLiteralType(Expression expression) {
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

    private ExpressionType checkOperation(Operation operation) {
        ExpressionType left = checkExpression(operation.lhs);
        ExpressionType right = checkExpression(operation.rhs);

        if (left == ExpressionType.COLOR || right == ExpressionType.COLOR) {
            operation.setError("Colors cannot be used in calculations");
            return ExpressionType.UNDEFINED;
        }
        if (left == ExpressionType.UNDEFINED || right == ExpressionType.UNDEFINED) {
            return ExpressionType.UNDEFINED;
        }

        if (operation instanceof MultiplyOperation) {
            if (left == ExpressionType.SCALAR) return right;
            if (right == ExpressionType.SCALAR) return left;
            return ExpressionType.UNDEFINED;
        }
        return left == right ? left : ExpressionType.UNDEFINED;
    }

}
