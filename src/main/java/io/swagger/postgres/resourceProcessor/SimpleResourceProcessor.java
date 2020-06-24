package io.swagger.postgres.resourceProcessor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.swagger.postgres.model.BaseEntity;
import org.hibernate.collection.internal.PersistentSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class SimpleResourceProcessor<E extends BaseEntity> {

    public JsonApiEntity toResource(E entity, List<String> includes) throws Exception {
        if ( !entity.getClass().isAnnotationPresent(JsonApiResource.class) )
            throw new Exception("Not the entity resource class!");

        JsonApiEntity resource = new JsonApiEntity();
        List<Method> methods = collectAllMethods(entity);
        resource.setData( buildAttributes(entity, methods, includes, resource.getIncluded(), false) );

        return resource;
    }

    public JsonApiEntityList toResourceList(List<E> entities, List<String> includes, Long totalElements, Pageable pageable) throws Exception {
        JsonApiEntityList resourceList = new JsonApiEntityList();
        resourceList.getMeta().setTotalResources(totalElements);

        if ( entities == null || entities.size() == 0 )
            return resourceList;

        for ( E entity : entities ) {
            if ( !entity.getClass().isAnnotationPresent(JsonApiResource.class) )
                throw new Exception("Not the entity resource class!");

            List<Method> methods = collectAllMethods(entity);
            resourceList.addData( buildAttributes(entity, methods, includes, resourceList.getIncluded(), false) );
        }

        return resourceList;
    }

    public JsonApiEntityList toResourcePage(Page<E> entities, List<String> includes, Long totalElements, Pageable pageable) throws Exception {
        return toResourceList(entities.getContent(), includes, totalElements, pageable);
    }

    private JsonApiData buildAttributes(Object entity, List<Method> methods, List<String> includes, List<JsonApiData> apiEntity, Boolean onlyDataAndType) throws Exception {
        JsonApiData data = new JsonApiData();

        JsonApiResource jsonApiResource;
        String type;
        if ( entity.getClass().isAnnotationPresent(JsonApiResource.class) ) {
            jsonApiResource =entity.getClass().getAnnotation(JsonApiResource.class);

            if ( jsonApiResource.type().length() == 0 )
                throw new Exception("Entity type not specified!");

            type = jsonApiResource.type();
            data.setType( type );
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
            data.setId( id.toString() );

            if ( !onlyDataAndType ) {
                if ( jsonApiResource.resourcePath().length() > 0 )
                    data.putLink("self", String.format("%s/%s/%s", getDomainName(), jsonApiResource.resourcePath(), id));
                else
                    data.putLink("self", String.format("%s/%s/%s", getDomainName(), jsonApiResource.type(), id));
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
                    data.putAttribute( fieldName, method.invoke(entity) );
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            else {
                // Если поле найдено, то проверяем, разрешено ли его экспортировать
                if ( field.isAnnotationPresent(JsonIgnore.class) ) continue;
                if ( field.isAnnotationPresent(JsonApiRelation.class) ) {
                    JsonApiRelation jsonApiRelation = field.getAnnotation(JsonApiRelation.class);
                    buildRelation(entity, jsonApiResource.resourcePath().length() > 0 ? jsonApiResource.resourcePath() : type, id, data.getRelationships(), method, jsonApiRelation.serialize().equals(SerializeType.EAGER), fieldName, includes, apiEntity);
                    continue;
                }
                if ( field.isAnnotationPresent(JsonApiId.class) ) continue;

                try {
                    data.putAttribute( fieldName, method.invoke(entity) );
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        }

        return data;
    }

    private void buildRelation(Object entity, String type, Object id, Map<String, Object> relationships, Method method,
                               Boolean isEager, String fieldName, List<String> includes, List<JsonApiData> apiEntity) throws Exception {
        JsonApiRelationEntity relationData = new JsonApiRelationEntity();

        relationData.putLink("self", String.format("%s/%s/%s/relationships/%s", getDomainName(), type, id, fieldName));
        relationData.putLink("related", String.format("%s/%s/%s/%s", getDomainName(), type, id, fieldName));

        if ( ( includes != null && includes.contains(fieldName) ) || isEager ) {
            Object fieldValue = method.invoke(entity);

            if ( fieldValue != null ) {
                if (fieldValue.getClass().equals(PersistentSet.class)) {
                    PersistentSet set = (PersistentSet) fieldValue;

                    List<JsonApiData> data = new ArrayList<>();
                    for (Object relation : set) {
                        data.add( buildAttributes(relation, null, null, null, true) );
                        addIncluded( apiEntity, buildAttributes(relation, collectAllMethods(relation), new ArrayList<>(), apiEntity, false) );
                    }

                    if ( data.size() > 0 )
                        relationData.setData(data);

                }
                else {
                    relationData.setData( buildAttributes(fieldValue, null, null, null, true) );
                    addIncluded( apiEntity, buildAttributes(fieldValue, collectAllMethods(fieldValue), new ArrayList<>(), apiEntity, false) );
                }
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

    private void addIncluded(List<JsonApiData> included, JsonApiData includedData) {
        if ( included.size() == 0 || included.stream().noneMatch( data -> data.isSame( includedData ) ) ) {
            included.add( includedData );
        }
    }

    public void addIncludes(List<JsonApiData> included, List<JsonApiData> includedData) {
        for (JsonApiData includedDatum : includedData) {
            addIncluded( included, includedDatum );
        }
    }

    public abstract String getDomainName();
    public abstract void customAttributes(Map<String, Object> attributes, E entity);

}
