package com.cki.base.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import com.cki.base.spring.Properties;

    
  
  
  
  
  
public class AnnotationBeanPostProcessor extends PropertyPlaceholderConfigurer implements BeanPostProcessor, InitializingBean {

    private java.util.Properties pros;

    @SuppressWarnings("unchecked")
    private Class[] enableClassList = {String.class, Integer.class};

    @SuppressWarnings("unchecked")
    public void setEnableClassList(Class[] enableClassList) {
        this.enableClassList = enableClassList;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {

        Field[] fields = bean.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Properties.class)) {
                if (filterType(field.getType().toString())) {
                    Properties p = field.getAnnotation(Properties.class);
                    ReflectionUtils.makeAccessible(field);
                    try {
                        if (isInteger(field)) {
                            field.set(bean, Integer.valueOf(pros.getProperty(p.value())));
                        } else if (isString(field)) {
                            field.set(bean, pros.getProperty(p.value()));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Annotation set field error", e);
                    }
                }
            }
        }
        return bean;
    }

    private boolean isString(Field field) {
        return field.getType().toString().equals(String.class.toString());
    }

    private boolean isInteger(Field field) {
        return field.getType().toString().equals(Integer.class.toString());
    }

    @SuppressWarnings("unchecked")
    private boolean filterType(String type) {
        if (type != null) {
            for (Class c : enableClassList) {
                if (c.toString().equals(type)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    public void afterPropertiesSet() throws Exception {
        pros = mergeProperties();
    }
}