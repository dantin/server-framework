package com.cosmos.data.dbproxy;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Thread Local Routing Data Sources which choose the right {@link DataSourceType}.
 */
public class ThreadLocalRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceManager.getDataSourceType();
    }
}
