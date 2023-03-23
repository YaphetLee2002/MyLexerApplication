package service;

import java.util.List;

public interface LexerService {

    public void Pretreatment(List<Character> str, String path);

    public StringBuilder getTokens(List<String> pretreatFile) throws Exception;

}
