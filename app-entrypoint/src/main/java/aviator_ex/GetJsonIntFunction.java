package aviator_ex;

import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.*;

import java.util.Map;

/**
 * @program: SyCep
 * @description:
 * @author: lijinzhong
 * @create: 2024-09-24
 */
public class GetJsonIntFunction extends JsonFieldFunction {
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        AviatorJavaType field = (AviatorJavaType) arg1;
        String name = field.getName();
        System.out.println("keyName\t" + name);
        if (name.contains(".")) {
            return new AviatorBigInt(Integer.parseInt(jsonValue(name, env)));
        }

        Number numberValue = FunctionUtils.getNumberValue(arg1, env);
        return new AviatorBigInt(numberValue.intValue());
    }

    @Override
    public String getName() {
        return "getInt";
    }
}
