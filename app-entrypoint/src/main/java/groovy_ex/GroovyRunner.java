package groovy_ex;

import bean.LoginEvent;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.flink.cep.pattern.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GroovyRunner {

    public static void main(String[] args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("groovy");

        try {
            List<String> res = readResourceFile("pt.groovy");
            StringBuilder sb = new StringBuilder();
            for (String s : res) {
                sb.append(s);
            }

            // 实例化Groovy对象
            GroovyClassLoader gcl = new GroovyClassLoader();
            Class groovyClass = gcl.parseClass(new File("app-entrypoint/src/main/resources/pt.groovy"));
            GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

            // 调用Groovy对象的方法
            Pattern<LoginEvent, LoginEvent> pattern
                    = (Pattern<LoginEvent, LoginEvent>) groovyObject.invokeMethod("getP", null);
            System.out.println(pattern.toString());
//            groovyObject.invokeMethod("getP", null);
//            engine.eval(sb.toString());
//            engine.eval("println 'Hello, Groovy!'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final List<String> readResourceFile(String filePath) {
        // 假设我们要读取的文件名为 "example.txt"，位于resources目录
        // 使用ClassLoader获取文件的InputStream
        InputStream inputStream = GroovyRunner.class.getClassLoader().getResourceAsStream(filePath);
        List<String> lists = new ArrayList<>();
        // 确保inputStream不为null
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lists.add(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Could not find the file");
        }
        return lists;

    }
}