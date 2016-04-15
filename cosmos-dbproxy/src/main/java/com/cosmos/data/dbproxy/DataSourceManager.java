package com.cosmos.data.dbproxy;

/**
 * Data Source Manager that hold the thread local {@link DataSourceType}.
 *
 * @author BSD
 */
public class DataSourceManager {

    // default data source type, in practice we use master to write to and slave to read from
    private static final ThreadLocal<DataSourceType> dataSourceTypes = new ThreadLocal<DataSourceType>() {
        @Override
        protected DataSourceType initialValue() {
            return DataSourceType.MASTER;
        }
    };

    /**
     * Get which data source in use.
     *
     * @return data source type
     */
    public static DataSourceType getDataSourceType() {
        return dataSourceTypes.get();
    }

    /**
     * Set the specific data source to use.
     *
     * @param dataSourceType data source type
     */
    public static void setDataSourceType(DataSourceType dataSourceType) {
        dataSourceTypes.set(dataSourceType);
    }

    /**
     * Reset the data source to default, which is {@link DataSourceType#MASTER}.
     */
    public static void resetDataSourceType() {
        dataSourceTypes.set(DataSourceType.MASTER);
    }
}
