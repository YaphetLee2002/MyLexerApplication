package service.impl;

import pojo.Keywords;
import pojo.Symbol;
import service.WordJudgeService;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @Author: 李亚赟
 * @Date: 2023/3/23 19:00
 * @Description: 词法分析中的关键字和符号判断方法接口的实现
 */
public class WordJudgeServiceImpl implements WordJudgeService {

    /**
     * 判断是否为关键字
     * @param s 待判断的字符串
     * @return true or false
     * @throws IllegalAccessException
     */
    public boolean isKeyword(String s) throws IllegalAccessException {

        // 获得一个单例类
        Keywords keywords = Keywords.getKeywords();

        // 逐一比较类中的属性，看是否有与其一致的
        Field[] fields = keywords.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String value = field.get(keywords).toString();
            if (Objects.equals(value, s)) {
                return true;
            }
        }
        return false;

    }

    /**
     * 判断是否为符号集中的符号
     * @param s 待判断的字符串
     * @return true or false
     * @throws IllegalAccessException
     */
    public boolean isSymbol(String s) throws IllegalAccessException {

        // 这里同样是一个单例类
        Symbol symbol = Symbol.getSymbol();

        // 逐一比较类中的属性，看是否有与其一致的
        Field[] fields = symbol.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String value = field.get(symbol).toString();
            if (Objects.equals(value, s)) {
                return true;
            }
        }
        return false;
    }
}
