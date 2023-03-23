import pojo.FilePath;
import service.LexerService;
import service.impl.LexerServiceImpl;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyLexerApplication {
    static List<String> pretreatFile = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        // 从实体类获取对应文件路径
        String sourceFilePath = FilePath.SOURCE_FILE_PATH;
        String pretreatFilePath = FilePath.PRETREAT_FILE_PATH;
        String tokensFilePath= FilePath.TOKENS_FILE_PATH;

        // 服务类接口
        LexerService lexerService = new LexerServiceImpl();


        // 读取C源文件并转化为List传入预处理方法
        FileInputStream fis = new FileInputStream(sourceFilePath);
        byte[] buffer = new byte[10];
        StringBuilder sb = new StringBuilder();
        while (fis.read(buffer) != -1) {
            sb.append(new String(buffer));
            buffer = new byte[10];
        }
        fis.close();
        String contents = sb.toString();
        List<Character> str = new ArrayList<>();
        for (int i = 0; i < contents.length(); i++) {
            str.add(contents.charAt(i));
        }

        lexerService.Pretreatment(str, pretreatFilePath);


        // 读取预处理后的文件至pretreatFile
        try (Scanner sc = new Scanner(new FileReader(pretreatFilePath))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                pretreatFile.add(line);
            }
        }

        // 词法分析
        StringBuilder tokens = lexerService.getTokens(pretreatFile);

        // 将tokens写入文件
        try {
            Files.write(Paths.get(tokensFilePath), tokens.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}