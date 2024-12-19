package dev.thezexquex.yasmpp.data.database;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.sqlite.databases.SqLite;

import java.nio.file.Path;
import java.sql.Driver;

public class DataBaseProvider {
    private final Path dataBasePath;

    public DataBaseProvider(Path dataBasePath) {
        this.dataBasePath = dataBasePath;
    }

    public HikariDataSource createDataSource() {
        return DataSourceCreator.create(SqLite.get())
                // We configure the usual stuff.
                .configure(config -> config
                        .path(dataBasePath)
                        .driverClass(Driver.class)
                )
                .create()
                .withMaximumPoolSize(3)
                .withMinimumIdle(1)
                .build();
    }
}
