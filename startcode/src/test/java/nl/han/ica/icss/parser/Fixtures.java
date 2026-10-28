package nl.han.ica.icss.parser;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class Fixtures {

    public static AST uncheckedLevel0() {
        Stylesheet stylesheet = new Stylesheet();
		/*
		p {
			background-color: #ffffff;
			width: 500px;
		}
		*/
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new PixelLiteral("500px")))
        );
		/*
		a {
			color: #ff0000;
		}
		*/
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("a"))
                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#ff0000")))
        );
		/*
		#menu {
			width: 520px;
		}
		*/
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#menu"))
                .addChild((new Declaration("width"))
                        .addChild(new PixelLiteral("520px")))
        );
		/*
		.menu {
			color: #000000;
		}
		*/
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".menu"))
                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#000000")))
        );

        return new AST(stylesheet);
    }

    public static AST uncheckedLevel1() {
        Stylesheet stylesheet = new Stylesheet();
		/*
			LinkColor := #ff0000;
			ParWidth := 500px;
			AdjustColor := TRUE;
			UseLinkColor := FALSE;
		 */
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("LinkColor"))
                .addChild(new ColorLiteral("#ff0000"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("ParWidth"))
                .addChild(new PixelLiteral("500px"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("AdjustColor"))
                .addChild(new BoolLiteral(true))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("UseLinkColor"))
                .addChild(new BoolLiteral(false))
        );
   	    /*
	        p {
	        background-color: #ffffff;
	        width: ParWidth;
            }
	    */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new VariableReference("ParWidth")))
        );
        /*
        a {
	        color: LinkColor;
        }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("a"))
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("LinkColor")))
        );
        /*
            #menu {
	            width: 520px;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#menu"))
                .addChild((new Declaration("width"))
                        .addChild(new PixelLiteral("520px")))
        );
        /*
            .menu {
	            color: #000000;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".menu"))
                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#000000")))
        );
        return new AST(stylesheet);
    }

    public static AST uncheckedLevel1Extended() {
        Stylesheet stylesheet = new Stylesheet();
    /*
       Red := #ff0000;
       LinkColor := Red;
       ParWidth := 500px;
       AdjustColor := TRUE;
       UseLinkColor := FALSE;
    */
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("Red"))
                .addChild(new ColorLiteral("#ff0000"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("LinkColor"))
                .addChild(new VariableReference("Red"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("ParWidth"))
                .addChild(new PixelLiteral("500px"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("AdjustColor"))
                .addChild(new BoolLiteral(true))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("UseLinkColor"))
                .addChild(new BoolLiteral(false))
        );
    /*
       p {
           background-color: #ffffff;
           width: ParWidth;
       }
    */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new VariableReference("ParWidth")))
        );
    /*
       a {
           color: LinkColor;
       }
    */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("a"))
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("LinkColor")))
        );
    /*
       .menu {
           Menucolor := #000000;
           color: Menucolor;
       }
    */
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".menu"))
                .addChild((new VariableAssignment())
                        .addChild(new VariableReference("Menucolor"))
                        .addChild(new ColorLiteral("#000000")))
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("Menucolor")))
        );
    /*
       #menu {
           width: 520px;
       }
    */
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#menu"))
                .addChild((new Declaration("width"))
                        .addChild(new PixelLiteral("520px")))
        );
        return new AST(stylesheet);
    }

    public static AST uncheckedLevel2() {
        Stylesheet stylesheet = new Stylesheet();
		/*
			LinkColor := #ff0000;
			ParWidth := 500px;
			AdjustColor := TRUE;
			UseLinkColor := FALSE;
		 */
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("LinkColor"))
                .addChild(new ColorLiteral("#ff0000"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("ParWidth"))
                .addChild(new PixelLiteral("500px"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("AdjustColor"))
                .addChild(new BoolLiteral(true))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("UseLinkColor"))
                .addChild(new BoolLiteral(false))
        );
   	    /*
	        p {
	        background-color: #ffffff;
	        width: ParWidth;
            }
	    */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new VariableReference("ParWidth")))
        );
        /*
        a {
	        color: LinkColor;
        }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("a"))
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("LinkColor")))
        );
        /*
            #menu {
        	width: ParWidth + 2 * 10px;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#menu"))
                .addChild((new Declaration("width"))
                        .addChild((new AddOperation())
                                .addChild(new VariableReference("ParWidth"))
                                .addChild((new MultiplyOperation())
                                        .addChild(new ScalarLiteral("2"))
                                        .addChild(new PixelLiteral("10px"))

                                ))));
        /*
            .menu {
	            color: #000000;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".menu"))
                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#000000")))
        );
        return new AST(stylesheet);
    }

    public static AST uncheckedLevel2Extended() {
        Stylesheet stylesheet = new Stylesheet();

        // LinkColor := #ff0000;
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("LinkColor"))
                .addChild(new ColorLiteral("#ff0000"))
        );
        // ParWidth := 100px + 200px * 2;
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("ParWidth"))
                .addChild((new AddOperation())
                        .addChild(new PixelLiteral("100px"))
                        .addChild((new MultiplyOperation())
                                .addChild(new PixelLiteral("200px"))
                                .addChild(new ScalarLiteral("2"))))
        );
        // PlusNumber := 100px + 200px + 2px;   ->  (100px + 200px) + 2px
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("PlusNumber"))
                .addChild((new AddOperation())
                        .addChild((new AddOperation())
                                .addChild(new PixelLiteral("100px"))
                                .addChild(new PixelLiteral("200px")))
                        .addChild(new PixelLiteral("2px")))
        );
        // PlusMinusNumber := 100px + 200px - 2px;   ->  (100px + 200px) - 2px
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("PlusMinusNumber"))
                .addChild((new SubtractOperation())
                        .addChild((new AddOperation())
                                .addChild(new PixelLiteral("100px"))
                                .addChild(new PixelLiteral("200px")))
                        .addChild(new PixelLiteral("2px")))
        );
        // MinusPlusNumber := 100px - 20px + 2px;   ->  (100px - 20px) + 2px
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("MinusPlusNumber"))
                .addChild((new AddOperation())
                        .addChild((new SubtractOperation())
                                .addChild(new PixelLiteral("100px"))
                                .addChild(new PixelLiteral("20px")))
                        .addChild(new PixelLiteral("2px")))
        );
        // MultiplyChain := 10px * 10 * 2 * 2;   ->  ((10px * 10) * 2) * 2
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("MultiplyChain"))
                .addChild((new MultiplyOperation())
                        .addChild((new MultiplyOperation())
                                .addChild((new MultiplyOperation())
                                        .addChild(new PixelLiteral("10px"))
                                        .addChild(new ScalarLiteral("10")))
                                .addChild(new ScalarLiteral("2")))
                        .addChild(new ScalarLiteral("2")))
        );
        // VariableCalculationNumber := MultiplyChain - 100px;
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("VariableCalculationNumber"))
                .addChild((new SubtractOperation())
                        .addChild(new VariableReference("MultiplyChain"))
                        .addChild(new PixelLiteral("100px")))
        );
        // MinusChain := 100px - 20px - 2px;   ->  (100px - 20px) - 2px
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("MinusChain"))
                .addChild((new SubtractOperation())
                        .addChild((new SubtractOperation())
                                .addChild(new PixelLiteral("100px"))
                                .addChild(new PixelLiteral("20px")))
                        .addChild(new PixelLiteral("2px")))
        );

        // PercentageCalc := 50% - 20%;
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("PercentageCalc"))
                .addChild((new SubtractOperation())
                        .addChild(new PercentageLiteral("50%"))
                        .addChild(new PercentageLiteral("20%")))
        );
        // AdjustColor := TRUE;
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("AdjustColor"))
                .addChild(new BoolLiteral(true))
        );
        // UseLinkColor := FALSE;
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("UseLinkColor"))
                .addChild(new BoolLiteral(false))
        );

        // p { background-color: #ffffff; width: ParWidth; }
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new VariableReference("ParWidth")))
        );
        // a { color: LinkColor; }
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("a"))
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("LinkColor")))
        );
        // #menu { width: ParWidth + 2 * 10px; height: 2 * 10px + 5px; }
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#menu"))
                .addChild((new Declaration("width"))
                        .addChild((new AddOperation())
                                .addChild(new VariableReference("ParWidth"))
                                .addChild((new MultiplyOperation())
                                        .addChild(new ScalarLiteral("2"))
                                        .addChild(new PixelLiteral("10px")))))
                .addChild((new Declaration("height"))
                        .addChild((new AddOperation())
                                .addChild((new MultiplyOperation())
                                        .addChild(new ScalarLiteral("2"))
                                        .addChild(new PixelLiteral("10px")))
                                .addChild(new PixelLiteral("5px"))))
        );
        // .menu { color: #000000; }
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".menu"))
                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#000000")))
        );
        return new AST(stylesheet);
    }

    public static AST uncheckedLevel3() {
        Stylesheet stylesheet = new Stylesheet();
		/*
			LinkColor := #ff0000;
			ParWidth := 500px;
			AdjustColor := TRUE;
			UseLinkColor := FALSE;
		 */
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("LinkColor"))
                .addChild(new ColorLiteral("#ff0000"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("ParWidth"))
                .addChild(new PixelLiteral("500px"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("AdjustColor"))
                .addChild(new BoolLiteral(true))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("UseLinkColor"))
                .addChild(new BoolLiteral(false))
        );
   	    /*
	        p {
				background-color: #ffffff;
				width: ParWidth;
				if[AdjustColor] {
	    			color: #124532;
	    			if[UseLinkColor]{
	        			bg-color: LinkColor;
	    			}
				}
			}
			p {
				background-color: #ffffff;
				width: ParWidth;
				if[AdjustColor] {
	    			color: #124532;
	    		if[UseLinkColor]{
	        		background-color: LinkColor;
	    		} else {
	        		background-color: #000000;
	    		}
	    		height: 20px;
			}
}
	    */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new VariableReference("ParWidth")))
                .addChild((new IfClause())
                        .addChild(new VariableReference("AdjustColor"))
                        .addChild((new Declaration("color")
                                .addChild(new ColorLiteral("#124532"))))
                        .addChild((new IfClause())
                                .addChild(new VariableReference("UseLinkColor"))
                                .addChild(new Declaration("background-color").addChild(new VariableReference("LinkColor")))
                                .addChild((new ElseClause())
                                        .addChild(new Declaration("background-color").addChild(new ColorLiteral("#000000")))

                                )
                        ))
                .addChild((new Declaration("height"))
                        .addChild(new PixelLiteral("20px")))
        );
        /*
        a {
	        color: LinkColor;
        }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("a"))
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("LinkColor"))
                )
        );
        /*
            #menu {
        	width: ParWidth + 20px;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#menu"))
                .addChild((new Declaration("width"))
                        .addChild((new AddOperation())
                                .addChild(new VariableReference("ParWidth"))
                                .addChild(new PixelLiteral("20px"))
                        )
                )
        );
        /*


         .menu {
				color: #000000;
    			background-color: LinkColor;

			}

        */
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".menu"))

                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#000000"))
                )
                .addChild((new Declaration("background-color"))
                        .addChild(new VariableReference("LinkColor"))
                )

        );

        return new AST(stylesheet);
    }
}
