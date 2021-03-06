/*
 * #%L
 * AnswerCallback.java - mongodb-async-driver - Allanbank Consulting, Inc.
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

package com.allanbank.mongodb;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

import com.allanbank.mongodb.client.callback.AddressAware;
import com.allanbank.mongodb.client.callback.ReplyCallback;
import com.allanbank.mongodb.client.message.Reply;

/**
 * AnswerCallback provides the ability to provide replies to callbacks.
 *
 * @param <R>
 *            The type for the reply callback.
 * @copyright 2012-2014, Allanbank Consulting, Inc., All Rights Reserved
 */
public class AnswerCallback<R>
        implements IArgumentMatcher {
    /**
     * Helper for matching callbacks and triggering them at method invocation
     * time.
     *
     * @param <T>
     *            The type for the callback.
     *
     * @return <code>null</code>
     */
    public static <T> ReplyCallback callback() {
        EasyMock.reportMatcher(new AnswerCallback<T>());
        return null;
    }

    /**
     * Helper for matching callbacks and triggering them at method invocation
     * time.
     *
     * @param reply
     *            The reply to give the callback when matching.
     * @return <code>null</code>
     */
    public static ReplyCallback callback(final Reply reply) {
        EasyMock.reportMatcher(new AnswerCallback<Reply>(reply));
        return null;
    }

    /**
     * Helper for matching callbacks and triggering them at method invocation
     * time.
     *
     * @param <T>
     *            The type for the callback.
     * @param reply
     *            The reply to give the callback when matching.
     * @return <code>null</code>
     */
    public static <T> Callback<T> callback(final T reply) {
        EasyMock.reportMatcher(new AnswerCallback<T>(reply));
        return null;
    }

    /**
     * Helper for matching callbacks and triggering them at method invocation
     * time.
     *
     * @param <T>
     *            The type for the callback.
     * @param error
     *            The error to provide the callback.
     *
     * @return <code>null</code>
     */
    public static <T> ReplyCallback callback(final Throwable error) {
        EasyMock.reportMatcher(new AnswerCallback<T>(error));
        return null;
    }

    /** The reply to provide the callbacks. */
    private final Throwable myError;

    /** The reply to provide the callbacks. */
    private final R myReply;

    /**
     * Creates a new AnswerCallback.
     */
    public AnswerCallback() {
        myReply = null;
        myError = null;
    }

    /**
     * Creates a new AnswerCallback.
     *
     * @param reply
     *            The reply to provide the callbacks.
     */
    public AnswerCallback(final R reply) {
        myReply = reply;
        myError = null;
    }

    /**
     * Creates a new AnswerCallback.
     *
     * @param error
     *            The reply to provide the callbacks.
     */
    public AnswerCallback(final Throwable error) {
        myError = error;
        myReply = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void appendTo(final StringBuffer buffer) {
        if (myError != null) {
            buffer.append("Callback<" + myError.getClass().getSimpleName()
                    + ">");
        }
        else if (myReply != null) {
            buffer.append("Callback<" + myReply.getClass().getSimpleName()
                    + ">");
        }
        else {
            buffer.append("Callback<<interrupt>>");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to check if the argument is a {@link Callback}, if so provide
     * it the reply and return true.
     * </p>
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(final Object argument) {
        if (argument instanceof AddressAware) {
            ((AddressAware) argument).setAddress("localhost:27017");
        }
        if (argument instanceof Callback<?>) {
            if (myError != null) {
                ((Callback<R>) argument).exception(myError);
            }
            else if (myReply != null) {
                ((Callback<R>) argument).callback(myReply);
            }
            else {
                Thread.currentThread().interrupt();
            }
            return true;
        }
        return false;
    }
}