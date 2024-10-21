package demo_by_2;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.shaded.curator5.com.google.common.base.Joiner;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author StephenYou
 * Created on 2023-07-29
 * Description: run for show source code
 */
public class DemoLaunch {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        env.setParallelism(1);


        KeyedStream<MetricEvent, String> source = env.fromElements(
                new MetricEvent("a", 1656914307000L, new HashMap<String, Object>(), new HashMap<String, String>())
        )
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<MetricEvent>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                                .withTimestampAssigner((event, timestamp) -> {
                                    return event.getTimestamp();
                                }))
                .returns(MetricEvent.class)
                .keyBy(new KeySelector<MetricEvent, String>() {
                    @Override
                    public String getKey(MetricEvent value) throws Exception {
                        return value.getName();
                    }
                });


        Pattern p1 = ScriptEngine.getPattern(

                "  import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy\n" +
                        "import org.apache.flink.cep.pattern.Pattern\n" +
                        "import demo2.AviatorCondition \n" +

                        "where1 = new AviatorCondition(" +
                        "   \"getT(tags,\\\"cluster_name\\\")==\\\"x\\\"&&getF(fields,\\\"load5\\\")>15 \"" +
                        "        )\n" +

                        "def get(){ " +
                        "      return Pattern.begin(\"start\", AfterMatchSkipStrategy.noSkip())\n" +
                        "        .where(where1)" +
                        "}",
                "get");

        SingleOutputStreamOperator<MetricEvent> metricEvent = source
                .flatMap(new ParseMetricEventFunction()).returns(MetricEvent.class);
        PatternStream pStream2 = CEP.pattern(metricEvent.keyBy(metricEvent1 -> metricEvent1.getName() + Joiner.on(",").join(metricEvent1.getTags().values())), p1);
        SingleOutputStreamOperator filter2 = pStream2.select(new PatternSelectFunction<MetricEvent, String>() {
            @Override
            public String select(Map<String, List<MetricEvent>> pattern) throws Exception {
                return "-----------------------------" + pattern.toString();
            }
        });

        filter2.print();

        env.execute("----flink cep alert ----");
    }

}
