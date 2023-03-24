package pojo;

/**
 * @Author: 李亚赟
 * @Date: 2023/3/23 19:00
 * @Description: 关键字类
 */
public class Keywords {

    private static volatile Keywords keywords;

    private Keywords() {}

    public static Keywords getKeywords() {
        if (keywords == null) {
            synchronized (Keywords.class) {
                if (keywords == null) {
                    keywords = new Keywords();
                }
            }
        }
        return keywords;
    }

    private static final String AUTO = "auto";
    private static final String BREAK = "break";
    private static final String CASE = "case";
    private static final String CHAR = "char";
    private static final String CONST = "const";
    private static final String CONTINUE = "continue";
    private static final String DEFAULT = "default";
    private static final String DO = "do";
    private static final String DOUBLE = "double";
    private static final String ELSE = "else";
    private static final String ENUM = "enum";
    private static final String EXTERN = "extern";
    private static final String FLOAT = "float";
    private static final String FOR = "for";
    private static final String GOTO = "goto";
    private static final String IF = "if";
    private static final String INLINE = "inline";
    private static final String INT = "int";
    private static final String LONG = "long";
    private static final String REGISTER = "register";
    private static final String RESTRICT = "restrict";
    private static final String RETURN = "return";
    private static final String SHORT = "short";
    private static final String SIGNED = "signed";
    private static final String SIZEOF = "sizeof";
    private static final String STATIC = "static";
    private static final String STRUCT = "struct";
    private static final String SWITCH = "switch";
    private static final String TYPEDEF = "typedef";
    private static final String UNION = "union";
    private static final String UNSIGNED = "unsigned";
    private static final String VOID = "void";
    private static final String VOLATILE = "volatile";
    private static final String WHILE = "while";
}
