package com.urline.dao;

import com.urline.rowMapper.CommentRowMapper;
import com.urline.template.JdbcTemplate;
import com.urline.vo.CommentVO;

import java.util.List;

public class CommentDAO {
    private static CommentDAO instance;
    private JdbcTemplate jdbcTemplate = null;

    public CommentDAO() {
        this.jdbcTemplate = new JdbcTemplate();
    }

    public static CommentDAO getInstance() {
        if (instance == null)
            instance = new CommentDAO();
        return instance;
    }

    public void insertComment(String sql, Object... args) throws Exception {
        jdbcTemplate.update(sql, args);
    }

    public void deleteComment(String sql, Object... args) throws Exception {
        jdbcTemplate.update(sql, args);
    }

    public void updateComment(String sql, Object... args) throws Exception {
        jdbcTemplate.update(sql, args);
    }

    public CommentVO selectOneComment(String sql, Object... args) throws Exception {
        CommentRowMapper urm = new CommentRowMapper();
        return (CommentVO) jdbcTemplate.queryForObject(sql, urm, args);
    }

    public List<CommentVO> selectAllComment(String sql, Object... args) throws Exception {
        CommentRowMapper urm = new CommentRowMapper();
        return jdbcTemplate.query(sql, urm, args);
    }


}
