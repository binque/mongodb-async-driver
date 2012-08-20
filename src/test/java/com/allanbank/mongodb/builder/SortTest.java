/*
 * Copyright 2012, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

package com.allanbank.mongodb.builder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.allanbank.mongodb.bson.element.IntegerElement;

/**
 * SortTest provides tests for the Sort helper class.
 * 
 * @copyright 2012, Allanbank Consulting, Inc., All Rights Reserved
 */
public class SortTest {

    /**
     * Test method for {@link Sort#asc(String)}.
     */
    @Test
    public void testAsc() {
        assertEquals(new IntegerElement("f", 1), Sort.asc("f"));
    }

    /**
     * Test method for {@link Sort#desc(String)}.
     */
    @Test
    public void testDesc() {
        assertEquals(new IntegerElement("g", -1), Sort.desc("g"));
    }
}