/*
 * Copyright 2012, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

package com.allanbank.mongodb.client;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.allanbank.mongodb.Callback;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.connection.message.Reply;

/**
 * ReplyResultCallbackTest provides tests for the {@link ReplyResultCallback}.
 * 
 * @copyright 2012, Allanbank Consulting, Inc., All Rights Reserved
 */
public class ReplyResultCallbackTest {

    /**
     * Test method for {@link ReplyResultCallback#convert(Reply)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testConvertReply() {
        final DocumentBuilder db = BuilderFactory.start().addInteger("ok", 1);
        db.pushArray("results").push();

        final List<Document> docs = Collections.singletonList(db.build());
        final Reply reply = new Reply(0, 0, 0, docs, false, false, false, true);

        final Callback<List<Document>> mockCallback = createMock(Callback.class);

        replay(mockCallback);

        final ReplyResultCallback callback = new ReplyResultCallback(
                mockCallback);
        assertEquals(Collections.singletonList(BuilderFactory.start().build()),
                callback.convert(reply));

        verify(mockCallback);
    }

    /**
     * Test method for {@link ReplyResultCallback#convert(Reply)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testConvertReplyNoResults() {
        final DocumentBuilder db = BuilderFactory.start().addInteger("ok", 1);
        db.pushArray("results");

        final List<Document> docs = Collections.singletonList(db.build());
        final Reply reply = new Reply(0, 0, 0, docs, false, false, false, true);

        final Callback<List<Document>> mockCallback = createMock(Callback.class);

        replay(mockCallback);

        final ReplyResultCallback callback = new ReplyResultCallback(
                mockCallback);
        assertEquals(Collections.emptyList(), callback.convert(reply));

        verify(mockCallback);
    }

    /**
     * Test method for {@link ReplyResultCallback#convert(Reply)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testConvertReplyToManyDocs() {
        final DocumentBuilder db = BuilderFactory.start().addInteger("ok", 1);
        db.pushArray("results").push();
        final List<Document> docs = Arrays.asList(db.build(), db.build());
        final Reply reply = new Reply(0, 0, 0, docs, false, false, false, true);

        final Callback<List<Document>> mockCallback = createMock(Callback.class);

        replay(mockCallback);

        final ReplyResultCallback callback = new ReplyResultCallback(
                mockCallback);
        assertTrue(callback.convert(reply).isEmpty());

        verify(mockCallback);
    }

}
