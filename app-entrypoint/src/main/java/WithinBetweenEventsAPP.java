import cep.CEP;
import cep.PatternSelectFunction;
import cep.PatternStream;
import cep.PatternTimeoutFunction;
import cep.pattern.Pattern;
import cep.pattern.WithinType;
import cep.pattern.conditions.IterativeCondition;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WithinBetweenEventsAPP {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        env.setParallelism(1);

        //数据源
        KeyedStream<Tuple3<String, Long, String>, String> source = env.fromElements(
                        new Tuple3<>("1001", 1656914303000L, "success"),
                        new Tuple3<>("1001", 1656914304000L, "fail"),
                        new Tuple3<>("1001", 1656914305000L, "fail"),
                        new Tuple3<>("1001", 1656914306000L, "success"),
                        new Tuple3<>("1001", 1656914307000L, "end"),
                        new Tuple3<>("1001", 1656914308000L, "success"),
                        new Tuple3<>("1001", 1656914309000L, "fail"),
                        new Tuple3<>("1001", 1656914310000L, "success"),
                        new Tuple3<>("1001", 1656914311000L, "fail"),
                        new Tuple3<>("1001", 1656914312000L, "fail"),
                        new Tuple3<>("1001", 1656914313000L, "success"),
                        new Tuple3<>("1001", 1656914316000L, "end")
                ).assignTimestampsAndWatermarks(WatermarkStrategy
                        .<Tuple3<String, Long, String>>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                        .withTimestampAssigner((event, timestamp) -> event.f1))
                .keyBy(e -> e.f0);

        Pattern<Tuple3<String, Long, String>, ?> pattern = Pattern
                .<Tuple3<String, Long, String>>begin("begin")
                .where(new Begincondition())
                .followedByAny("middle")
                .where(new Middlecondition())
                .within(Time.seconds(5), WithinType.PREVIOUS_AND_CURRENT)
                .followedBy("end")
                .where(new Endcondition())
                .within(Time.seconds(5), WithinType.PREVIOUS_AND_CURRENT);

        // TODO 内部构建 PatternStreamBuilder 并返回 PatternStream
        PatternStream<Tuple3<String, Long, String>> patternStream = CEP.pattern(source, pattern);

        OutputTag<Map<String, Object>> outputTag = new OutputTag<Map<String, Object>>("exec-timeout") {};

        SingleOutputStreamOperator<Map<String, Object>> select = patternStream.select(outputTag,
                new PatternTimeoutFunction<Tuple3<String, Long, String>, Map<String, Object>>() {
                    @Override
                    public Map<String, Object> timeout(Map<String, List<Tuple3<String, Long, String>>> pattern, long timeoutTimestamp) throws Exception {
                        // 处理超时逻辑
                        Map<String, Object> result = new HashMap<>();
                        result.put("timeoutTimestamp", timeoutTimestamp);
                        result.put("timedOutPattern", pattern);  // 将超时的模式信息放入结果
                        return result;
                    }
                },
                new PatternSelectFunction<Tuple3<String, Long, String>, Map<String, Object>>() {

                    @Override
                    public Map<String, Object> select(Map<String, List<Tuple3<String, Long, String>>> pattern) throws Exception {
                        // 处理正常匹配逻辑
                        Map<String, Object> result = new HashMap<>();
                        result.put("matchedPattern", pattern);  // 将匹配的模式信息放入结果
                        return result;
                    }
                });


        select.print("normal");

        select.getSideOutput(outputTag).print("timeout");

        env.execute("cep");
    }

    // 定义 Begincondition 为静态内部类
    public static class Begincondition extends IterativeCondition<Tuple3<String, Long, String>> {

        @Override
        public boolean filter(Tuple3<String, Long, String> event, Context<Tuple3<String, Long, String>> ctx) throws Exception {
            // 开始条件：状态为 "success"
            return "success".equals(event.f2);
        }
    }

    // 定义 Middlecondition 为静态内部类
    public static class Middlecondition extends IterativeCondition<Tuple3<String, Long, String>> {

        @Override
        public boolean filter(Tuple3<String, Long, String> event, Context<Tuple3<String, Long, String>> ctx) throws Exception {
            // 中间条件：状态为 "fail"
            return "fail".equals(event.f2);
        }
    }

    // 定义 Endcondition 为静态内部类
    public static class Endcondition extends IterativeCondition<Tuple3<String, Long, String>> {

        @Override
        public boolean filter(Tuple3<String, Long, String> event, Context<Tuple3<String, Long, String>> ctx) throws Exception {
            // 结束条件：状态为 "end"
            return "end".equals(event.f2);
        }
    }
}
