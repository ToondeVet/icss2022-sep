package nl.han.ica.icss.evaluator;

import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.parser.ASTListener;
import nl.han.ica.icss.parser.ICSSLexer;
import nl.han.ica.icss.parser.ICSSParser;
import nl.han.ica.icss.transforms.Evaluator;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class EvaluatorTest {

    private AST parse(String icss) {
        ICSSLexer lexer = new ICSSLexer(CharStreams.fromString(icss));
        ICSSParser parser = new ICSSParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new BailErrorStrategy());
        ASTListener listener = new ASTListener();
        try {
            new ParseTreeWalker().walk(listener, parser.stylesheet());
        } catch (ParseCancellationException e) {
            fail("Does not parse: " + e.getMessage());
        }
        return listener.getAST();
    }

    private String icss(String... lines) { return String.join("\n", lines); }

    private void assertTransforms(String input, String expected) {
        AST actual = parse(input);
        new Evaluator().apply(actual);
        assertEquals(parse(expected), actual);
    }

    @Test
    void assignmentExampleFromTheAssignment() {
        assertTransforms(icss(
                        "AdjustWidth := TRUE;",
                        "WidthVar := 0px;",
                        "p {",
                        "  if [AdjustWidth] { WidthVar := 200px; } else { WidthVar := 250px; }",
                        "  width: WidthVar;",
                        "}"),
                "p { width: 200px; }");
    }

    @Test
    void calculationsAreEvaluated() {
        assertTransforms(icss(
                        "p { width: 50px + 2 * 10px - 2px; }"),
                "p { width: 68px; }");
    }

    @Test
    void evaluationIsLeftToRight() {
        assertTransforms("p { width: 100px - 20px - 2px; }", "p { width: 78px; }");
    }

    @Test
    void percentagesAndScalars() {
        assertTransforms("p { width: 50% - 20%; height: 4% * 3; }",
                "p { width: 30%; height: 12%; }");
    }

    @Test
    void variablesAreSubstitutedAndChained() {
        assertTransforms(icss(
                        "A := 10px;",
                        "B := A * 2;",
                        "p { width: B + A; }"),
                "p { width: 30px; }");
    }

    @Test
    void trueIfIsReplacedByItsBody() {
        assertTransforms(icss(
                        "p { if[TRUE] { width: 1px; height: 2px; } else { width: 9px; } }"),
                "p { width: 1px; height: 2px; }");
    }

    @Test
    void falseIfIsReplacedByElseBody() {
        assertTransforms(icss(
                        "p { if[FALSE] { width: 1px; } else { width: 9px; } }"),
                "p { width: 9px; }");
    }

    @Test
    void falseIfWithoutElseDisappears() {
        assertTransforms(icss(
                        "p { height: 1px; if[FALSE] { width: 2px; } height: 3px; }"),
                "p { height: 1px; height: 3px; }");
    }

    @Test
    void nestedIfsAreFlattenedInOrder() {
        assertTransforms(icss(
                        "p {",
                        "  width: 1px;",
                        "  if[TRUE] { height: 2px; if[FALSE] { width: 3px; } else { color: #ff0000; } }",
                        "  background-color: #00ff00;",
                        "}"),
                "p { width: 1px; height: 2px; color: #ff0000; background-color: #00ff00; }");
    }

    @Test
    void ifConditionFromVariable() {
        assertTransforms(icss(
                        "Flag := FALSE;",
                        "p { if[Flag] { width: 1px; } else { width: 2px; } }"),
                "p { width: 2px; }");
    }

    @Test
    void variableInsideIfDoesNotLeakOut() {
        assertTransforms(icss(
                        "Outer := 5px;",
                        "p {",
                        "  if[TRUE] { Inner := 1px; width: Inner + Outer; }",
                        "  height: Outer;",
                        "}"),
                "p { width: 6px; height: 5px; }");
    }

    @Test
    void localVariableDoesNotLeakToNextRule() {
        assertTransforms(icss(
                        "Width := 10px;",
                        "p { Local := 5px; width: Local; }",
                        "a { width: Width; }"),
                "p { width: 5px; } a { width: 10px; }");
    }
}