package demo_by_2;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

/**
 * @program: SyCep
 * @description:
 * @author: lijinzhong
 * @create: 2024-09-25
 */
public class ParseMetricEventFunction implements FlatMapFunction<MetricEvent, MetricEvent> {
    @Override
    public void flatMap(MetricEvent value, Collector<MetricEvent> out) throws Exception {
        out.collect(value);

    }
}
