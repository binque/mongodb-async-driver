/*
 * #%L
 * ServerNameUtilsTest.java - mongodb-async-driver - Allanbank Consulting, Inc.
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

package com.allanbank.mongodb.util;

import static org.junit.Assert.assertEquals;

import java.net.InetSocketAddress;

import org.junit.Test;

/**
 * ServerNameUtilsTest provides tests of the {@link ServerNameUtils} class.
 *
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class ServerNameUtilsTest {

    /**
     * Test method for {@link ServerNameUtils#normalize(InetSocketAddress)}.
     */
    @Test
    public void testNormalizeInetSocketAddress() {
        assertEquals("foo:27017",
                ServerNameUtils.normalize(ServerNameUtils.parse("foo")));
        assertEquals("foo:27018",
                ServerNameUtils.normalize(ServerNameUtils.parse("foo:27018")));

        assertEquals("foo:abot:27017",
                ServerNameUtils.normalize(ServerNameUtils.parse("foo:abot")));

        assertEquals("192.168.0.1:27017",
                ServerNameUtils.normalize(ServerNameUtils.parse("192.168.0.1")));
        assertEquals("192.168.0.1:27018",
                ServerNameUtils.normalize(ServerNameUtils
                        .parse("192.168.0.1:27018")));

        assertEquals("[fe80:0:0:0:868f:69ff:feb2:95d4]:27018",
                ServerNameUtils.normalize(new InetSocketAddress(
                        "fe80::868f:69ff:feb2:95d4", 27018)));
        assertEquals("[fe80:0:0:0:868f:69ff:feb2:95d4]:27018",
                ServerNameUtils.normalize(new InetSocketAddress(
                        "[fe80::868f:69ff:feb2:95d4]", 27018)));
    }

    /**
     * Test method for {@link ServerNameUtils#normalize(String)}.
     */
    @Test
    public void testNormalizeString() {
        assertEquals("foo:27017", ServerNameUtils.normalize("foo"));
        assertEquals("foo:27018", ServerNameUtils.normalize("foo:27018"));

        assertEquals("foo:abot:27017", ServerNameUtils.normalize("foo:abot"));

        assertEquals("192.168.0.1:27017",
                ServerNameUtils.normalize("192.168.0.1"));
        assertEquals("192.168.0.1:27018",
                ServerNameUtils.normalize("192.168.0.1:27018"));

        assertEquals("[fe80::868f:69ff:feb2:95d4]:27017",
                ServerNameUtils.normalize("fe80::868f:69ff:feb2:95d4"));
        assertEquals("[fe80::868f:69ff:feb2:95d4]:27018",
                ServerNameUtils.normalize("fe80::868f:69ff:feb2:95d4:27018"));
        assertEquals("[fe80::868f:69ff:feb2:95d4]:27018",
                ServerNameUtils.normalize("[fe80::868f:69ff:feb2:95d4]:27018"));
        assertEquals("[fe80::868f:69ff:feb2:95d4]:27017",
                ServerNameUtils.normalize("[fe80::868f:69ff:feb2:95d4]"));
    }

    /**
     * Test method for {@link ServerNameUtils#parse(String)}.
     */
    @Test
    public void testParse() {
        assertEquals(new InetSocketAddress("foo", 27017),
                ServerNameUtils.parse("foo"));
        assertEquals(new InetSocketAddress("foo", 27018),
                ServerNameUtils.parse("foo:27018"));
        assertEquals(new InetSocketAddress("fe80::868f:69ff:feb2:95d4", 27018),
                ServerNameUtils.parse("fe80::868f:69ff:feb2:95d4:27018"));
        assertEquals(new InetSocketAddress("fe80::868f:69ff:feb2:95d4", 27018),
                ServerNameUtils.parse("[fe80::868f:69ff:feb2:95d4]:27018"));
        assertEquals(new InetSocketAddress("fe80::868f:69ff:feb2:95d4", 27017),
                ServerNameUtils.parse("[fe80::868f:69ff:feb2:95d4]"));
        assertEquals(new InetSocketAddress("fe80::868f:69ff:feb2:95d4", 27017),
                ServerNameUtils.parse("fe80::868f:69ff:feb2:95d4"));
    }
}
