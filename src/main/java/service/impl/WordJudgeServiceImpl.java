package service.impl;

import pojo.Keywords;
import pojo.Symbol;
import service.WordJudgeService;

import java.lang.reflect.Field;
import java.util.Objects;

public class WordJudgeServiceImpl implements WordJudgeService {
    public boolean isKeyword(String s) throws IllegalAccessException {
        Keywords keywords = Keywords.getKeywords();
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

    public boolean isSymbol(String s) throws IllegalAccessException {
        Symbol symbol = Symbol.getSymbol();

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
