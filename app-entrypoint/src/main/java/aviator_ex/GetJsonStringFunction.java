package aviator_ex;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;

import java.util.HashMap;
import java.util.Map;

public class GetJsonStringFunction extends JsonFieldFunction {

    @Override
    public String getName() {
        return "getField";
    }

    @Override
    public AviatorString call(Map<String, Object> params, AviatorObject arg1) {

        AviatorJavaType field = (AviatorJavaType) arg1;
        String name = field.getName();

        if (name.contains(".")) {
            return new AviatorString(jsonValue(name, params));
        }

        String stringValue = FunctionUtils.getStringValue(arg1, params);
        return new AviatorString(stringValue);
    }

    public static void main(String[] args) {
        AviatorEvaluator.addFunction(new GetJsonIntFunction());
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("person", "{age:12,name:\"zhangsan\"}");
        System.out.println(AviatorEvaluator.execute("getInt(person.age)<12", m));
    }
}
 
 
