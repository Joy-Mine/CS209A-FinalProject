package com.example.springproject.crawl;

import com.example.springproject.domain.Contributor;
import org.assertj.core.internal.bytebuddy.implementation.bind.annotation.Pipe;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContributorPipeline implements Pipeline {
    public static Connection connection = null;
    public static PreparedStatement contributor_statement = null;
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
            contributor_statement = connection.prepareStatement("insert ignore into contributor values(?,?,?,?,?)");
            System.out.println("============Trying to insert===============");

        } catch (SQLException e) {
            System.err.println("Insert Statement fail");
            System.err.println(e.getMessage());
            closeDB();
            System.exit(1);
        }
    }

    public static void closeDB() throws SQLException {
        if (connection != null) {
            if (contributor_statement != null)
                contributor_statement.close();
            Druid.closeAll(connection);
        }
    }

    public static void loadContributor(Contributor contributor) throws SQLException {
        if (connection != null) {
            contributor_statement.setLong(1, contributor.getId());
            contributor_statement.setString(2, contributor.getLogin());
            contributor_statement.setLong(3, contributor.getContributions());
            contributor_statement.setString(4, contributor.getAvatar_url());
            contributor_statement.setString(5, contributor.getHtml_url());
            contributor_statement.addBatch();
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
        List<Contributor> contributorList = resultItems.get("contributor");
       contributorList.stream().forEach(o-> {
           try {
               loadContributor(o);
           } catch (SQLException e) {
               throw new RuntimeException(e);
           }
       });
        try {
            contributor_statement.executeBatch();
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
