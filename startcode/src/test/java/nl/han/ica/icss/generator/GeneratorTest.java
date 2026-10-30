package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.parser.ASTListener;
import nl.han.ica.icss.parser.ICSSLexer;
import nl.han.ica.icss.parser.ICSSParser;
import nl.han.ica.icss.transforms.Evaluator;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {

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

    private String lines(String... lines) {
        return String.join("\n", lines) + "\n";
    }

    private String generate(String icss) {
        AST ast = parse(icss);
        new Evaluator().apply(ast);
        return new Generator().generate(ast);
    }

    @Test
    void simpleRuleWithEachLiteralType() {
        assertEquals(lines(
                        "a {",
                        "  color: #ff0000;",
                        "  width: 50%;",
                        "  height: 10px;",
                        "}"),
                generate("a { color: #ff0000; width: 50%; height: 10px; }"));
    }

    @Test
    void idAndClassSelectorsKeepTheirPrefix() {
        assertEquals(lines(
                        "#menu {",
                        "  width: 100%;",
                        "}",
                        ".active {",
                        "  color: #00ffff;",
                        "}"),
                generate("#menu { width: 100%; } .active { color: #00ffff; }"));
    }

    @Test
    void rulesAreGeneratedInSourceOrder() {
        assertEquals(lines(
                        "p {",
                        "  width: 1px;",
                        "}",
                        "a {",
                        "  width: 2px;",
                        "}"),
                generate("p { width: 1px; } a { width: 2px; }"));
    }

    @Test
    void emptyRuleStillProducesBraces() {
        assertEquals(lines(
                        "p {",
                        "}"),
                generate("p { if[FALSE] { width: 1px; } }"));
    }

    @Test
    void emptyStylesheetGeneratesNothing() {
        assertEquals("", generate("A := 1px;"));
    }

    @Test
    void declarationsAreIndentedWithTwoSpacesAndRulesAreNot() {
        String css = generate("p { width: 1px; height: 2px; }");
        for (String line : css.split("\n")) {
            if (line.startsWith("p") || line.equals("}")) {
                assertFalse(line.startsWith(" "), "Rule lines must not be indented: '" + line + "'");
            } else {
                assertTrue(line.startsWith("  ") && !line.startsWith("   "),
                        "Declarations need exactly two spaces: '" + line + "'");
            }
        }
    }
}
