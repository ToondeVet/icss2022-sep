package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;

import java.util.ArrayList;
import java.util.HashMap;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        openScope();
        transformBody(ast.root.body);
        closeScope();
    }


    private void openScope()  { variableValues.addFirst(new HashMap<>()); }
    private void closeScope() { variableValues.removeFirst(); }

    private Literal lookupVariable(String name) {
        for (int i = 0; i < variableValues.getSize(); i++) {
            HashMap<String, Literal> scope = variableValues.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }

    private void assignVariable(String name, Literal value) {
        for (int i = 0; i < variableValues.getSize(); i++) {
            HashMap<String, Literal> scope = variableValues.get(i);
            if (scope.containsKey(name)) {
                scope.put(name, value);
                return;
            }
        }
        variableValues.getFirst().put(name, value);
    }

    private void transformBody(ArrayList<ASTNode> body) {
        ArrayList<ASTNode> result = new ArrayList<>();
        for (ASTNode node : body) {
            if (node instanceof VariableAssignment) {
                VariableAssignment assignment = (VariableAssignment) node;
                assignVariable(assignment.name.name, evaluate(assignment.expression));
            } else if (node instanceof Declaration) {
                Declaration declaration = (Declaration) node;
                declaration.expression = evaluate(declaration.expression);
                result.add(declaration);
            } else if (node instanceof Stylerule) {
                Stylerule rule = (Stylerule) node;
                openScope();
                transformBody(rule.body);
                closeScope();
                result.add(rule);
            } else if (node instanceof IfClause) {
                result.addAll(transformIfClause((IfClause) node));
            }
        }
        body.clear();
        body.addAll(result);
    }

    private ArrayList<ASTNode> transformIfClause(IfClause ifClause) {
        BoolLiteral condition = (BoolLiteral) evaluate(ifClause.conditionalExpression);

        ArrayList<ASTNode> chosen = new ArrayList<>();
        if (condition.value) {
            chosen.addAll(ifClause.body);
        } else if (ifClause.elseClause != null) {
            chosen.addAll(ifClause.elseClause.body);
        }

        openScope();
        transformBody(chosen);
        closeScope();
        return chosen;
    }

    private Literal evaluate(Expression expression) {
        if (expression instanceof Literal) {
            return (Literal) expression;
        }
        if (expression instanceof VariableReference) {
            return lookupVariable(((VariableReference) expression).name);
        }
        Operation operation = (Operation) expression;
        Literal left = evaluate(operation.lhs);
        Literal right = evaluate(operation.rhs);

        if (operation instanceof MultiplyOperation) {
            Literal typed = (left instanceof ScalarLiteral) ? right : left;
            return withValue(typed, valueOf(left) * valueOf(right));
        }
        if (operation instanceof AddOperation) {
            return withValue(left, valueOf(left) + valueOf(right));
        }
        return withValue(left, valueOf(left) - valueOf(right));
    }

    private int valueOf(Literal literal) {
        if (literal instanceof PixelLiteral) return ((PixelLiteral) literal).value;
        if (literal instanceof PercentageLiteral) return ((PercentageLiteral) literal).value;
        return ((ScalarLiteral) literal).value;
    }

    private Literal withValue(Literal template, int value) {
        if (template instanceof PixelLiteral) return new PixelLiteral(value);
        if (template instanceof PercentageLiteral) return new PercentageLiteral(value);
        return new ScalarLiteral(value);
    }
}