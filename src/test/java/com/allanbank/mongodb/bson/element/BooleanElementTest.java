/*
 * #%L
 * BooleanElementTest.java - mongodb-async-driver - Allanbank Consulting, Inc.
 * %%
 * Copyright (C) 2011 - 2014 Allanbank Consulting, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package com.allanbank.mongodb.bson.element;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.Visitor;

/**
 * BooleanElementTest provides tests for the {@link BooleanElement} class.
 *
 * @copyright 2012-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class BooleanElementTest {

    /**
     * Test method for
     * {@link BooleanElement#accept(com.allanbank.mongodb.bson.Visitor)} .
     */
    @Test
    public void testAccept() {
        final BooleanElement element = new BooleanElement("foo", false);

        final Visitor mockVisitor = createMock(Visitor.class);

        mockVisitor.visitBoolean(eq("foo"), eq(false));
        expectLastCall();

        replay(mockVisitor);

        element.accept(mockVisitor);

        verify(mockVisitor);
    }

    /**
     * Test method for
     * {@link BooleanElement#BooleanElement(java.lang.String, boolean)} .
     */
    @Test
    public void testBooleanElement() {
        final BooleanElement element = new BooleanElement("foo", false);

        assertEquals("foo", element.getName());
        assertFalse(element.getValue());
        assertEquals(ElementType.BOOLEAN, element.getType());
    }

    /**
     * Test method for {@link BooleanElement#compareTo(Element)}.
     */
    @Test
    public void testCompareTo() {
        final BooleanElement t = new BooleanElement("a", true);
        final BooleanElement f = new BooleanElement("a", false);
        final Element other = new MaxKeyElement("a");

        assertEquals(0, t.compareTo(t));
        assertTrue(t.compareTo(f) > 0);
        assertTrue(f.compareTo(t) < 0);

        assertTrue(t.compareTo(other) < 0);
        assertTrue(other.compareTo(t) > 0);
    }

    /**
     * Test method for {@link BooleanElement#equals(java.lang.Object)} .
     */
    @Test
    public void testEqualsObject() {

        final List<Element> objs1 = new ArrayList<Element>();
        final List<Element> objs2 = new ArrayList<Element>();

        for (final String name : Arrays.asList("1", "foo", "bar", "baz", "2")) {
            objs1.add(new BooleanElement(name, false));
            objs2.add(new BooleanElement(name, false));
            objs1.add(new BooleanElement(name, true));
            objs2.add(new BooleanElement(name, true));
        }

        // Sanity check.
        assertEquals(objs1.size(), objs2.size());

        for (int i = 0; i < objs1.size(); ++i) {
            final Element obj1 = objs1.get(i);
            Element obj2 = objs2.get(i);

            assertTrue(obj1.equals(obj1));
            assertNotSame(obj1, obj2);
            assertEquals(obj1, obj2);

            assertEquals(obj1.hashCode(), obj2.hashCode());

            for (int j = i + 1; j < objs1.size(); ++j) {
                obj2 = objs2.get(j);

                assertFalse(obj1.equals(obj2));
                assertFalse(obj1.hashCode() == obj2.hashCode());
            }

            assertFalse(obj1.equals("foo"));
            assertFalse(obj1.equals(null));
            assertFalse(obj1.equals(new MaxKeyElement(obj1.getName())));
        }
    }

    /**
     * Test method for {@link BooleanElement#BooleanElement}.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testThrowsOnNullName() {

        new BooleanElement(null, true);
    }

    /**
     * Test method for {@link BooleanElement#toString()}.
     */
    @Test
    public void testToString() {
        BooleanElement element = new BooleanElement("foo", false);

        assertEquals("foo : false", element.toString());

        element = new BooleanElement("foo", true);

        assertEquals("foo : true", element.toString());
    }

    /**
     * Test method for {@link BooleanElement#getValueAsObject()}.
     */
    @Test
    public void testValueAsObject() {
        BooleanElement element = new BooleanElement("foo", false);

        assertEquals(Boolean.FALSE, element.getValueAsObject());

        element = new BooleanElement("foo", true);

        assertEquals(Boolean.TRUE, element.getValueAsObject());
    }

    /**
     * Test method for {@link BooleanElement#getValueAsString()}.
     */
    @Test
    public void testValueAsString() {
        BooleanElement element = new BooleanElement("foo", false);

        assertEquals("false", element.getValueAsString());

        element = new BooleanElement("foo", true);

        assertEquals("true", element.getValueAsString());
    }

    /**
     * Test method for {@link BooleanElement#withName(String)}.
     */
    @Test
    public void testWithName() {
        BooleanElement element = new BooleanElement("foo", false);
        element = element.withName("bar");
        assertEquals("bar", element.getName());
        assertFalse(element.getValue());
        assertEquals(ElementType.BOOLEAN, element.getType());
    }

    /**
     * Test method for {@link BooleanElement#withName(String)}.
     */
    @Test
    public void testWithNameWhenSameName() {
        final BooleanElement element = new BooleanElement("foo", false);

        assertSame(element, element.withName("foo"));
    }
}
