package de.zalando.aruha.nakadi.repository.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.zalando.aruha.nakadi.config.JsonConfig;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractDbRepositoryTest {

    protected JdbcTemplate template;
    protected Connection connection;
    protected ObjectMapper mapper;
    protected String repositoryTable;

    private static final String postgresqlUrl = "jdbc:postgresql://localhost:5432/local_nakadi_db";
    private static final String username = "nakadi_app";
    private static final String password = "nakadi";

    public AbstractDbRepositoryTest(final String repositoryTable) {
        this.repositoryTable = repositoryTable;
    }

    @Before
    public void setUp() {
        try {
            mapper = (new JsonConfig()).jacksonObjectMapper();
            final DataSource datasource = new DriverManagerDataSource(postgresqlUrl, username, password);
            template = new JdbcTemplate(datasource);
            connection = datasource.getConnection();
            clearRepositoryTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws SQLException {
        clearRepositoryTable();
        connection.close();
    }

    private void clearRepositoryTable() {
        template.execute("DELETE FROM " + repositoryTable);
    }

}
