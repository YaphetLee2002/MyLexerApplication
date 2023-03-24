package service;

import java.util.List;

/**
 * @Author: 李亚赟
 * @Date: 2023/3/23 19:00
 * @Description: 词法分析中的预处理方法
 */
public interface LexerService {

    public void Pretreatment(List<Character> str, String path);

    public StringBuilder getTokens(List<String> pretreatFile) throws Exception;

}
