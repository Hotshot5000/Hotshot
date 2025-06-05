/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 9:36 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.db;

//import com.badlogic.gdx.sql.Database;
//import com.badlogic.gdx.sql.DatabaseFactory;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by sebas on 06.10.2015.
 */
public class DatabaseConnection {

    private static final String KEY_ID = "id";
    private static final String KEY_ERROR_TEXT = "error_text";
    private static final String KEY_DATE_ADDED = "date_added";
    private static final String KEY_SENT = "sent";
    private static final String KEY_VERSION = "version";
    private static final int DB_VERSION = 2;
    private static final String TABLE_VERSION = "Version";
    private static final String TABLE_EXCEPTIONS = "Exceptions";
    private static final DatabaseConnection databaseConnection = new DatabaseConnection();
//    private Connection connection;
//    Database versionTableHandler;
//    Database exceptionTableHandler;
    private String databaseFile;

    private interface Query {
        void execute(ResultSet rs) throws SQLException;

        void onEmptyResultSet();
    }

    private interface InsertQuery {
        void onKeysGenerated(ResultSet rs);
    }

    private DatabaseConnection() {
//        if (databaseConnection != null) {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
//        databaseConnection = this;
    }

    public void createConnection() {
//        File file = ENG_FileUtils.getFile(databaseFile, true);
//        if (!file.exists()) {
//            try {
//                ENG_FileUtils.createNewFile(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        String url = "jdbc:h2:" +
//                file.getPath() +
//                ";FILE_LOCK=FS" +
//                ";PAGE_SIZE=1024" +
//                ";CACHE_SIZE=8192";
//        try {
//            Class.forName("org.h2.Driver");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//
//            connection = DriverManager.getConnection(url);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }



        // Shit needs to be seriously fixed around here. No shits given about the porting from h2 to sqlite for now.
//        String versionTable = "CREATE TABLE IF NOT EXISTS " + TABLE_VERSION + "(" +
//                KEY_VERSION + " INT)";
//
//        versionTableHandler = DatabaseFactory.getNewDatabase(TABLE_VERSION,
//                1, versionTable, null);
//
//        versionTableHandler.setupDatabase();
//
////        execute(versionTable);
//
//        String exceptionTable = "CREATE TABLE IF NOT EXISTS " + TABLE_EXCEPTIONS + "(" +
//                KEY_ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
//                KEY_ERROR_TEXT + " VARCHAR(1048576), " +
//                KEY_DATE_ADDED + " TIMESTAMP AS CURRENT_TIMESTAMP, " +
//                KEY_SENT + " BOOLEAN NOT NULL DEFAULT FALSE)";
//
//        exceptionTableHandler = DatabaseFactory.getNewDatabase(TABLE_EXCEPTIONS,
//                1, exceptionTable, null);
//
//        exceptionTableHandler.setupDatabase();
//
////        execute(exceptionTable);
//
//        String versionQuery = "SELECT " + KEY_VERSION + " FROM " + TABLE_VERSION;
//
//        executeQuery(versionQuery, new Query() {
//            @Override
//            public void execute(ResultSet rs) throws SQLException {
//                int column = rs.findColumn(KEY_VERSION);
//                int version = rs.getInt(column);
//                if (version != DB_VERSION) {
//                    onUpgrade(version, DB_VERSION);
//                }
//            }
//
//            @Override
//            public void onEmptyResultSet() {
//                String insertVersion = "INSERT INTO " + TABLE_VERSION + " VALUES(" + DB_VERSION + ")";
//                DatabaseConnection.this.execute(insertVersion);
//            }
//        });


//        long id = addException("broken");
//        setExceptionSent(id);
//        ArrayList<ExceptionsSelectResult> exceptionsNotSent = getExceptionsNotSent();
//        closeConnection();
    }

    public long addException(String s) {
        String insertException = "INSERT INTO " + TABLE_EXCEPTIONS + "(" + KEY_ERROR_TEXT + ") VALUES('" + s + "')";
        final long[] id = new long[1];
        execute(insertException, Statement.RETURN_GENERATED_KEYS, rs -> {
            try {
                if (rs.next()) {
                    id[0] = rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return id[0];
    }

    public void setExceptionSent(long id) {
        String updateException = "UPDATE " + TABLE_EXCEPTIONS + " SET " + KEY_SENT + "=" + "TRUE WHERE " + KEY_ID + "=" + id;
        execute(updateException);
    }

    public static class ExceptionsSelectResult {
        public long id;
        public String exception;

        public ExceptionsSelectResult() {

        }

        public ExceptionsSelectResult(long id, String exception) {
            this.id = id;
            this.exception = exception;
        }
    }

    public ArrayList<ExceptionsSelectResult> getExceptionsNotSent() {
        String query = "SELECT " + KEY_ERROR_TEXT + ", " + KEY_ID + " FROM " + TABLE_EXCEPTIONS
                + " WHERE " + KEY_SENT + "=FALSE " + " ORDER BY " + KEY_DATE_ADDED + " DESC";
        final ArrayList<ExceptionsSelectResult> errorTextList = new ArrayList<>();
        executeQuery(query, new Query() {
            @Override
            public void execute(ResultSet rs) throws SQLException {
                int idColumn = rs.findColumn(KEY_ID);
                int errorTextColumn = rs.findColumn(KEY_ERROR_TEXT);
                while (rs.next()) {
                    String string = rs.getString(errorTextColumn);
                    int id = rs.getInt(idColumn);
                    errorTextList.add(new ExceptionsSelectResult(id, string));
                }
            }

            @Override
            public void onEmptyResultSet() {

            }
        });
        return errorTextList;
    }

    private void onUpgrade(int oldVersion, int newVersion) {
        String updateVersion = "UPDATE " + TABLE_VERSION + " SET " + KEY_VERSION + "=" + DB_VERSION;
        execute(updateVersion);
    }

    private void executeQuery(String queryStr, Query query) {
//        try {
//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery(queryStr);
//            if (!resultSet.isBeforeFirst()) {
//                query.onEmptyResultSet();
//            }
//            while (resultSet.next()) {
//                query.execute(resultSet);
//            }
//            resultSet.close();
//            statement.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    private void execute(String query) {
        execute(query, Statement.NO_GENERATED_KEYS, null);
    }

    private void execute(String query, int autoGeneratedKeys, InsertQuery insertQuery) {
//        try {
//            Statement versionStmt = connection.createStatement();
//            versionStmt.execute(query, autoGeneratedKeys);
//            if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {
//                ResultSet generatedKeys = versionStmt.getGeneratedKeys();
//                insertQuery.onKeysGenerated(generatedKeys);
//            }
//            versionStmt.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public void closeConnection() {
//        try {
//            connection.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public String getDatabaseFile() {
        return databaseFile;
    }

    public void setDatabaseFile(String databaseFile) {
        this.databaseFile = databaseFile;
    }

    public static DatabaseConnection getConnection() {
        return databaseConnection;
    }
}
