package cn.toesbieya.jxc.common.util;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

public class SpringUtil {
    private static final ExpressionParser parser = new SpelExpressionParser();

    /**
     * el表达式解析
     *
     * @param el    表达式
     * @param names 参数名称数组
     * @param args  参数数组
     */
    public static Object spell(String el, String[] names, Object[] args) {
        if (StringUtils.isEmpty(el)) {
            return null;
        }
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(names[i], args[i]);
        }
        return parser.parseExpression(el).getValue(context);
    }
}
