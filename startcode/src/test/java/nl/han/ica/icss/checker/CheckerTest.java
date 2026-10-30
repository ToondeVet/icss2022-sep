package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.parser.ASTListener;
import nl.han.ica.icss.parser.ICSSLexer;
import nl.han.ica.icss.parser.ICSSParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CheckerTest {

    private AST parse(String icss) {
        ICSSLexer lexer = new ICSSLexer(CharStreams.fromString(icss));
        ICSSParser parser = new ICSSParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new BailErrorStrategy());

        ASTListener listener = new ASTListener();
        try {
            ParseTree tree = parser.stylesheet();
            new ParseTreeWalker().walk(listener, tree);
        } catch (ParseCancellationException e) {
            fail("Test input does not parse: " + e.getMessage());
        }
        return listener.getAST();
    }

    private List<String> check(String icss) {
        AST ast = parse(icss);
        new Checker().check(ast);
        return ast.getErrors().stream()
                .map(SemanticError::toString)
                .collect(Collectors.toList());
    }

    private String icss(String... lines) {
        return String.join("\n", lines);
    }

    private void assertNoErrors(String icss) {
        List<String> errors = check(icss);
        assertTrue(errors.isEmpty(), "Expected no errors but got: " + errors);
    }

    private void assertErrorCount(int expected, String icss) {
        List<String> errors = check(icss);
        assertEquals(expected, errors.size(), "Errors found: " + errors);
    }

    // CH01: undefined variables + CH06: scope

    @Test
    void globalVariableIsVisibleInRules() {
        assertNoErrors(icss(
                "Global := 10px;",
                "p { width: Global; }",
                "a { height: Global; }"));
    }

    @Test
    void localVariableIsVisibleAfterAssignmentInSameRule() {
        assertNoErrors(icss(
                "p {",
                "  Local := 5px;",
                "  width: Local;",
                "}"));
    }

    @Test
    void localVariableIsNotVisibleInOtherRule() {
        assertErrorCount(1, icss(
                "p { Local := 5px; }",
                "a { width: Local; }"));
    }

    @Test
    void variableUsedBeforeDeclarationIsAnError() {
        assertErrorCount(1, icss(
                "p { width: Later; Later := 5px; }"));
    }

    @Test
    void globalDeclaredAfterRuleIsNotVisibleInThatRule() {
        assertErrorCount(1, icss(
                "p { width: Late; }",
                "Late := 5px;"));
    }

    @Test
    void variableInIfIsNotVisibleAfterTheIf() {
        assertErrorCount(1, icss(
                "p {",
                "  if[TRUE] { InIf := 2px; }",
                "  width: InIf;",
                "}"));
    }

    @Test
    void variableInIfIsNotVisibleInElse() {
        assertErrorCount(1, icss(
                "p {",
                "  if[TRUE] { InIf := 2px; } else { width: InIf; }",
                "}"));
    }

    @Test
    void outerVariablesAreVisibleInsideNestedIfs() {
        assertNoErrors(icss(
                "Global := 1px;",
                "p {",
                "  Local := 2px;",
                "  if[TRUE] {",
                "    if[FALSE] { width: Local; } else { height: Global; }",
                "  }",
                "}"));
    }

    @Test
    void neverDeclaredVariableIsAnError() {
        assertErrorCount(1, icss(
                "p { width: Nope; }"));
    }

    @Test
    void undefinedVariableInsideCalculationIsAnError() {
        assertErrorCount(1, icss(
                "p { width: 10px + Nope; }"));
    }

    @Test
    void undefinedVariableInIfConditionIsAnError() {
        assertErrorCount(1, icss(
                "p { if[Nope] { width: 1px; } }"));
    }

    @Test
    void variableCannotUseItselfBeforeItExists() {
        assertErrorCount(1, icss(
                "A := A + 1px;"));
    }

    @Test
    void scopeRulesCombinedExample() {
        // the example from the explanation: exactly 4 errors
        assertErrorCount(4, icss(
                "Global := 10px;",
                "p {",
                "  Local := 5px;",
                "  width: Global;",
                "  height: Local;",
                "  if[TRUE] {",
                "    InIf := 2px;",
                "    width: Local;",
                "  } else {",
                "    width: InIf;",
                "  }",
                "  height: InIf;",
                "}",
                "a {",
                "  width: Local;",
                "  height: Later;",
                "}",
                "Later := 1px;"));
    }

    // CH03: no colors in operations

    @Test
    void colorInAdditionIsAnError() {
        assertErrorCount(1, icss("p { color: #ff0000 + #00ff00; }"));
    }

    @Test
    void colorInSubtractionIsAnError() {
        assertErrorCount(1, icss("p { color: #ff0000 - #00ff00; }"));
    }

    @Test
    void colorInMultiplicationIsAnError() {
        assertErrorCount(1, icss("p { width: #ff0000 * 2; }"));
    }

    @Test
    void colorVariableInOperationIsAnError() {
        assertErrorCount(1, icss(
                "C := #ff0000;",
                "p { width: C + 1px; }"));
    }

    @Test
    void colorNestedInLargerCalculationIsAnError() {
        assertErrorCount(1, icss("p { width: 10px + 2 * #ff0000; }"));
    }

    @Test
    void colorInVariableAssignmentOperationIsAnError() {
        assertErrorCount(1, icss("X := #ff0000 + 1px;"));
    }

    @Test
    void failedCalculationDoesNotCauseFollowUpErrors() {
        assertErrorCount(1, icss(
                "C := #ff0000;",
                "X := C + 1px;",
                "p { width: X; }"));
    }

    @Test
    void pixelCalculationsAreStillAllowed() {
        assertNoErrors(icss("p { width: 10px + 5px * 2; }"));
    }

    @Test
    void scalarMultiplicationOnEitherSideIsAllowed() {
        assertNoErrors(icss(
                "p { width: 2 * 10px; height: 50% * 3; }"));
    }

// CH04: declaration value matches property

    @Test
    void colorValueOnWidthIsAnError() {
        assertErrorCount(1, icss("p { width: #ff0000; }"));
    }

    @Test
    void colorValueOnHeightIsAnError() {
        assertErrorCount(1, icss("p { height: #ff0000; }"));
    }

    @Test
    void pixelValueOnColorIsAnError() {
        assertErrorCount(1, icss("p { color: 12px; }"));
    }

    @Test
    void percentageValueOnBackgroundColorIsAnError() {
        assertErrorCount(1, icss("p { background-color: 50%; }"));
    }

    @Test
    void boolValueOnPropertyIsAnError() {
        assertErrorCount(1, icss(
                "Flag := TRUE;",
                "p { width: Flag; }"));
    }

    @Test
    void boolLiteralOnPropertyIsAnError() {
        assertErrorCount(1, icss("p { color: TRUE; }"));
    }

    @Test
    void scalarValueOnWidthIsAnError() {
        assertErrorCount(1, icss("p { width: 3; }"));
    }

    @Test
    void calculationResultMustMatchProperty() {
        assertErrorCount(1, icss("p { color: 10px + 5px; }"));
    }

    @Test
    void variableTypeIsCheckedAgainstProperty() {
        assertErrorCount(1, icss(
                "Main := #ff0000;",
                "p { width: Main; }"));
    }

    @Test
    void typeErrorInsideIfBodyIsFound() {
        assertErrorCount(2, icss(
                "p { if[TRUE] { width: #ff0000; } else { color: 1px; } }"));
    }

    @Test
    void validDeclarationsHaveNoErrors() {
        assertNoErrors(icss(
                "p {",
                "  width: 10px;",
                "  height: 50%;",
                "  color: #ff0000;",
                "  background-color: #00ff00;",
                "}"));
    }

    @Test
    void validVariablesAndCalculationsHaveNoErrors() {
        assertNoErrors(icss(
                "Main := #ff0000;",
                "Size := 10px * 2;",
                "p {",
                "  color: Main;",
                "  width: Size + 5px;",
                "  height: 100% - 20%;",
                "}"));
    }
}