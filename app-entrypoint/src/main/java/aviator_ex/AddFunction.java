package aviator_ex;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

public class AddFunction extends AbstractFunction {

    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
//        if (args.length != 2) {
//            throw new IllegalArgumentException("Add only supports two arguments");
//        }
        Number left = FunctionUtils.getNumberValue(arg1, env);
        Number right = FunctionUtils.getNumberValue(arg2, env);
        return new AviatorDouble(left.doubleValue() + right.doubleValue());
    }


    public String getName() {
        return "add";
    }

    public static void main(String[] args) {
        //注册函数
        AviatorEvaluator.addFunction(new AddFunction());
        System.out.println(AviatorEvaluator.execute("add(1,2)"));
        System.out.println(AviatorEvaluator.execute("add(add(1,2),100)"));
    }
}