/*
 * #%L
 * MinKeyElementTest.java - mongodb-async-driver - Allanbank Consulting, Inc.
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
 * MinKeyElementTest provides tests for the {@link MinKeyElement} class.
 *
 * @copyright 2012-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class MinKeyElementTest {

    /**
     * Test method for
     * {@link MinKeyElement#accept(com.allanbank.mongodb.bson.Visitor)} .
     */
    @Test
    public void testAccept() {
        final MinKeyElement element = new MinKeyElement("foo");

        final Visitor mockVisitor = createMock(Visitor.class);

        mockVisitor.visitMinKey(eq("foo"));
        expectLastCall();

        replay(mockVisitor);

        element.accept(mockVisitor);

        verify(mockVisitor);
    }

    /**
     * Test method for {@link MinKeyElement#compareTo(Element)}.
     */
    @Test
    public void testCompareTo() {
        final MinKeyElement a = new MinKeyElement("a");
        final MinKeyElement b = new MinKeyElement("b");
        final Element other = new MaxKeyElement("a");

        assertEquals(0, a.compareTo(a));

        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);

        assertTrue(a.compareTo(other) < 0);
        assertTrue(other.compareTo(a) > 0);
    }

    /**
     * Test method for {@link MinKeyElement#equals(java.lang.Object)} .
     */
    @Test
    public void testEqualsObject() {

        final List<Element> objs1 = new ArrayList<Element>();
        final List<Element> objs2 = new ArrayList<Element>();

        for (final String name : Arrays.asList("1", "foo", "bar", "baz", "2")) {
            objs1.add(new MinKeyElement(name));
            objs2.add(new MinKeyElement(name));
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
     * Test method for {@link MinKeyElement#MinKeyElement(String)} .
     */
    @Test
    public void testMinKeyElement() {
        final MinKeyElement element = new MinKeyElement("foo");

        assertEquals("foo", element.getName());
        assertEquals(ElementType.MIN_KEY, element.getType());
    }

    /**
     * Test method for {@link MinKeyElement#MinKeyElement}.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testThrowsOnNullName() {

        new MinKeyElement(null);
    }

    /**
     * Test method for {@link MinKeyElement#toString()}.
     */
    @Test
    public void testToString() {
        final MinKeyElement element = new MinKeyElement("foo");

        assertEquals("foo : MinKey()", element.toString());
    }

    /**
     * Test method for {@link MinKeyElement#getValueAsObject()}.
     */
    @Test
    public void testValueAsObject() {
        final MinKeyElement element = new MinKeyElement("foo");

        assertEquals(Double.valueOf(Double.NEGATIVE_INFINITY),
                element.getValueAsObject());
    }

    /**
     * Test method for {@link MinKeyElement#getValueAsString()}.
     */
    @Test
    public void testValueAsString() {
        final MinKeyElement element = new MinKeyElement("foo");

        assertEquals("MinKey()", element.getValueAsString());
    }

    /**
     * Test method for {@link MinKeyElement#withName(String)}.
     */
    @Test
    public void testWithName() {
        MinKeyElement element = new MinKeyElement("foo");

        element = element.withName("bar");
        assertEquals("bar", element.getName());
        assertEquals(ElementType.MIN_KEY, element.getType());
    }

    /**
     * Test method for {@link MinKeyElement#withName(String)}.
     */
    @Test
    public void testWithNameWhenSameName() {
        final MinKeyElement element = new MinKeyElement("foo");

        assertSame(element, element.withName("foo"));
    }
}
