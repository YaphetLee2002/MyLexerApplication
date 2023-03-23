package service.impl;

import pojo.DFA;
import service.LexerService;
import service.WordJudgeService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class LexerServiceImpl implements LexerService {
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
                    /*下面是遇到注释//或/*的时候*/
                    c = str.get(currentPosition + 1); //否则c取p指向字符的下一个字符
                    if (c == '/') //遇到了双斜杠
                    {
                        lineCommentPosition = currentPosition; //标记双斜杠的开始
                        currentPosition += 2; //p指向双斜杠后面的字符
                    }
                    else if (c == '*') //遇到了/*
                    {
                        blockCommentPosition = currentPosition; //标记/*的开始
                        currentPosition += 2; //p指向/*后面的字符
                    }
                    /*上面是遇到注释//或/*的时候*/
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

    @Override
    public StringBuilder getTokens(List<String> pretreatFile) throws Exception {

        WordJudgeService wordJudgeService = new WordJudgeServiceImpl();

        String line;

        int lineIndex = 0;
        int charIndex = 0;
        int tokenNum = 0;
        String state = DFA.INITIAL;
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
                case DFA.INITIAL:
                {
                    token = "";
                    if (c == '_' || Character.isAlphabetic(c))
                    {
                        if (c == 'u' || c == 'U' || c == 'l' || c == 'L')
                        {
                            state = DFA.MIDCHAR;
                        }
                        else
                        {
                            state = DFA.IDENTIFIER;
                        }
                        token = token + c;
                    }
                    else if (Character.isDigit(c))
                    {
                        state = DFA.INTERGER;
                        token = token + c;
                    }
                    else if (c == '\'')
                    {
                        state = DFA.CHAR;
                        token = token + c;
                    }
                    else if (c == '"')
                    {
                        state = DFA.STRING;
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
                            state = DFA.SYMBOL;
                        }
                        else
                        {
                            throw new Exception("Illegal character: " + c);
                        }
                    }
                    break;
                }
                case DFA.IDENTIFIER:
                {
                    if (Character.isAlphabetic(c) || Character.isDigit(c) || c == '_')
                    {
                        state = DFA.IDENTIFIER;
                        token = token + c;
                    }
                    else
                    {
                        if (wordJudgeService.isKeyword(token))
                        {
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
                        state = DFA.INITIAL;
                        pre = true;
                    }
                    break;
                }
                case DFA.MIDCHAR:
                {
                    if (c == '\'')
                    {
                        state = DFA.CHAR;
                        token = token + c;
                    }
                    else if (c == '"')
                    {
                        state = DFA.STRING;
                        token = token + c;
                    }
                    else if (c == '8')
                    {
                        state = DFA.MID_STRING;
                        token = token + c;
                    }
                    else
                    {
                        state = DFA.IDENTIFIER;
                        token = token + c;
                    }
                    break;
                }
                case DFA.CHAR:
                {
                    if (c != '\'')
                    {
                        state = DFA.CHAR;
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
                        state = DFA.INITIAL;
                    }
                    break;

                }
                case DFA.MID_STRING:
                    if (c == '"')
                    {
                        state = DFA.STRING;
                        token = token + c;
                    }
                    else
                    {
                        state = DFA.IDENTIFIER;
                        token = token + c;
                    }
                    break;
                case DFA.STRING:
                {
                    if (c != '"')
                    {
                        state = DFA.STRING;
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
                        state = DFA.INITIAL;
                    }
                    break;
                }
                case DFA.INTERGER:
                {
                    if (Character.isDigit(c) || c == 'x' || c == 'X' || c == 'A' || c == 'B' || c == 'C' || c == 'D' || c == 'E' || c == 'F' || c == 'a' || c == 'b' || c == 'c' || c == 'd' || c == 'e' || c == 'f' || c == 'L' || c == 'l' || c == 'U' || c == 'u')
                    {
                        state = DFA.INTERGER;
                        token = token + c;
                    }
                    else if (c == '.')
                    {
                        state = DFA.FLOAT;
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
                        state = DFA.INITIAL;
                    }
                    break;
                }
                case DFA.FLOAT:
                {
                    if (Character.isDigit(c) || c == 'e' || c == 'E' || c == 'f' || c == 'F' || c == 'L' || c == 'l' || c == 'p' || c == 'P' || c == '+' || c == '-' || c == 'A' || c == 'B' || c == 'C' || c == 'D' || c == 'a' || c == 'b' || c == 'c' || c == 'd')
                    {
                        state = DFA.FLOAT;
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
                        state = DFA.INITIAL;
                    }
                    break;
                }
                case DFA.SYMBOL:
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
                        state = DFA.INITIAL;
                    }
                    else
                    {
                        state = DFA.SYMBOL;
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
