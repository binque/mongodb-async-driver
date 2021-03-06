/*
 * #%L
 * ReplyResultCallback.java - mongodb-async-driver - Allanbank Consulting, Inc.
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

package com.allanbank.mongodb.client.callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.allanbank.mongodb.Callback;
import com.allanbank.mongodb.MongoDbException;
import com.allanbank.mongodb.MongoIterator;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.element.DocumentElement;
import com.allanbank.mongodb.client.SimpleMongoIteratorImpl;
import com.allanbank.mongodb.client.message.Reply;

/**
 * Callback to extract the map/reduce and aggregation results from the reply.
 *
 * @api.no This class is <b>NOT</b> part of the drivers API. This class may be
 *         mutated in incompatible ways between any two releases of the driver.
 * @copyright 2011-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class ReplyResultCallback
        extends AbstractReplyCallback<MongoIterator<Document>> {

    /** The field in the reply holding the results. */
    private final String myReplyField;

    /**
     * Create a new ReplyResultCallback.
     *
     * @param forwardCallback
     *            The callback to forward the result documents to.
     */
    public ReplyResultCallback(
            final Callback<MongoIterator<Document>> forwardCallback) {
        this("results", forwardCallback);
    }

    /**
     * Create a new ReplyResultCallback.
     *
     * @param field
     *            The field in the reply holding the results.
     * @param forwardCallback
     *            The callback to forward the result documents to.
     */
    public ReplyResultCallback(final String field,
            final Callback<MongoIterator<Document>> forwardCallback) {
        super(forwardCallback);
        myReplyField = field;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to extract the 'results' elements from the reply.
     * </p>
     *
     * @see com.allanbank.mongodb.client.callback.AbstractReplyCallback#convert(com.allanbank.mongodb.client.message.Reply)
     */
    @Override
    protected MongoIterator<Document> convert(final Reply reply)
            throws MongoDbException {
        List<Document> results = Collections.emptyList();

        final List<Document> replyDocs = reply.getResults();
        if (replyDocs.size() == 1) {
            final Document doc = replyDocs.get(0);

            final List<DocumentElement> resultsElems = doc.find(
                    DocumentElement.class, myReplyField, ".*");
            if (!resultsElems.isEmpty()) {
                results = new ArrayList<Document>();
                for (final DocumentElement resultElem : resultsElems) {
                    results.add(resultElem.getDocument());
                }
            }
        }

        return new SimpleMongoIteratorImpl<Document>(results);
    }

}
