package com.toesbieya.my.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SpringUtil implements ApplicationContextAware {
    private static final ExpressionParser parser = new SpelExpressionParser();
    private static ApplicationContext applicationContext = null;

    /**
     * 获取applicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    /**
     * 通过name获取 Bean
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

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
