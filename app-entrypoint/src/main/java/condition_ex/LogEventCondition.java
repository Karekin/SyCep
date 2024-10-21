package condition_ex;

import bean.LoginEvent;
import com.googlecode.aviator.AviatorEvaluator;
import aviator_ex.GetJsonStringFunction;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import util.Obj2Map;

import java.io.Serializable;
import java.util.Map;

public class LogEventCondition extends SimpleCondition<LoginEvent> implements Serializable {
    private String script;

    static {
        AviatorEvaluator.addFunction(new GetJsonStringFunction());
    }

    //getField(eventType)==\"fail\"
    public LogEventCondition(String script) {
        this.script = script;
    }

    @Override
    public boolean filter(LoginEvent value) throws Exception {
        Map<String, Object> stringObjectMap = Obj2Map.objectToMap(value);
        //计算表达式的值
        boolean result = (Boolean) AviatorEvaluator.execute(script, stringObjectMap);
        return result;
    }

}