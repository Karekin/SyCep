import bean.LoginEvent;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * @author StephenYou
 * Created on 2023-07-29
 * Description: run for show source code
 */
public class DemoApp_Groovy {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        env.setParallelism(1);


        KeyedStream<Tuple3<String, Long, String>, String> source = env.fromElements(
                        new Tuple3<String, Long, String>("1001", 1656914303000L, "success")
                        , new Tuple3<String, Long, String>("1001", 1656914304000L, "fail")
                        , new Tuple3<String, Long, String>("1001", 1656914305000L, "fail")
                        , new Tuple3<String, Long, String>("1001", 1656914306000L, "success")
                        , new Tuple3<String, Long, String>("1001", 1656914307000L, "fail")
                        , new Tuple3<String, Long, String>("1001", 1656914308000L, "success")
                        , new Tuple3<String, Long, String>("1001", 1656914309000L, "fail")
                        , new Tuple3<String, Long, String>("1001", 1656914310000L, "success")
                        , new Tuple3<String, Long, String>("1001", 1656914311000L, "fail")
                        , new Tuple3<String, Long, String>("1001", 1656914312000L, "fail")
                        , new Tuple3<String, Long, String>("1001", 1656914313000L, "success")
                        , new Tuple3<String, Long, String>("1001", 1656914314000L, "end")
                )
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<Tuple3<String, Long, String>>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                        .withTimestampAssigner((event, timestamp) ->{
                            return event.f1;
                        }))

                .keyBy(e -> e.f0);


        // 实例化Groovy对象
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class groovyClass = gcl.parseClass(new File("app-entrypoint/src/main/resources/pt2.groovy"));
        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

        // 调用Groovy对象的方法
        Pattern<Tuple3<String, Long, String>,?> pattern
                = (Pattern<Tuple3<String, Long, String>, Tuple3<String, Long, String>>) groovyObject.invokeMethod("getP", null);


        PatternStream patternStream = CEP.pattern(source, pattern);

        patternStream.select(new PatternSelectFunction<Tuple3<String, Long, String>, Map<String, List<Tuple3<String, Long, String>>>>() {

            @Override
            public Map<String, List<Tuple3<String, Long, String>>> select(Map<String, List<Tuple3<String, Long, String>>> pattern)
                    throws Exception {
                return pattern;
            }
        }).print();
        env.execute("org/apache/flink/cep");
    }

}
