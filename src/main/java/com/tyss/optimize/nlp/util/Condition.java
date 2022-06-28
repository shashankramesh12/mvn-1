package com.tyss.optimize.nlp.util;

import bsh.EvalError;
import bsh.Interpreter;

import java.math.BigDecimal;
import java.util.Objects;

public class Condition {
    public Boolean condition(String value1, String operator, String value2) throws EvalError {
        Boolean status = false;
        Interpreter interpreter = new Interpreter();
        BigDecimal val1=null;
        BigDecimal val2=null;
        try {
            val1 = new BigDecimal(value1);
        }catch (Exception e) {
        }
        try {
            val2 = new BigDecimal(value2);

        } catch (Exception e){

        }
        if(Objects.nonNull(val1)&&Objects.nonNull(val2))
        {
            interpreter.eval("condition =" + val1+operator+"("+val2+")");
        }
        else if(Objects.nonNull(val1) && Objects.isNull(val2))
        {
            interpreter.eval("condition =" + val1+operator+"("+"\""+value2+"\""+")");
        }
        else if(Objects.nonNull(val2) && Objects.isNull(val1))
        {
            interpreter.eval("condition =" + "\""+value1+"\""+operator+"("+val2+")");
        }
        else {
            interpreter.eval("condition =" + "\"" + value1 + "\"" + operator + "(" + "\"" + value2 + "\"" + ")");
        }
        status = (Boolean) interpreter.get("condition");
        return status;
    }

}
