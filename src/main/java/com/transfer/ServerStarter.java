package com.transfer;

import com.transfer.config.PropertyManager;
import com.transfer.controller.AccountController;
import com.transfer.controller.MdcFilter;
import com.transfer.controller.TransactionController;
import com.transfer.dao.*;
import com.transfer.service.AccountService;
import com.transfer.service.AccountServiceImpl;
import com.transfer.service.TransactionService;
import com.transfer.service.TransactionServiceImpl;
import com.transfer.service.transformer.Transformer;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

import static com.transfer.config.Property.*;

@Slf4j
public class ServerStarter {

    private final PropertyManager propertyManager;
    private final Transformer transformer;
    private final Server server;
    private final DbManager dbManager;
    private final DataSourceManager dataSourceManager;

    public ServerStarter(PropertyManager propertyManager, Transformer transformer) {
        this.propertyManager = propertyManager;
        this.transformer = transformer;
        this.server = new Server();
        this.dataSourceManager = new DataSourceManagerImpl(propertyManager);
        this.dbManager = new DbManager(dataSourceManager.getDataSource());
    }

    public void start() {
        log.info("Preparing DB");
        dbManager.prepareDb();
        log.info("DB started");

        log.info("Starting jetty server");
        try {
            int port = propertyManager.getInt(APP_PORT);
            Connector connector = createHttpConnector(server, port);
            server.addConnector(connector);
            log.info("Jetty connected on port {}", port);

            ServletContextHandler context = new ServletContextHandler();
            context.setContextPath(propertyManager.getString(APP_CONTEXT_PATH));
            context.setClassLoader(Thread.currentThread().getContextClassLoader());
            server.setHandler(context);

            AccountDao accountDao = new AccountDaoImpl(dataSourceManager.getDataSource());
            TransactionDao transactionDao = new TransactionDaoImpl(dataSourceManager.getDataSource());
            AccountService accountService = new AccountServiceImpl(accountDao);
            TransactionService transactionService = new TransactionServiceImpl(accountDao, transactionDao);

            log.info("Creating jetty servlets...");
            ServletHolder createAccountController = new ServletHolder("AccountController", new AccountController(transformer, accountService));
            createAccountController.setAsyncSupported(true);
            createAccountController.setEnabled(true);
            context.addServlet(createAccountController, "/account");

            ServletHolder createTransactionController = new ServletHolder("TransactionController", new TransactionController(transformer, transactionService));
            createTransactionController.setAsyncSupported(true);
            createTransactionController.setEnabled(true);
            context.addServlet(createTransactionController, "/transaction");

            context.addFilter(MdcFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

            log.info("Starting jetty...");
            server.start();
        } catch (Exception e) {
            log.error("Failed to start jetty server", e);
        } finally {
            log.info("Starting jetty server finished");
        }
    }

    public void stop() {
        if (server.isStarted() || server.isStarting()) {
            try {
                server.stop();
            } catch (Exception e) {
                log.error("Unable to stop server", e);
            }
        }
    }

    private Connector createHttpConnector(Server server, int port) {
        log.info("Configuring HTTP connector");

        ServerConnector connector = new ServerConnector(server);
        connector.setIdleTimeout(propertyManager.getInt(APP_TIMEOUT));
        connector.setPort(port);
        return connector;
    }

}
