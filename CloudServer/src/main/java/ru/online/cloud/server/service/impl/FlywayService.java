package ru.online.cloud.server.service.impl;

import org.flywaydb.core.Flyway;
import ru.online.cloud.server.service.DataBaseMigrationService;
import ru.online.cloud.server.util.PropertyUtil;

public class FlywayService implements DataBaseMigrationService {

    private static FlywayService instance;

    private FlywayService() {
    }

    public static FlywayService getInstance() {
        if (instance == null) {
            instance = new FlywayService();
        }
        return instance;
    }

    @Override
    public void migrate() {
        Flyway flyway = Flyway.configure().dataSource(
                PropertyUtil.getServerDBConnection(),
                PropertyUtil.getServerDBLogin(),
                PropertyUtil.getServerDBPassword()
        ).load();
        flyway.migrate();
    }
}
