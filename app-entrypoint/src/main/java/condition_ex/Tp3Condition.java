package condition_ex;

import aviator_ex.GetJsonStringFunction;
import com.googlecode.aviator.AviatorEvaluator;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import util.Obj2Map;

import java.io.Serializable;
import java.util.Map;

public class Tp3Condition extends SimpleCondition<Tuple3<String, Long, String>> implements Serializable {
    private String script;

    static {
        AviatorEvaluator.addFunction(new GetJsonStringFunction());
    }

    //getField(eventType)==\"fail\"
    public Tp3Condition(String script) {
        this.script = script;
    }

    @Override
    public boolean filter(Tuple3<String, Long, String> value) throws Exception {
        Map<String, Object> stringObjectMap = Obj2Map.objectToMap(value);
        //计算表达式的值
        boolean result = (Boolean) AviatorEvaluator.execute(script, stringObjectMap);
        return result;
    }

}