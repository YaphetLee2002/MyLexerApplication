package service.impl;

import pojo.StateCode;
import service.LexerService;
import service.WordJudgeService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @Author: 李亚赟
 * @Date: 2023/3/23 19:00
 * @Description: 词法分析中的预处理方法接口的实现
 */
public class LexerServiceImpl implements LexerService {

    /**
     * 预处理方法
     * @param str 待处理的字符串
     * @param path 待处理的文件路径
     * @throws IOException
     */
    @Override
    public void Pretreatment(List<Character> str, String path)
    {
        Character c;
        int len;
        int currentPosition = 0;
        int singleQuotationPosition = -1;
        int doubleQuotationPosition = -1;
        int lineCommentPosition = -1;
        int blockCommentPosition = -1;
        int numberSignPosition = -1;

        while (currentPosition < str.size())
        {
            c = str.get(currentPosition);

            switch (c)
            {
                case '\'':
                {
                    if (doubleQuotationPosition != -1 || lineCommentPosition != -1 || blockCommentPosition != -1) //当遇到过双引号、//或/*的时候，则不需要再判断'//'的情况了。
                    {
                        currentPosition++;
                        continue;
                    }

                    // Without """ or "//" or "/*"
                    if (singleQuotationPosition == -1)
                    {
                        singleQuotationPosition = currentPosition++;
                    }
                    else
                    {
                        len = (currentPosition++) - singleQuotationPosition;
                        if (len == 2 && str.get(singleQuotationPosition + 1) == '\\')
                        {
                            continue;
                        }
                        singleQuotationPosition = -1;
                    }
                    break;
                }

                case '\"':
                {
                    if (singleQuotationPosition != -1 || lineCommentPosition != -1 || blockCommentPosition != -1) //当遇到过单引号、//或/*的时候，则不需要处理
                    {
                        currentPosition++;
                        continue;
                    }
                    // Without "'" or "//" or "/*"
                    if (doubleQuotationPosition == -1)
                    {
                        doubleQuotationPosition = currentPosition++;
                    }
                    else
                    {
                        len = (currentPosition++) - doubleQuotationPosition;
                        if (len == 2 && str.get(doubleQuotationPosition + 1) == '\\')
                        {
                            continue;
                        }
                        doubleQuotationPosition = -1;
                    }
                    break;
                }
                case '/':
                {
                    if (singleQuotationPosition != -1 || doubleQuotationPosition != -1 || lineCommentPosition != -1 || blockCommentPosition != -1) //如果是单引号、双引号、斜杠、/*的后面
                    {
                        currentPosition++;
                        continue;
                    }

                    c = str.get(currentPosition + 1);
                    if (c == '/')
                    {
                        lineCommentPosition = currentPosition;
                        currentPosition += 2;
                    }
                    else if (c == '*')
                    {
                        blockCommentPosition = currentPosition;
                        currentPosition += 2;
                    }

                    else
                    {
                        currentPosition++;
                    }
                    break;
                }
                case '*':
                {
                    if (singleQuotationPosition != -1 || doubleQuotationPosition != -1 || lineCommentPosition != -1) //如果是单引号、双引号、斜杠、/*的后面
                    {
                        currentPosition++;
                        continue;
                    }
                    if (str.get(currentPosition + 1) != '/')
                    {
                        currentPosition++;
                        continue;
                    }
                    currentPosition += 2;
                    int x = currentPosition - blockCommentPosition;
                    for (int i = 0; i < x; i++)
                    {
                        if (str.get(blockCommentPosition) == '\n')
                        {

                        }
                        else
                        {
                            str.set(blockCommentPosition, ' ');
                        }
                        blockCommentPosition++;
                    }
                    blockCommentPosition = -1;
                    break;
                }
                case '\n':
                {
                    if (lineCommentPosition == -1)
                    {
                        currentPosition++;
                        continue;
                    }
                    c = str.get(currentPosition - 1);

                    int length = (c == '\r' ? ((currentPosition++) - 1) : currentPosition++);
                    for (int i = lineCommentPosition; i < length; i++) {
                        str.set(i, ' ');
                    }
                    lineCommentPosition = -1;
                    break;
                }
                case '#':
                {
                    if (singleQuotationPosition != -1 || doubleQuotationPosition != -1 || lineCommentPosition != -1 || blockCommentPosition != -1)
                    {
                        currentPosition++;
                        continue;
                    }
                    else
                    {
                        numberSignPosition = currentPosition;
                        currentPosition++;
                    }
                    break;
                }
                case '>':
                {
                    if (singleQuotationPosition != -1 || doubleQuotationPosition != -1 || lineCommentPosition != -1 || blockCommentPosition != -1)
                    {
                        currentPosition++;
                        continue;
                    }
                    else if (numberSignPosition != -1)
                    {
                        for (int i = numberSignPosition; i < currentPosition + 1; i++) {
                            str.set(i, ' ');
                        }
                    }
                    else
                    {
                        currentPosition++;
                        continue;
                    }
                    break;
                }
                default:
                    currentPosition++;
                    break;
            }

            if (lineCommentPosition != -1)
            {
                for (int i = lineCommentPosition; i < currentPosition; i++) {
                    str.set(i, ' ');
                }
            }

        }
        StringBuilder sb = new StringBuilder();
        for (Character character : str) {
            sb.append(character);
        }
        String outputStream = sb.toString();
        try {
            Files.write(Paths.get(path), outputStream.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 词法分析
     * @param pretreatFile 预处理后的文件内容
     * @return 词法分析结果文件内容StringBuilder
     * @throws Exception
     */
    @Override
    public StringBuilder getTokens(List<String> pretreatFile) throws Exception {

        WordJudgeService wordJudgeService = new WordJudgeServiceImpl();

        String line;

        int lineIndex = 0;
        int charIndex = 0;
        int tokenNum = 0;
        String state = StateCode.INITIAL;
        StringBuilder tokens = new StringBuilder();
        String token = "";
        Character c = null;
        boolean SymbolFlag = false;
        boolean pre = false;
        boolean flag = true;
        while (flag)
        {
            if (!pre)
            {
                c = '\0';
                while (true)
                {
                    if (lineIndex < pretreatFile.size())
                    {
                        line = pretreatFile.get(lineIndex);
                        if (charIndex < line.length())
                        {
                            c = line.charAt(charIndex);
                            charIndex++;
                            break;
                        }
                        else if (charIndex == line.length())
                        {
                            c = '\n';
                            charIndex++;
                            break;
                        }
                        else
                        {
                            lineIndex++;
                            charIndex = 0;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
            }
            pre = false;
            switch (state)
            {
                case StateCode.INITIAL:
                {
                    token = "";
                    if (c == '_' || Character.isAlphabetic(c))
                    {
                        if (c == 'u' || c == 'U' || c == 'l' || c == 'L')
                        {
                            state = StateCode.MIDCHAR;
                        }
                        else
                        {
                            state = StateCode.IDENTIFIER;
                        }
                        token = token + c;
                    }
                    else if (Character.isDigit(c))
                    {
                        state = StateCode.INTERGER;
                        token = token + c;
                    }
                    else if (c == '\'')
                    {
                        state = StateCode.CHAR;
                        token = token + c;
                    }
                    else if (c == '"')
                    {
                        state = StateCode.STRING;
                        token = token + c;
                    }
                    else if (c == ' '||c=='\n'||c=='\r'||c=='\t')
                    {

                    }
                    else if (c == '\0')
                    {
                        flag = false;
                        String tokenstream = "";
                        tokenstream = "[@" + tokenNum + "," + (charIndex - token.length() + 1) + ":" + charIndex + "='" + "<'EOF'>" + "',<" + "EOF" + ">," + lineIndex + ":" + (charIndex - token.length() + 1) + "]\n";
                        tokens.append(tokenstream);
                        System.out.println(tokenstream);
                    }
                    else
                    {
                        token = token + c;
                        SymbolFlag = wordJudgeService.isSymbol(token);
                        if (SymbolFlag)
                        {
                            state = StateCode.SYMBOL;
                        }
                        else
                        {
                            throw new Exception("Illegal character: " + c);
                        }
                    }
                    break;
                }
                case StateCode.IDENTIFIER:
                {
                    if (Character.isAlphabetic(c) || Character.isDigit(c) || c == '_')
                    {
                        state = StateCode.IDENTIFIER;
                        token = token + c;
                    }
                    else
                    {
                        if (wordJudgeService.isKeyword(token))
                        {
                            state = StateCode.KEYWORD;
                            String tokenstream = "";
                            tokenstream = "[@" + (tokenNum) + "," + (charIndex - token.length()-1) + ":" + (charIndex-2) + "='" + token + "',<'" + token + "'>," + (lineIndex) + ":" + (charIndex - token.length() -1) + "]\n";
                            tokens.append(tokenstream);
                            System.out.println(tokenstream);
                        }
                        else
                        {
                            String tokenstream = "";
                            tokenstream = "[@" + (tokenNum) + "," + (charIndex - token.length() - 1) + ":" + (charIndex-2) + "='" + token + "',<" + "Identifier" + ">," + (lineIndex) + ":" + (charIndex - token.length()-1) + "]\n";
                            tokens.append(tokenstream);
                            System.out.println(tokenstream);
                        }
                        tokenNum++;
                        state = StateCode.INITIAL;
                        pre = true;
                    }
                    break;
                }
                case StateCode.MIDCHAR:
                {
                    if (c == '\'')
                    {
                        state = StateCode.CHAR;
                        token = token + c;
                    }
                    else if (c == '"')
                    {
                        state = StateCode.STRING;
                        token = token + c;
                    }
                    else if (c == '8')
                    {
                        state = StateCode.MID_STRING;
                        token = token + c;
                    }
                    else
                    {
                        state = StateCode.IDENTIFIER;
                        token = token + c;
                    }
                    break;
                }
                case StateCode.CHAR:
                {
                    if (c != '\'')
                    {
                        state = StateCode.CHAR;
                        token = token + c;
                    }
                    else
                    {
                        token = token + c;
                        String tokenstream = "";
                        tokenstream = "[@" + (tokenNum) + "," + (charIndex - token.length() -1) + ":" + (charIndex-2) + "='" + token + "',<" + "CharacterConstant" + ">," + (lineIndex) + ":" + (charIndex - token.length()-1) + "]\n";
                        tokens.append(tokenstream);
                        System.out.println(tokenstream);
                        tokenNum++;
                        state = StateCode.INITIAL;
                    }
                    break;

                }
                case StateCode.MID_STRING:
                    if (c == '"')
                    {
                        state = StateCode.STRING;
                        token = token + c;
                    }
                    else
                    {
                        state = StateCode.IDENTIFIER;
                        token = token + c;
                    }
                    break;
                case StateCode.STRING:
                {
                    if (c != '"')
                    {
                        state = StateCode.STRING;
                        token = token + c;
                    }
                    else
                    {
                        token = token + c;
                        String tokenstream = "";
                        tokenstream = "[@" + (tokenNum) + "," + (charIndex - token.length() -1) + ":" + (charIndex-2) + "='" + token + "',<" + "StringLiteral" + ">," + (lineIndex) + ":" + (charIndex - token.length() - 1) + "]\n";
                        tokens.append(tokenstream);
                        System.out.println(tokenstream);
                        tokenNum++;
                        state = StateCode.INITIAL;
                    }
                    break;
                }
                case StateCode.INTERGER:
                {
                    if (Character.isDigit(c) || c == 'x' || c == 'X' || c == 'A' || c == 'B' || c == 'C' || c == 'D' || c == 'E' || c == 'F' || c == 'a' || c == 'b' || c == 'c' || c == 'd' || c == 'e' || c == 'f' || c == 'L' || c == 'l' || c == 'U' || c == 'u')
                    {
                        state = StateCode.INTERGER;
                        token = token + c;
                    }
                    else if (c == '.')
                    {
                        state = StateCode.FLOAT;
                        token = token + c;
                    }
                    else
                    {
                        pre = true;
                        String tokenstream = "";
                        tokenstream = "[@" + (tokenNum) + "," + (charIndex - token.length() - 1) + ":" + (charIndex-2) + "='" + token + "',<" + "IntegerConstant" + ">," + (lineIndex) + ":" + (charIndex - token.length() - 1) + "]\n";
                        tokens.append(tokenstream);
                        System.out.println(tokenstream);
                        tokenNum++;
                        state = StateCode.INITIAL;
                    }
                    break;
                }
                case StateCode.FLOAT:
                {
                    if (Character.isDigit(c) || c == 'e' || c == 'E' || c == 'f' || c == 'F' || c == 'L' || c == 'l' || c == 'p' || c == 'P' || c == '+' || c == '-' || c == 'A' || c == 'B' || c == 'C' || c == 'D' || c == 'a' || c == 'b' || c == 'c' || c == 'd')
                    {
                        state = StateCode.FLOAT;
                        token = token + c;
                    }
                    else
                    {
                        pre = true;
                        String tokenstream = "";
                        tokenstream = "[@" + (tokenNum) + "," + (charIndex - token.length() - 1) + ":" + (charIndex-2) + "='" + token + "',<" + "FloatingConstant" + ">," + (lineIndex) + ":" + (charIndex - token.length() - 1) + "]\n";
                        tokens.append(tokenstream);
                        System.out.println(tokenstream);
                        tokenNum++;
                        state = StateCode.INITIAL;
                    }
                    break;
                }
                case StateCode.SYMBOL:
                {
                    String temp = token + c;
                    if (!wordJudgeService.isSymbol(temp))
                    {
                        pre = true;
                        String tokenstream = "";
                        tokenstream = "[@" + (tokenNum) + "," + (charIndex - token.length() - 1) + ":" + (charIndex-2) + "='" + token + "',<'" + token + "'>," + (lineIndex) + ":" + (charIndex - token.length() - 1) + "]\n";
                        tokens.append(tokenstream);
                        System.out.println(tokenstream);
                        tokenNum++;
                        state = StateCode.INITIAL;
                    }
                    else
                    {
                        state = StateCode.SYMBOL;
                        token = temp;
                    }
                    break;
                }
                default:
                    break;
            }
        }
        return tokens;
    }
}
