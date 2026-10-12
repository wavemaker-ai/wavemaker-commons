/*******************************************************************************
 * Copyright (C) 2022-2023 WaveMaker, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wavemaker.commons.json.module;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.PropertyWriter;
import tools.jackson.databind.ser.bean.BeanSerializerBase;
import tools.jackson.databind.util.NameTransformer;

/**
 * @author <a href="mailto:dilip.gundu@wavemaker.com">Dilip Kumar</a>
 * @since 3/10/18
 */
public class CircularLoopHandlingSerializer<T> extends ValueSerializer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CircularLoopHandlingSerializer.class);
    /**
     * Stack to store object call stack and to determine cycling dependency during serialization.
     */
    private static final ThreadLocal<Deque<Object>> objectReferenceStackTL = new ThreadLocal<>();

    private final BeanSerializerBase delegate;
    private final boolean failOnCircularReferences;

    public CircularLoopHandlingSerializer(final BeanSerializerBase delegate, final boolean failOnCircularReferences) {
        this.delegate = delegate;
        this.failOnCircularReferences = failOnCircularReferences;
    }

    @Override
    public void serialize(
        final T value, final JsonGenerator gen, final SerializationContext serializers) {
        LOGGER.debug("Serialization started for :{}", value.getClass());

        if (hasCyclicReference(value) && !delegate.usesObjectId()) {
            handleCyclicReference(gen, serializers);
            return;
        }

        notifyStartSerialization(value);
        try {
            delegate.serialize(value, gen, serializers);
        } finally {
            LOGGER.debug("Serialization completed for:{}", value.getClass());
            notifyEndSerialization();
        }
    }

    @Override
    public ValueSerializer<?> createContextual(
        final SerializationContext prov, final BeanProperty property) {
        final ValueSerializer<?> contextual = delegate.createContextual(prov, property);
        return wrapIfNeeded(contextual);
    }

    @Override
    public void resolve(final SerializationContext provider) {
        delegate.resolve(provider);
    }

    @Override
    public ValueSerializer<T> unwrappingSerializer(final NameTransformer unwrapper) {
        return (ValueSerializer<T>) wrapIfNeeded(delegate.unwrappingSerializer(unwrapper));
    }

    @Override
    public ValueSerializer<T> replaceDelegatee(final ValueSerializer<?> delegatee) {
        return (ValueSerializer<T>) wrapIfNeeded(delegate.replaceDelegatee(delegatee));
    }

    @Override
    public ValueSerializer<?> withFilterId(final Object filterId) {
        return wrapIfNeeded(delegate.withFilterId(filterId));
    }

    @Override
    public void serializeWithType(
        final T value, final JsonGenerator gen, final SerializationContext serializers,
        final TypeSerializer typeSer) {
        delegate.serializeWithType(value, gen, serializers, typeSer);
    }

    @Override
    public Class<T> handledType() {
        return (Class<T>) delegate.handledType();
    }

    @Override
    public boolean isEmpty(final SerializationContext provider, final T value) {
        return delegate.isEmpty(provider, value);
    }

    @Override
    public boolean usesObjectId() {
        return delegate.usesObjectId();
    }

    @Override
    public boolean isUnwrappingSerializer() {
        return delegate.isUnwrappingSerializer();
    }

    @Override
    public ValueSerializer<?> getDelegatee() {
        return delegate.getDelegatee();
    }

    @Override
    public Iterator<PropertyWriter> properties() {
        return delegate.properties();
    }

    @Override
    public void acceptJsonFormatVisitor(
        final JsonFormatVisitorWrapper visitor, final JavaType type) {
        delegate.acceptJsonFormatVisitor(visitor, type);
    }

    public void notifyStartSerialization(Object value) {
        getObjectRefStack().push(value);
    }

    /**
     * Removes the last added object from the stack.
     */
    public void notifyEndSerialization() {
        getObjectRefStack().pop();
        if (getObjectRefStack().isEmpty()) {
            objectReferenceStackTL.remove(); // gc
        }
    }

    /**
     * Check for the cycle in object serialization stack. It ignores Self references as they are handled separately.
     *
     * @param value to be check for cycle.
     *
     * @return true when value in serialization reference stack and not as top value else false
     */
    public boolean hasCyclicReference(Object value) {
        // ignores self references,
        return (!getObjectRefStack().isEmpty() && !getObjectRefStack().getFirst().equals(value)) &&
            getObjectRefStack().contains(value);
    }

    public void handleCyclicReference(final JsonGenerator jgen, SerializationContext provider) {
        String refStack = printableRefStack();
        if (failOnCircularReferences) {
            throw DatabindException.from(jgen,
                "Cyclic-reference leading to cycle, Object Reference Stack:" + refStack).clearLocation();
        }
        // else serializing as NULL.
        provider.defaultSerializeNullValue(jgen);
    }

    private String printableRefStack() {
        StringBuilder sb = new StringBuilder();
        Object[] objects = getObjectRefStack().toArray();
        for (int i = objects.length - 1; i >= 0; i--) {
            sb.append(objects[i].getClass().getSimpleName());
            if (i > 0) {
                sb.append("->");
            }
        }
        return sb.toString();
    }

    private Deque<Object> getObjectRefStack() {
        if (objectReferenceStackTL.get() == null) {
            objectReferenceStackTL.set(new ArrayDeque<>());
        }
        return objectReferenceStackTL.get();
    }

    private ValueSerializer wrapIfNeeded(ValueSerializer serializer) {
        return serializer instanceof BeanSerializerBase ?
            new CircularLoopHandlingSerializer<>((BeanSerializerBase) serializer, failOnCircularReferences)
            : serializer;
    }
}
