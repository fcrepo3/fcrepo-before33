package fedora.server.utilities;

import java.sql.*;
import java.util.Map;

/**
 *
 * <p><b>Title:</b> ConnectionWrapper.java</p>
 * <p><b>Description:</b> A wrapper around a java.sql.Connection that calls
 * the wrapped Connection's methods for all calls.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public abstract class ConnectionWrapper
        implements Connection {

    private Connection m_wrappedConnection;

    public ConnectionWrapper(Connection wrapped) {
        m_wrappedConnection=wrapped;
    }

    public Statement createStatement()
            throws SQLException {
        return m_wrappedConnection.createStatement();
    }

    public Statement createStatement(int resultSetType,
            int resultSetConcurrency)
            throws SQLException {
        return m_wrappedConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql)
            throws SQLException {
        return m_wrappedConnection.prepareStatement(sql);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType,
            int resultSetConcurrency)
            throws SQLException {
        return m_wrappedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql)
            throws SQLException {
        return m_wrappedConnection.prepareCall(sql);
    }

    public CallableStatement prepareCall(String sql, int resultSetType,
            int resultSetConcurrency)
            throws SQLException {
        return m_wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public String nativeSQL(String sql)
            throws SQLException {
        return m_wrappedConnection.nativeSQL(sql);
    }

    public boolean getAutoCommit()
            throws SQLException {
        return m_wrappedConnection.getAutoCommit();
    }

    public void setAutoCommit(boolean autoCommit)
            throws SQLException {
        m_wrappedConnection.setAutoCommit(autoCommit);
    }

    public void commit()
            throws SQLException {
        m_wrappedConnection.commit();
    }

    public void rollback()
            throws SQLException {
        m_wrappedConnection.rollback();
    }

    public void close()
            throws SQLException {
        m_wrappedConnection.close();
    }

    public boolean isClosed()
            throws SQLException {
        return m_wrappedConnection.isClosed();
    }

    public DatabaseMetaData getMetaData()
            throws SQLException {
        return m_wrappedConnection.getMetaData();
    }

    public boolean isReadOnly()
            throws SQLException {
        return m_wrappedConnection.isReadOnly();
    }

    public void setReadOnly(boolean readOnly)
            throws SQLException {
        m_wrappedConnection.setReadOnly(readOnly);
    }

    public String getCatalog()
            throws SQLException {
        return m_wrappedConnection.getCatalog();
    }

    public void setCatalog(String catalog)
            throws SQLException {
        m_wrappedConnection.setCatalog(catalog);
    }

    public int getTransactionIsolation()
            throws SQLException {
        return m_wrappedConnection.getTransactionIsolation();
    }

    public void setTransactionIsolation(int level)
            throws SQLException {
        m_wrappedConnection.setTransactionIsolation(level);
    }

    public SQLWarning getWarnings()
            throws SQLException {
        return m_wrappedConnection.getWarnings();
    }

    public void clearWarnings()
            throws SQLException {
        m_wrappedConnection.clearWarnings();
    }

    public Map getTypeMap()
            throws SQLException {
        return m_wrappedConnection.getTypeMap();
    }

    public void setTypeMap(Map map)
            throws SQLException {
        m_wrappedConnection.setTypeMap(map);
    }

    public void setHoldability(int holdability)
            throws SQLException {
        m_wrappedConnection.setHoldability(holdability);
    }

    public int getHoldability()
            throws SQLException {
        return m_wrappedConnection.getHoldability();
    }

    public Savepoint setSavepoint()
            throws SQLException {
        return m_wrappedConnection.setSavepoint();
    }

    public Savepoint setSavepoint(String name)
            throws SQLException {
        return m_wrappedConnection.setSavepoint(name);
    }

    public void rollback(Savepoint savepoint)
            throws SQLException {
        m_wrappedConnection.rollback(savepoint);
    }

    public void releaseSavepoint(Savepoint savepoint)
            throws SQLException {
        m_wrappedConnection.releaseSavepoint(savepoint);
    }

    public Statement createStatement(int resultSetType,
            int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return m_wrappedConnection.createStatement(resultSetType,
                resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql,
            int resultSetType, int resultSetConcurrency,
            int resultSetHoldability)
            throws SQLException {
        return m_wrappedConnection.prepareStatement(sql, resultSetType,
                resultSetConcurrency, resultSetHoldability);
    }

    public CallableStatement prepareCall(String sql, int resultSetType,
            int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return m_wrappedConnection.prepareCall(sql, resultSetType,
                resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql,int autoGeneratedKeys)
            throws SQLException {
        return m_wrappedConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
            throws SQLException {
        return m_wrappedConnection.prepareStatement(sql, columnIndexes);
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames)
            throws SQLException {
        return m_wrappedConnection.prepareStatement(sql, columnNames);
    }

}