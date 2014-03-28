/*
 * Copyright 2011-2013, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */
package com.allanbank.mongodb.client.connection.bootstrap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import com.allanbank.mongodb.MongoClientConfiguration;
import com.allanbank.mongodb.Version;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.element.StringElement;
import com.allanbank.mongodb.client.ClusterType;
import com.allanbank.mongodb.client.FutureCallback;
import com.allanbank.mongodb.client.connection.Connection;
import com.allanbank.mongodb.client.connection.ConnectionFactory;
import com.allanbank.mongodb.client.connection.ReconnectStrategy;
import com.allanbank.mongodb.client.connection.auth.AuthenticationConnectionFactory;
import com.allanbank.mongodb.client.connection.proxy.ProxiedConnectionFactory;
import com.allanbank.mongodb.client.connection.rs.ReplicaSetConnectionFactory;
import com.allanbank.mongodb.client.connection.sharded.ShardedConnectionFactory;
import com.allanbank.mongodb.client.connection.socket.SocketConnectionFactory;
import com.allanbank.mongodb.client.message.IsMaster;
import com.allanbank.mongodb.client.message.Reply;
import com.allanbank.mongodb.client.state.Cluster;
import com.allanbank.mongodb.error.CannotConnectException;
import com.allanbank.mongodb.util.IOUtils;
import com.allanbank.mongodb.util.log.Log;
import com.allanbank.mongodb.util.log.LogFactory;

/**
 * Provides the ability to bootstrap into the appropriate
 * {@link ConnectionFactory} based on the configuration of the server(s)
 * connected to.
 * 
 * @api.no This class is <b>NOT</b> part of the drivers API. This class may be
 *         mutated in incompatible ways between any two releases of the driver.
 * @copyright 2011-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class BootstrapConnectionFactory implements ConnectionFactory {

    /** The logger for the {@link BootstrapConnectionFactory}. */
    protected static final Log LOG = LogFactory
            .getLog(BootstrapConnectionFactory.class);

    /** The configuration for the connections to be created. */
    private final MongoClientConfiguration myConfig;

    /** The delegate connection factory post */
    private ConnectionFactory myDelegate = null;

    /**
     * Creates a {@link BootstrapConnectionFactory}
     * 
     * @param config
     *            The configuration to use in discovering the server
     *            configuration.
     */
    public BootstrapConnectionFactory(final MongoClientConfiguration config) {
        myConfig = config;
    }

    /**
     * Re-bootstraps the environment. Normally this method is only called once
     * during the constructor of the factory to initialize the delegate but
     * users can reset the delegate by manually invoking this method.
     * <p>
     * A bootstrap will issue one commands to the first working MongoDB process.
     * The reply to the {@link IsMaster} command is used to detect connecting to
     * a mongos <tt>process</tt> and by extension a Sharded configuration.
     * </p>
     * <p>
     * If not using a Sharded configuration then the server status is checked
     * for a <tt>repl</tt> element. If present a Replication Set configuration
     * is assumed.
     * </p>
     * <p>
     * If neither a Sharded or Replication Set is being used then a plain socket
     * connection factory is used.
     * </p>
     */
    public void bootstrap() {
        ProxiedConnectionFactory factory = new SocketConnectionFactory(myConfig);
        // Authentication has to be right on top of the physical
        // connection.
        if (myConfig.isAuthenticating()) {
            factory = new AuthenticationConnectionFactory(factory, myConfig);
        }
        try {
            final Cluster cluster = new Cluster(myConfig);
            for (final InetSocketAddress addr : myConfig.getServerAddresses()) {
                Connection conn = null;
                final FutureCallback<Reply> future = new FutureCallback<Reply>();
                try {
                    conn = factory.connect(cluster.add(addr), myConfig);

                    conn.send(new IsMaster(), future);
                    final Reply reply = future.get();

                    // Close the connection now that we have the reply.
                    IOUtils.close(conn);

                    final List<Document> results = reply.getResults();
                    if (!results.isEmpty()) {
                        final Document doc = results.get(0);

                        if (isMongos(doc)) {
                            LOG.debug("Sharded bootstrap to {}.", addr);
                            myDelegate = new ShardedConnectionFactory(factory,
                                    myConfig);
                        }
                        else if (isReplicationSet(doc)) {
                            LOG.debug("Replica-set bootstrap to {}.", addr);
                            myDelegate = new ReplicaSetConnectionFactory(
                                    factory, myConfig);
                        }
                        else {
                            LOG.debug("Simple MongoDB bootstrap to {}.", addr);
                            myDelegate = factory;
                        }
                        factory = null; // Don't close.
                        return;
                    }
                }
                catch (final IOException ioe) {
                    LOG.warn(ioe, "I/O error during bootstrap to {}.", addr);
                }
                catch (final InterruptedException e) {
                    LOG.warn(e, "Interrupted during bootstrap to {}.", addr);
                }
                catch (final ExecutionException e) {
                    LOG.warn(e, "Error during bootstrap to {}.", addr);
                }
                finally {
                    IOUtils.close(conn, Level.WARNING,
                            "I/O error shutting down bootstrap connection to "
                                    + addr + ".");
                }
            }
        }
        finally {
            IOUtils.close(factory);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to close the delegate {@link ConnectionFactory}.
     * </p>
     */
    @Override
    public void close() {
        IOUtils.close(myDelegate);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates the connection to the setup delegate.
     * </p>
     */
    @Override
    public Connection connect() throws IOException {
        return getDelegate().connect();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return the cluster type of the delegate
     * {@link ConnectionFactory}.
     * </p>
     */
    @Override
    public ClusterType getClusterType() {
        return getDelegate().getClusterType();
    }

    /**
     * Returns the maximum server version within the cluster.
     * 
     * @return The maximum server version within the cluster.
     */
    @Override
    public Version getMaximumServerVersion() {
        return getDelegate().getMaximumServerVersion();
    }

    /**
     * Returns the minimum server version within the cluster.
     * 
     * @return The minimum server version within the cluster.
     */
    @Override
    public Version getMinimumServerVersion() {
        return getDelegate().getMinimumServerVersion();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return the delegates strategy.
     * </p>
     */
    @Override
    public ReconnectStrategy getReconnectStrategy() {
        return getDelegate().getReconnectStrategy();
    }

    /**
     * Returns smallest value for the maximum number of write operations allowed
     * in a single write command.
     * 
     * @return The smallest value for maximum number of write operations allowed
     *         in a single write command.
     */
    @Override
    public int getSmallestMaxBatchedWriteOperations() {
        return getDelegate().getSmallestMaxBatchedWriteOperations();
    }

    /**
     * Returns the smallest value for the maximum BSON object within the
     * cluster.
     * 
     * @return The smallest value for the maximum BSON object within the
     *         cluster.
     */
    @Override
    public long getSmallestMaxBsonObjectSize() {
        return getDelegate().getSmallestMaxBsonObjectSize();
    }

    /**
     * Returns the underlying delegate factory.
     * 
     * @return The underlying delegate factory.
     */
    protected ConnectionFactory getDelegate() {
        if (myDelegate == null) {
            bootstrap();
            if (myDelegate == null) {
                LOG.warn("Could not bootstrap a connection to the MongoDB servers.");
                throw new CannotConnectException(
                        "Could not bootstrap a connection to the MongoDB servers.");
            }
        }
        return myDelegate;
    }

    /**
     * Sets the underlying delegate factory.
     * 
     * @param delegate
     *            The underlying delegate factory.
     */
    protected void setDelegate(final ConnectionFactory delegate) {
        myDelegate = delegate;
    }

    /**
     * Returns true if the document contains a "process" element that is a
     * string and contains the value "mongos".
     * 
     * @param doc
     *            The document to validate.
     * @return True if the document contains a "process" element that is a
     *         string and contains the value "mongos".
     */
    private boolean isMongos(final Document doc) {

        final Element processName = doc.get("msg");
        if (processName instanceof StringElement) {
            return "isdbgrid".equals(((StringElement) processName).getValue());
        }

        return false;
    }

    /**
     * Returns true if the document contains a "repl" element that is a
     * sub-document.
     * 
     * @param doc
     *            The document to validate.
     * @return True if the document contains a "repl" element that is a
     *         sub-document.
     */
    private boolean isReplicationSet(final Document doc) {
        return (doc.get("setName") instanceof StringElement);
    }
}