/*
 * Copyright 2013, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

package com.allanbank.mongodb.client.callback;

import java.util.ArrayList;
import java.util.List;

import com.allanbank.mongodb.Callback;
import com.allanbank.mongodb.MongoIterator;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.builder.ConditionBuilder;
import com.allanbank.mongodb.builder.Text;
import com.allanbank.mongodb.builder.TextResult;
import com.allanbank.mongodb.client.SimpleMongoIteratorImpl;

/**
 * TextCallback provides conversion from a {@link Text text} command's result
 * document to a {@link TextResult}.
 * 
 * @api.no This class is <b>NOT</b> part of the drivers API. This class may be
 *         mutated in incompatible ways between any two releases of the driver.
 * @deprecated Support for the {@code text} command was deprecated in the 2.6
 *             version of MongoDB. Use the {@link ConditionBuilder#text(String)
 *             $text} query operator instead. This class will not be removed
 *             until two releases after the MongoDB 2.6 release (e.g. 2.10 if
 *             the releases are 2.8 and 2.10).
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
@Deprecated
public class TextCallback implements Callback<MongoIterator<Document>> {

    /** The delegate callback to receive the {@link TextResult}s. */
    private final Callback<MongoIterator<TextResult>> myDelegate;

    /**
     * Creates a new TextCallback.
     * 
     * @param delegate
     *            The delegate callback to receive the {@link TextResult}s.
     */
    public TextCallback(final Callback<MongoIterator<TextResult>> delegate) {
        myDelegate = delegate;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert each document into a {@link TextResult} and forward
     * to the delegate.
     * </p>
     */
    @Override
    public void callback(final MongoIterator<Document> result) {
        final List<TextResult> results = new ArrayList<TextResult>();

        for (final Document doc : result) {
            results.add(new TextResult(doc));
        }

        myDelegate.callback(new SimpleMongoIteratorImpl<TextResult>(results));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to forward to the delegate callback.
     * </p>
     */
    @Override
    public void exception(final Throwable thrown) {
        myDelegate.exception(thrown);
    }
}