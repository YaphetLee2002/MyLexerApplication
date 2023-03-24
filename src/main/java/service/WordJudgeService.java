package service;

/**
 * @Author: 李亚赟
 * @Date: 2023/3/23 19:00
 * @Description: 词法分析中的关键字和符号判断
 */
public interface WordJudgeService {

    public boolean isKeyword(String s) throws IllegalAccessException;

    public boolean isSymbol(String s) throws IllegalAccessException;

}
