package com.urline.template;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.swing.plaf.synth.SynthSeparatorUI;

public class JdbcTemplate {
    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection makeConn() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521/XE", "test", "test");
        return conn;
    }

    public int update(String sql, Object... args) throws Exception {
        int rc = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // 커넥션 연결
            conn = makeConn();

            // 자동 커밋을 false로 한다.
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(sql);

            makingStatement(stmt, args);
            rc = stmt.executeUpdate();

            conn.commit();
        } catch (ClassNotFoundException | NamingException | SQLException sqle){
            conn.rollback();
            throw new RuntimeException(sqle.getMessage());
        } finally {
            // Connection, PreparedStatement를 닫는다.
            closer(conn, stmt);
        }
        return rc;
    }

    public static void closer(Connection conn, PreparedStatement stmt) {
        try{
            if ( stmt != null ){ stmt.close(); stmt=null; }
            if ( conn != null ){ conn.close(); conn=null; }
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private void makingStatement(PreparedStatement stmt, Object[] args) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                stmt.setObject(i + 1, null);
            } else if (args[i] instanceof Integer) {
                stmt.setInt(i + 1, ((Integer) args[i]).intValue());
            } else if (args[i] instanceof Double) {
                stmt.setDouble(i + 1, ((Double) args[i]).doubleValue());
            } else if (args[i] instanceof String) {
                stmt.setString(i + 1, (String) args[i]);
            }
        }
    }

    public <T extends Object> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws Exception {
        T vo = null;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = makeConn();
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                System.out.println(args[i]);
            }
            makingStatement(stmt, args);
            rs = stmt.executeQuery();

            if (rs.next()) {
                vo = rowMapper.mapRow(rs);
                if (rs.next()) {
                    throw new Exception("many records");
                }
            } else {
                throw new Exception("no record");
            }
            return vo;
        } catch (Exception e) {
            throw e;
        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
        }
    }

    public <T extends Object> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) throws Exception {
        List<T> rl = new ArrayList<T>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = makeConn();
            stmt = conn.prepareStatement(sql);

            makingStatement(stmt, args);

            rs = stmt.executeQuery();
            while (rs.next()) {
                T vo = rowMapper.mapRow(rs);
                rl.add(vo);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
        }
        return rl;
    }

}
