package com.ctp.cdi.query.util;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.jboss.logging.Logger;

import com.ctp.cdi.query.Dao;
import com.ctp.cdi.query.meta.DaoEntity;

public abstract class DaoUtils {
    
    private static final Logger log = Logger.getLogger(DaoUtils.class);
    
    /**
     * Extract the generic type information from the DAO class or interface definition.
     * @param daoClass          DAO class to analyze.
     * @return                  Meta data containing entity and primary key classes.
     */
    public static DaoEntity extractEntityMetaData(Class<?> daoClass) {
        log.debugv("extractEntityMetaData: class = {0}", daoClass);
        DaoEntity fromAnnotation = extractFromAnnotation(daoClass);
        if (fromAnnotation != null)
            return fromAnnotation;
        if (daoClass.isInterface()) {
            for (Type inf : daoClass.getGenericInterfaces()) {
                DaoEntity result = extractFrom(inf);
                if (result != null)
                    return result;
            }
        } else {
            DaoEntity result = extractFrom(daoClass.getGenericSuperclass());
            if (result != null)
                return result;
        }
        if (daoClass.getSuperclass() != null)
            return extractEntityMetaData(daoClass.getSuperclass());
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private static DaoEntity extractFrom(Type type) {
        log.debugv("extractFrom: type = {0}", type);
        if (type  instanceof ParameterizedType) {
            Type[] genericTypes = ((ParameterizedType) type).getActualTypeArguments();
            DaoEntity result = null;
            for (Type genericType : genericTypes) {
                if (genericType instanceof Class && EntityUtils.isEntityClass((Class<?>) genericType)) {
                    result = new DaoEntity((Class<?>) genericType);
                    continue;
                }
                if (result != null && genericType instanceof Class) {
                    result.setPrimaryClass((Class<? extends Serializable>) genericType);
                    return result;
                }
            }
        }
        return null;
    }
    
    private static DaoEntity extractFromAnnotation(Class<?> daoClass) {
        if (daoClass.isAnnotationPresent(Dao.class)) {
            Dao dao = daoClass.getAnnotation(Dao.class);
            if (!Object.class.equals(dao.value())) {
                return new DaoEntity(dao.value(), EntityUtils.primaryKeyClass(dao.value()));
            }
        }
        return null;
    }

}
