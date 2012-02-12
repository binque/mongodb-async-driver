/*
 * Copyright 2011-2012, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

package com.allanbank.mongodb.client;

import java.util.List;

import com.allanbank.mongodb.Callback;
import com.allanbank.mongodb.MongoDbException;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.NumericElement;
import com.allanbank.mongodb.bson.element.StringElement;
import com.allanbank.mongodb.connection.message.Reply;
import com.allanbank.mongodb.error.CursorNotFoundException;
import com.allanbank.mongodb.error.QueryFailedException;
import com.allanbank.mongodb.error.ReplyException;
import com.allanbank.mongodb.error.ShardConfigStaleException;

/**
 * Helper class for constructing callbacks that convert a {@link Reply} message
 * into a different type of result.
 * 
 * @param <F>
 *            The type for the converted {@link Reply}.
 * 
 * @copyright 2011-2012, Allanbank Consulting, Inc., All Rights Reserved
 */
public abstract class AbstractReplyCallback<F> implements Callback<Reply> {

    /** The callback for the converted type. */
    private final Callback<F> myForwardCallback;

    /**
     * Create a new AbstractReplyCallback.
     * 
     * @param forwardCallback
     *            The callback for the converted type.
     */
    public AbstractReplyCallback(final Callback<F> forwardCallback) {
        myForwardCallback = forwardCallback;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to {@link #verify(Reply) verify} the reply and then
     * {@link #convert(Reply) convert} it to the final result.
     * </p>
     * 
     * @see Callback#callback
     */
    @Override
    public void callback(final Reply result) {

        try {
            verify(result);

            getForwardCallback().callback(convert(result));
        }
        catch (final MongoDbException error) {
            exception(error);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to forward the exception to the {@link #myForwardCallback}.
     * </p>
     * 
     * @see Callback#exception
     */
    @Override
    public void exception(final Throwable thrown) {
        getForwardCallback().exception(thrown);
    }

    /**
     * Returns the forwardCallback value.
     * 
     * @return the forwardCallback
     */
    public Callback<F> getForwardCallback() {
        return myForwardCallback;
    }

    /**
     * Creates an exception from the {@link Reply}.
     * 
     * @param reply
     *            The raw reply.
     * @return The exception created.
     */
    protected MongoDbException asError(final Reply reply) {
        final List<Document> results = reply.getResults();
        if (results.size() == 1) {
            final Document doc = results.get(0);
            final Element okElem = doc.get("ok");
            final Element errorNumberElem = doc.get("code");
            final Element errorMessageElem = doc.get("errmsg");
            if ((okElem != null) && (errorMessageElem != null)) {
                final int okValue = toInt(okElem);
                if (okValue != 1) {
                    return asError(reply, okValue, toInt(errorNumberElem),
                            asString(errorMessageElem));
                }
            }
        }
        return null;
    }

    /**
     * Creates an exception from the parsed reply fields.
     * 
     * @param reply
     *            The raw reply.
     * @param okValue
     *            The 'ok' field.
     * @param errorNumber
     *            The 'errno' field.
     * @param errorMessage
     *            The 'errmsg' field.
     * @return The exception created.
     */
    protected MongoDbException asError(final Reply reply, final int okValue,
            final int errorNumber, final String errorMessage) {
        return new ReplyException(okValue, errorNumber, errorMessage, reply);
    }

    /**
     * Converts the {@link Element} to a string. If a {@link StringElement} the
     * value of the element is returned. In all other cases the toString()
     * result for the element is returned.
     * 
     * @param errorMessageElem
     *            The element to convert to a string.
     * @return The {@link Element}'s string value.
     */
    protected String asString(final Element errorMessageElem) {
        if (errorMessageElem instanceof StringElement) {
            return ((StringElement) errorMessageElem).getValue();
        }
        return String.valueOf(errorMessageElem);
    }

    /**
     * Checks for a non-flag error in the reply.
     * 
     * @param reply
     *            The reply to check.
     * @throws MongoDbException
     *             On an error represented in the reply.
     */
    protected void checkForError(final Reply reply) throws MongoDbException {
        final MongoDbException exception = asError(reply);
        if (exception != null) {
            throw exception;
        }
    }

    /**
     * Converts the {@link Reply} into the final response type.
     * 
     * @param reply
     *            The reply to convert.
     * @return The converted reply.
     * @throws MongoDbException
     *             On a failure converting the reply. Generally, the
     *             {@link #verify(Reply)} method should be used to report
     *             errors.
     */
    protected abstract F convert(Reply reply) throws MongoDbException;

    /**
     * Converts a {@link NumericElement}into an <tt>int</tt> value. If not a
     * {@link NumericElement} then -1 is returned.
     * 
     * @param element
     *            The element to convert.
     * @return The element's integer value or -1.
     */
    protected int toInt(final Element element) {
        if (element instanceof NumericElement) {
            return ((NumericElement) element).getIntValue();
        }

        return -1;
    }

    /**
     * Checks the reply for an error message.
     * 
     * @param reply
     *            The Reply to verify is successful.
     * @throws MongoDbException
     *             On a failure message in the reply.
     */
    protected void verify(final Reply reply) throws MongoDbException {
        if (reply.isCursorNotFound()) {
            throw new CursorNotFoundException(reply, asError(reply));
        }
        else if (reply.isQueryFailed()) {
            throw new QueryFailedException(reply, asError(reply));
        }
        else if (reply.isShardConfigStale()) {
            throw new ShardConfigStaleException(reply, asError(reply));
        }
        else {
            checkForError(reply);
        }
    }
}
