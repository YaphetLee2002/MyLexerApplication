package pojo;


/**
 * @Author: 李亚赟
 * @Date: 2023/3/23 19:00
 * @Description: 符号集
 */
public class Symbol {

    private static volatile Symbol symbol;

    private Symbol() {}

    public static Symbol getSymbol() {
        if (symbol == null) {
            synchronized (Symbol.class) {
                if (symbol == null) {
                    symbol = new Symbol();
                }
            }
        }
        return symbol;
    }

    private static final String SYMBOL1 = "[";
    private static final String SYMBOL2 = "]";
    private static final String SYMBOL3 = "(";
    private static final String SYMBOL4 = ")";
    private static final String SYMBOL5 = "{";
    private static final String SYMBOL6 = "}";
    private static final String SYMBOL7 = ".";
    private static final String SYMBOL8 = "->";
    private static final String SYMBOL9 = "++";
    private static final String SYMBOL10 = "--";
    private static final String SYMBOL11 = "&";
    private static final String SYMBOL12 = "*";
    private static final String SYMBOL13 = "+";
    private static final String SYMBOL14 = "-";
    private static final String SYMBOL15 = "~";
    private static final String SYMBOL16 = "!";
    private static final String SYMBOL17 = "/";
    private static final String SYMBOL18 = "%";
    private static final String SYMBOL19 = "<<";
    private static final String SYMBOL20 = ">>";
    private static final String SYMBOL21 = "<";
    private static final String SYMBOL22 = ">";
    private static final String SYMBOL23 = "<=";
    private static final String SYMBOL24 = ">=";
    private static final String SYMBOL25 = "==";
    private static final String SYMBOL26 = "!=";
    private static final String SYMBOL27 = "^";
    private static final String SYMBOL28 = "|";
    private static final String SYMBOL29 = "&&";
    private static final String SYMBOL30 = "||";
    private static final String SYMBOL31 = "?";
    private static final String SYMBOL32 = ":";
    private static final String SYMBOL33 = ";";
    private static final String SYMBOL34 = "...";
    private static final String SYMBOL35 = "=";
    private static final String SYMBOL36 = "*=";
    private static final String SYMBOL37 = "/=";
    private static final String SYMBOL38 = "%=";
    private static final String SYMBOL39 = "+=";
    private static final String SYMBOL40 = "-=";
    private static final String SYMBOL41 = "<<=";
    private static final String SYMBOL42 = ">>=";
    private static final String SYMBOL43 = "&=";
    private static final String SYMBOL44 = "^=";
    private static final String SYMBOL45 = "|=";
    private static final String SYMBOL46 = ",";
    private static final String SYMBOL47 = "#";
    private static final String SYMBOL48 = "##";
    private static final String SYMBOL49 = "<:";
    private static final String SYMBOL50 = ":>";
    private static final String SYMBOL51 = "<%";
    private static final String SYMBOL52 = "%>";
    private static final String SYMBOL53 = "%:";
    private static final String SYMBOL54 = "%:%:";
}