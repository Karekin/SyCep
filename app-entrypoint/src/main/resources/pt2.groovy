import bean.LoginEvent;
import condition_ex.Tp3Condition;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.streaming.api.windowing.time.Time;
import groovy.lang.GroovyObject
def getP(){
    return Pattern<Tuple3<String, Long, String>>.begin("begin")
        .where(new Tp3Condition("getField(f2)==\"success\""))
             .followedByAny("middle")
                        .where(new Tp3Condition("getField(f2)==\"fail\""))
                        .times(5)
                        .followedBy("end")
                        .where(new Tp3Condition("getField(f2)==\"end\""));

}