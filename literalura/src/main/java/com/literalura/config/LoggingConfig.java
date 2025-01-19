package com.literalura.config;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class LoggingConfig {
    public LoggingConfig() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        Logger hikariLogger = loggerContext.getLogger("com.zaxxer.hikari");
        hikariLogger.setLevel(Level.INFO);

        Logger hibernateSqlLogger = loggerContext.getLogger("org.hibernate.SQL");
        hibernateSqlLogger.setLevel(Level.INFO);

        Logger hibernateBinderLogger = loggerContext.getLogger("org.hibernate.type.descriptor.sql.BasicBinder");
        hibernateBinderLogger.setLevel(Level.INFO);

        Logger hibernateHbm2ddlLogger = loggerContext.getLogger("org.hibernate.tool.hbm2ddl");
        hibernateHbm2ddlLogger.setLevel(Level.INFO);

        Logger hibernateTransactionLogger = loggerContext.getLogger("org.hibernate.engine.transaction");
        hibernateTransactionLogger.setLevel(Level.INFO);

        Logger hibernateConnectionLogger = loggerContext.getLogger("org.hibernate.engine.jdbc.connections.internal");
        hibernateConnectionLogger.setLevel(Level.INFO);
    }
}
