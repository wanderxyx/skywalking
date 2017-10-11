package org.skywalking.apm.collector.agentstream.worker.service.entry.dao;

import org.skywalking.apm.collector.agentstream.worker.serviceref.dao.ServiceReferenceH2DAO;
import org.skywalking.apm.collector.client.h2.H2Client;
import org.skywalking.apm.collector.client.h2.H2ClientException;
import org.skywalking.apm.collector.core.stream.Data;
import org.skywalking.apm.collector.storage.define.DataDefine;
import org.skywalking.apm.collector.storage.define.service.ServiceEntryTable;
import org.skywalking.apm.collector.storage.define.serviceref.ServiceReferenceTable;
import org.skywalking.apm.collector.storage.h2.dao.H2DAO;
import org.skywalking.apm.collector.stream.worker.impl.dao.IPersistenceDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pengys5
 */
public class ServiceEntryH2DAO extends H2DAO implements IServiceEntryDAO, IPersistenceDAO<Map<String, Object>, Map<String, Object>> {
    private final Logger logger = LoggerFactory.getLogger(ServiceEntryH2DAO.class);
    @Override public Data get(String id, DataDefine dataDefine) {
        H2Client client = getClient();
        String sql = "select * from " + ServiceReferenceTable.TABLE + " where " + ServiceReferenceTable.COLUMN_ENTRY_SERVICE_ID + "=?";
        Object[] params = new Object[] {id};
        ResultSet rs = null;
        try {
            rs = client.executeQuery(sql, params);
            Data data = dataDefine.build(id);
            data.setDataInteger(0, rs.getInt(ServiceEntryTable.COLUMN_APPLICATION_ID));
            data.setDataInteger(1, rs.getInt(ServiceEntryTable.COLUMN_ENTRY_SERVICE_ID));
            data.setDataString(1, rs.getString(ServiceEntryTable.COLUMN_ENTRY_SERVICE_NAME));
            data.setDataLong(0, rs.getLong(ServiceEntryTable.COLUMN_REGISTER_TIME));
            data.setDataLong(1, rs.getLong(ServiceEntryTable.COLUMN_NEWEST_TIME));
            return data;
        } catch (SQLException | H2ClientException e) {
            logger.error(e.getMessage(), e);
        } finally {
            client.closeResultSet(rs);
        }
        return null;
    }
    @Override public Map<String, Object> prepareBatchInsert(Data data) {
        Map<String, Object> source = new HashMap<>();
        source.put(ServiceEntryTable.COLUMN_APPLICATION_ID, data.getDataInteger(0));
        source.put(ServiceEntryTable.COLUMN_ENTRY_SERVICE_ID, data.getDataInteger(1));
        source.put(ServiceEntryTable.COLUMN_ENTRY_SERVICE_NAME, data.getDataString(1));
        source.put(ServiceEntryTable.COLUMN_REGISTER_TIME, data.getDataLong(0));
        source.put(ServiceEntryTable.COLUMN_NEWEST_TIME, data.getDataLong(1));
        return source;
    }
    @Override public Map<String, Object> prepareBatchUpdate(Data data) {
        Map<String, Object> source = new HashMap<>();
        source.put(ServiceEntryTable.COLUMN_APPLICATION_ID, data.getDataInteger(0));
        source.put(ServiceEntryTable.COLUMN_ENTRY_SERVICE_ID, data.getDataInteger(1));
        source.put(ServiceEntryTable.COLUMN_ENTRY_SERVICE_NAME, data.getDataString(1));
        source.put(ServiceEntryTable.COLUMN_REGISTER_TIME, data.getDataLong(0));
        source.put(ServiceEntryTable.COLUMN_NEWEST_TIME, data.getDataLong(1));
        return source;
    }
}
