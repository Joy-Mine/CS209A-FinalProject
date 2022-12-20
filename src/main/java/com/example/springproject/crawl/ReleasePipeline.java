package com.example.springproject.crawl;

import com.example.springproject.domain.Contributor;
//import com.example.springproject.domain.OpenIssue;
import com.example.springproject.domain.Release;
import org.assertj.core.internal.bytebuddy.implementation.bind.annotation.Pipe;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReleasePipeline implements Pipeline {
    public static Connection connection = null;
    public static PreparedStatement release_statement = null;
    public Lock lock = new ReentrantLock();

    public static void openDB(String database) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.err.println("Can't find Mysql Driver, please check the path");
            System.exit(1);
        }

        try {
            connection = Druid.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        try {
            release_statement = connection.prepareStatement("insert ignore into releases values(?,?,?,?)");

        } catch (SQLException e) {
            System.err.println("Insert Statement fail");
            System.err.println(e.getMessage());
            closeDB();
            System.exit(1);
        }
    }

    public static void closeDB() throws SQLException {
        if (connection != null) {
            if (release_statement != null)
                release_statement.close();
            Druid.closeAll(connection);
        }
    }

    public static void loadRelease(Release release) throws SQLException {
        if (connection != null) {
            release_statement.setString(1, release.getAssets_url());
            release_statement.setDate(2, new java.sql.Date(release.getCreated_at().getTime()));
            release_statement.setDate(3, new java.sql.Date(release.getPublished_at().getTime()));
            release_statement.setString(4, release.getName());
            release_statement.addBatch();
        }
    }

    @Override
    public void process(ResultItems resultItems, Task task){
        lock.lock();
        try {
            openDB("repo");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        List<Release> releaseList = resultItems.get("releases");
        releaseList.stream().forEach(o-> {
            try {
                loadRelease(o);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            release_statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            closeDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        lock.unlock();
    }

    public static void commit() throws SQLException {
        try {
            connection.commit();
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
            try {
                connection.rollback();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            closeDB();
            System.exit(1);
        }
    }
}
