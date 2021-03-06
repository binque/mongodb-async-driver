/*
 * #%L
 * BufferingBsonOutputStream.java - mongodb-async-driver - Allanbank Consulting, Inc.
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
package com.allanbank.mongodb.bson.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.Visitor;

/**
 * {@link BufferingBsonOutputStream} provides a class to write BSON documents
 * based on the <a href="http://bsonspec.org/">BSON specification</a>.
 * <p>
 * Users of this class must make sure that the {@link #flushBuffer()} method is
 * called after calling any of the {@link #writeInt(int) writeXXX()} methods.
 * </p>
 *
 * @api.yes This class is part of the driver's API. Public and protected members
 *          will be deprecated for at least 1 non-bugfix release (version
 *          numbers are &lt;major&gt;.&lt;minor&gt;.&lt;bugfix&gt;) before being
 *          removed or modified.
 * @copyright 2011-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
@NotThreadSafe
public class BufferingBsonOutputStream
        extends FilterOutputStream {

    /** The {@link Visitor} to write the BSON documents. */
    private final RandomAccessOutputStream myOutput;

    /** The {@link Visitor} to write the BSON documents. */
    private final BufferingWriteVisitor myVisitor;

    /**
     * Creates a new {@link BufferingBsonOutputStream}.
     *
     * @param output
     *            The stream to write to.
     */
    public BufferingBsonOutputStream(final OutputStream output) {
        super(output);

        myVisitor = new BufferingWriteVisitor();
        myOutput = myVisitor.getOutputBuffer();
    }

    /**
     * Creates a new {@link BufferingBsonOutputStream}.
     *
     * @param output
     *            The stream to write to.
     * @param cache
     *            The cache for encoding strings.
     */
    public BufferingBsonOutputStream(final OutputStream output,
            final StringEncoderCache cache) {
        super(output);

        myVisitor = new BufferingWriteVisitor(cache);
        myOutput = myVisitor.getOutputBuffer();
    }

    /**
     * Creates a new {@link BufferingBsonOutputStream}.
     *
     * @param output
     *            The stream to write to.
     */
    public BufferingBsonOutputStream(final RandomAccessOutputStream output) {
        super(output);

        myVisitor = new BufferingWriteVisitor(output);
        myOutput = myVisitor.getOutputBuffer();
    }

    /**
     * Writes any pending data to the underlying stream.
     * <p>
     * Users should call this method after calling any of the
     * {@link #writeInt(int) writeXXX(...)} methods.
     * </p>
     *
     * @throws IOException
     *             On a failure to write to the underlying document.
     */
    public void flushBuffer() throws IOException {
        if (out != myOutput) {
            myVisitor.writeTo(out);
            myVisitor.reset();
        }
    }

    /**
     * Returns the maximum number of strings that may have their encoded form
     * cached.
     *
     * @return The maximum number of strings that may have their encoded form
     *         cached.
     * @deprecated The cache {@link StringEncoderCache} should be controlled
     *             directory. This method will be removed after the 2.1.0
     *             release.
     */
    @Deprecated
    public int getMaxCachedStringEntries() {
        return myVisitor.getMaxCachedStringEntries();
    }

    /**
     * Returns the maximum length for a string that the stream is allowed to
     * cache.
     *
     * @return The maximum length for a string that the stream is allowed to
     *         cache.
     * @deprecated The cache {@link StringEncoderCache} should be controlled
     *             directory. This method will be removed after the 2.1.0
     *             release.
     */
    @Deprecated
    public int getMaxCachedStringLength() {
        return myVisitor.getMaxCachedStringLength();
    }

    /**
     * Returns the output buffer.
     *
     * @return The output buffer.
     */
    public RandomAccessOutputStream getOutput() {
        return myOutput;
    }

    /**
     * Returns the current position in the stream.
     *
     * @return The current position in the stream.
     */
    public long getPosition() {
        return myOutput.getPosition();
    }

    /**
     * Sets the value of maximum number of strings that may have their encoded
     * form cached.
     *
     * @param maxCacheEntries
     *            The new value for the maximum number of strings that may have
     *            their encoded form cached.
     * @deprecated The cache {@link StringEncoderCache} should be controlled
     *             directory. This method will be removed after the 2.1.0
     *             release.
     */
    @Deprecated
    public void setMaxCachedStringEntries(final int maxCacheEntries) {
        myVisitor.setMaxCachedStringEntries(maxCacheEntries);
    }

    /**
     * Sets the value of length for a string that the stream is allowed to cache
     * to the new value. This can be used to stop a single long string from
     * pushing useful values out of the cache.
     *
     * @param maxlength
     *            The new value for the length for a string that the encoder is
     *            allowed to cache.
     * @deprecated The cache {@link StringEncoderCache} should be controlled
     *             directory. This method will be removed after the 2.1.0
     *             release.
     */
    @Deprecated
    public void setMaxCachedStringLength(final int maxlength) {
        myVisitor.setMaxCachedStringLength(maxlength);

    }

    /**
     * Writes <code>b.length</code> bytes to this output stream.
     * <p>
     * Calls the write(byte[]) of the underlying stream.
     * </p>
     *
     * @param b
     *            the data to be written.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.FilterOutputStream#write(byte[], int, int)
     */
    @Override
    public void write(final byte b[]) throws IOException {
        out.write(b);
    }

    /**
     * Writes <code>len</code> bytes from the specified <code>byte</code> array
     * starting at offset <code>off</code> to this output stream.
     * <p>
     * Calls the write(byte[],int,int) of the underlying stream.
     * </p>
     *
     * @param b
     *            the data.
     * @param off
     *            the start offset in the data.
     * @param len
     *            the number of bytes to write.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.FilterOutputStream#write(int)
     */
    @Override
    public void write(final byte b[], final int off, final int len)
            throws IOException {
        out.write(b, off, len);
    }

    /**
     * Writes the Document in BSON format to the underlying stream.
     * <p>
     * This method automatically calls {@link #flushBuffer()}.
     * </p>
     *
     * @param doc
     *            The document to write.
     * @return The number of bytes written for the document.
     * @throws IOException
     *             On a failure to write to the underlying document.
     */
    public long write(final Document doc) throws IOException {

        doc.accept(myVisitor);

        final long position = myVisitor.getSize();

        flushBuffer();

        return position;
    }

    /**
     * Writes a single byte to the output buffer.
     * <p>
     * Users of this method must call {@link #flushBuffer()} or the contents
     * will not be written to the wrapped stream.
     * </p>
     *
     * @param b
     *            The byte to write.
     */
    public void writeByte(final byte b) {
        myOutput.writeByte(b);
    }

    /**
     * Writes a sequence of bytes to the output buffer.
     * <p>
     * Users of this method must call {@link #flushBuffer()} or the contents
     * will not be written to the wrapped stream.
     * </p>
     *
     * @param data
     *            The bytes to write.
     */
    public void writeBytes(final byte[] data) {
        myOutput.writeBytes(data);
    }

    /**
     * Writes a "Cstring" to the output buffer.
     * <p>
     * Users of this method must call {@link #flushBuffer()} or the contents
     * will not be written to the wrapped stream.
     * </p>
     *
     * @param strings
     *            The CString to write. The strings are concatenated into a
     *            single CString value.
     */
    public void writeCString(final String... strings) {
        myOutput.writeCString(strings);
    }

    /**
     * Writes the Document in BSON format to the underlying stream.
     * <p>
     * Users of this method must call {@link #flushBuffer()} or the contents
     * will not be written to the wrapped stream.
     * </p>
     *
     * @param doc
     *            The document to write.
     * @throws IOException
     *             On a failure to write to the underlying document.
     */
    public void writeDocument(final Document doc) throws IOException {
        doc.accept(myVisitor);
    }

    /**
     * Writes the integer value in little-endian byte order to the output
     * buffer.
     * <p>
     * Users of this method must call {@link #flushBuffer()} or the contents
     * will not be written to the wrapped stream.
     * </p>
     *
     * @param value
     *            The value to write.
     */
    public void writeInt(final int value) {
        myOutput.writeInt(value);
    }

    /**
     * Similar to {@link #writeInt(int)} but allows a portion of the already
     * written buffer to be re-written.
     * <p>
     * Users of this method must call {@link #flushBuffer()} or the contents
     * will not be written to the wrapped stream.
     * </p>
     *
     * @param position
     *            The position to write at. This location should have already
     *            been written.
     * @param value
     *            The integer value to write.
     */
    public void writeIntAt(final long position, final int value) {
        myOutput.writeIntAt(position, value);
    }

    /**
     * Write the long value in little-endian byte order to the output buffer.
     * <p>
     * Users of this method must call {@link #flushBuffer()} or the contents
     * will not be written to the wrapped stream.
     * </p>
     *
     * @param value
     *            The long to write.
     */
    public void writeLong(final long value) {
        myOutput.writeLong(value);
    }

    /**
     * Writes a "string" to the output buffer.
     * <p>
     * Users of this method must call {@link #flushBuffer()} or the contents
     * will not be written to the wrapped stream.
     * </p>
     *
     * @param string
     *            The String to write.
     */
    public void writeString(final String string) {
        myOutput.writeString(string);
    }
}
