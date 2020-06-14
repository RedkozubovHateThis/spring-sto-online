package io.swagger.postgres.resourceProcessor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.swagger.postgres.model.BaseEntity;
import org.hibernate.collection.internal.PersistentSet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class SimpleResourceProcessor<E extends BaseEntity> {

    public Map<String, Object> toResource(E entity, List<String> includes) throws Exception {
        if ( !entity.getClass().isAnnotationPresent(JsonApiResource.class) )
            throw new Exception("Not the entity resource class!");

        Map<String, Object> resource = new HashMap<>();
        List<Map<String, Object>> included = new ArrayList<>();
        List<Method> methods = collectAllMethods(entity);
        resource.put( "data", buildAttributes(entity, methods, includes, included, false) );
        if ( included.size() > 0 )
            resource.put( "included", included );

        return resource;
    }

//    public Map<String, Object> toResourceList(List<E> entity, Integer totalElements) throws Exception {
//        Map<String, Object> resourceList = new HashMap<>();
//        if ( !entity.getClass().isAnnotationPresent(JsonApiResource.class) )
//            throw new Exception("Not the entity resource class!");
//
//        Map<String, Object> resource = new HashMap<>();
//        List<Method> methods = collectAllMethods(entity);
//
//        return resourceList;
//    }

    private Map<String, Object> buildAttributes(Object entity, List<Method> methods, List<String> includes, List<Map<String, Object>> included, Boolean onlyDataAndType) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> attributes = new HashMap<>();
        Map<String, Object> relationships = new HashMap<>();

        JsonApiResource jsonApiResource;
        String type;
        if ( entity.getClass().isAnnotationPresent(JsonApiResource.class) ) {
            jsonApiResource =entity.getClass().getAnnotation(JsonApiResource.class);

            if ( jsonApiResource.type().length() == 0 )
                throw new Exception("Entity type not specified!");

            type = jsonApiResource.type();
            data.put( "type", type );
        }
        else
            throw new Exception("Not the entity resource class!");

        Field idField = findIdField( entity.getClass() );
        Object id;
        if ( idField == null )
            throw new Exception("ID field is not found!");

        try {
            idField.setAccessible(true);
            id = idField.get(entity);
            idField.setAccessible(false);
            data.put( "id", id.toString() );

            if ( !onlyDataAndType ) {
                Map<String, String> links = new HashMap<>();
                if ( jsonApiResource.resourcePath().length() > 0 )
                    links.put("self", String.format("%s/%s/%s", getDomainName(), jsonApiResource.resourcePath(), id));
                else
                    links.put("self", String.format("%s/%s/%s", getDomainName(), jsonApiResource.type(), id));
                data.put("links", links);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new Exception("ID field is not found!");
        }

        if ( onlyDataAndType ) {
            return data;
        }

        for (Method method : methods) {

            String fieldName = prepareFieldName( method );
            Field field = findField( entity.getClass(), fieldName );

            if ( field == null ) {
                // Если поле не найдено, то считаем это геттером для json
                try {
                    attributes.put( fieldName, method.invoke(entity) );
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            else {
                // Если поле найдено, то проверяем, разрешено ли его экспортировать
                if ( field.isAnnotationPresent(JsonIgnore.class) ) continue;
                if ( field.isAnnotationPresent(JsonApiRelation.class) ) {
                    JsonApiRelation jsonApiRelation = field.getAnnotation(JsonApiRelation.class);
                    buildRelation(entity, jsonApiResource.resourcePath().length() > 0 ? jsonApiResource.resourcePath() : type, id, relationships, method, jsonApiRelation.serialize().equals(SerializeType.EAGER), fieldName, includes, included);
                    continue;
                }
                if ( field.isAnnotationPresent(JsonApiId.class) ) continue;

                try {
                    attributes.put( fieldName, method.invoke(entity) );
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        }

        data.put("attributes", attributes);
        if ( relationships.size() > 0 )
            data.put("relationships", relationships);
        return data;
    }

    private void buildRelation(Object entity, String type, Object id, Map<String, Object> relationships, Method method,
                               Boolean isEager, String fieldName, List<String> includes, List<Map<String, Object>> included) throws Exception {
        Map<String, Object> relationData = new HashMap<>();
        Map<String, Object> links = new HashMap<>();

        links.put("self", String.format("%s/%s/%s/relationships/%s", getDomainName(), type, id, fieldName));
        links.put("related", String.format("%s/%s/%s/%s", getDomainName(), type, id, fieldName));
        relationData.put("links", links);

        if ( includes.contains(fieldName) || isEager ) {
            Object fieldValue = method.invoke(entity);

            if (fieldValue.getClass().equals(PersistentSet.class)) {
                PersistentSet set = (PersistentSet) fieldValue;

                List<Map<String, Object>> data = new ArrayList<>();
                List<Map<String, Object>> includedData = new ArrayList<>();
                for (Object relation : set) {
                    data.add( buildAttributes(relation, null, null, null, true) );
                    includedData.add( buildAttributes(relation, collectAllMethods(relation), new ArrayList<>(), included, false) );
                }

                if ( data.size() > 0 )
                    relationData.put("data", data);
                if ( includedData.size() > 0 )
                    included.addAll(includedData);

            }
            else {
                Map<String, Object> data = buildAttributes(fieldValue, null, null, null, true);
                Map<String, Object> includedData = buildAttributes(fieldValue, collectAllMethods(fieldValue), new ArrayList<>(), included, false);

                relationData.put("data", data);
                included.add(includedData);
            }
        }

        relationships.put(fieldName, relationData);
    }

    private List<Method> collectAllMethods(Object entity) {
        List<Method> methods = new ArrayList<>();

        collectMethods(entity.getClass(), methods);
        return methods;
    }

    private void collectMethods(Class<?> clazz, List<Method> methods) {
        if ( clazz == null ) return;

        for ( Method declaredMethod : clazz.getDeclaredMethods() ) {
            if ( !declaredMethod.isAnnotationPresent(JsonIgnore.class) && ( declaredMethod.getName().startsWith("get") || declaredMethod.getName().startsWith("is") ) )
                methods.add( declaredMethod );
        }

        if ( clazz.equals(BaseEntity.class) ) return;
        collectMethods( clazz.getSuperclass(), methods );
    }

    private Field findField(Class<?> clazz, String fieldName) {

        if ( clazz == null ) return null;

        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if ( clazz.equals(BaseEntity.class) ) return null;
            return findField(clazz.getSuperclass(), fieldName);
        }

    }

    private Field findIdField(Class<?> clazz) {

        if ( clazz == null ) return null;

        Field[] fields = clazz.getDeclaredFields();
        Field idField = null;
        for (Field field : fields) {
            if ( field.isAnnotationPresent(JsonApiId.class) ) {
                idField = field;
            }
        }

        if ( idField == null ) {
            if ( clazz.equals(BaseEntity.class) )
                return null;
            return findIdField( clazz.getSuperclass() );
        }

        return idField;
    }

    private String prepareFieldName(Method method) {
        String baseFieldName = method.getName();

        if ( baseFieldName.startsWith("get") )
            baseFieldName = baseFieldName.replaceFirst("get", "");
        else
            baseFieldName = baseFieldName.replaceFirst("is", "");

        return baseFieldName.replaceFirst( String.valueOf( baseFieldName.charAt(0) ), String.valueOf( baseFieldName.charAt(0) ).toLowerCase() );
    }

    public abstract String getDomainName();
    public abstract void customAttributes(Map<String, Object> attributes, E entity);

}
