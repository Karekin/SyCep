import bean.LoginEvent;
import condition_ex.LogEventCondition;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.streaming.api.windowing.time.Time;
import groovy.lang.GroovyObject

def getP(){
    return Pattern<LoginEvent>.begin("begin")
        .where(new LogEventCondition("getField(eventType)==\"fail\""))
        .next("next").where(new LogEventCondition("getField(eventType)==\"fail\""))
        .times(2).within(Time.seconds(3));
}