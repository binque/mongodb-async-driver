/*
 * Copyright 2012, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

package com.allanbank.mongodb.client;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.allanbank.mongodb.Callback;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.ArrayElement;
import com.allanbank.mongodb.connection.message.Reply;
import com.allanbank.mongodb.error.ReplyException;

/**
 * ReplyArrayCallbackTest provides tests for the {@link ReplyArrayCallback}.
 * 
 * @copyright 2012, Allanbank Consulting, Inc., All Rights Reserved
 */
public class ReplyArrayCallbackTest {

    /**
     * Test method for {@link ReplyArrayCallback#asError(Reply)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testAsErrorReply() {
        final DocumentBuilder db = BuilderFactory.start().addInteger("ok", 1);
        db.pushArray("n");

        final List<Document> docs = Collections.singletonList(db.build());
        final Reply reply = new Reply(0, 0, 0, docs, false, false, false, true);

        final Callback<ArrayElement> mockCallback = createMock(Callback.class);

        replay(mockCallback);

        final ReplyArrayCallback callback = new ReplyArrayCallback(mockCallback);
        final ReplyException error = (ReplyException) callback.asError(reply);
        assertEquals(-1, error.getOkValue());

        verify(mockCallback);
    }

    /**
     * Test method for {@link ReplyArrayCallback#asError(Reply)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testAsErrorReplyBadOk() {
        final DocumentBuilder db = BuilderFactory.start().addInteger("ok", 0);
        db.pushArray("n");

        final List<Document> docs = Collections.singletonList(db.build());
        final Reply reply = new Reply(0, 0, 0, docs, false, false, false, true);

        final Callback<ArrayElement> mockCallback = createMock(Callback.class);

        replay(mockCallback);

        final ReplyArrayCallback callback = new ReplyArrayCallback(mockCallback);
        final ReplyException error = (ReplyException) callback.asError(reply);
        assertNotNull(error);

        verify(mockCallback);
    }

    /**
     * Test method for {@link ReplyArrayCallback#asError(Reply)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testAsErrorReplyNoN() {
        final DocumentBuilder db = BuilderFactory.start().addInteger("ok", 1);
        db.pushArray("n");

        final List<Document> docs = Collections.singletonList(db.build());
        final Reply reply = new Reply(0, 0, 0, docs, false, false, false, true);

        final Callback<ArrayElement> mockCallback = createMock(Callback.class);

        replay(mockCallback);

        final ReplyArrayCallback callback = new ReplyArrayCallback(mockCallback);
        final ReplyException error = (ReplyException) callback.asError(reply);
        assertTrue(error.getMessage().contains("array in the reply"));

        verify(mockCallback);
    }

    /**
     * Test method for {@link ReplyArrayCallback#asError(Reply)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testAsErrorReplyWithN() {
        final DocumentBuilder db = BuilderFactory.start().addInteger("ok", 1);
        db.pushArray("n");

        final List<Document> docs = Collections.singletonList(db.build());
        final Reply reply = new Reply(0, 0, 0, docs, false, false, false, true);

        final Callback<ArrayElement> mockCallback = createMock(Callback.class);

        replay(mockCallback);

        final ReplyArrayCallback callback = new ReplyArrayCallback("n",
                mockCallback);
        assertNull(callback.asError(reply));

        verify(mockCallback);
    }

    /**
     * Test method for {@link ReplyArrayCallback#asError(Reply)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testAsErrorReplyWithToManyDocs() {
        final DocumentBuilder db = BuilderFactory.start().addInteger("ok", 1);
        db.pushArray("n");

        final List<Document> docs = Arrays.asList(db.build(), BuilderFactory
                .start().build());
        final Reply reply = new Reply(0, 0, 0, docs, false, false, false, true);

        final Callback<ArrayElement> mockCallback = createMock(Callback.class);

        replay(mockCallback);

        final ReplyArrayCallback callback = new ReplyArrayCallback("n",
                mockCallback);
        assertNotNull(callback.asError(reply));

        verify(mockCallback);
    }

    /**
     * Test method for {@link ReplyArrayCallback#convert(Reply)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testConvertReply() {
        final DocumentBuilder db = BuilderFactory.start().addInteger("ok", 1);
        db.pushArray("n");

        final List<Document> docs = Collections.singletonList(db.build());
        final Reply reply = new Reply(0, 0, 0, docs, false, false, false, true);

        final Callback<ArrayElement> mockCallback = createMock(Callback.class);

        replay(mockCallback);

        final ReplyArrayCallback callback = new ReplyArrayCallback("n",
                mockCallback);
        assertEquals(new ArrayElement("n"), callback.convert(reply));

        verify(mockCallback);
    }

    /**
     * Test method for {@link ReplyArrayCallback#convert(Reply)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testConvertReplyToManyDocs() {
        final DocumentBuilder db = BuilderFactory.start().addInteger("ok", 1);
        db.pushArray("n");
        final List<Document> docs = Arrays.asList(db.build(), db.build());
        final Reply reply = new Reply(0, 0, 0, docs, false, false, false, true);

        final Callback<ArrayElement> mockCallback = createMock(Callback.class);

        replay(mockCallback);

        final ReplyArrayCallback callback = new ReplyArrayCallback("n",
                mockCallback);
        assertNull(callback.convert(reply));

        verify(mockCallback);
    }

}
