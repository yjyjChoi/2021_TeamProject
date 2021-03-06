package com.mju.groupware.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.mju.groupware.dto.Board;

@Service
@Repository
public class BoardDaoImpl implements BoardDao {
	@Autowired
	private SqlSessionTemplate sqlSession;

	@Override
	public void InsertBoardInfo(Board board) {
		sqlSession.insert("InsertBoard", board);

	}

	@Override
	public void InsertFile(Map<String, Object> map) {

		sqlSession.insert("InsertFile", map);

	}

	@Override
	public List<Board> SelectCommunityBoardList() {
		List<Board> Output = sqlSession.selectList("SelectCommunityBoardList");
		return Output;
	}

	@Override
	public void UpdateHitCount(String boardID) {
		sqlSession.update("UpdateHitCount", boardID);
	}

	@Override
	public void insertFile(Map<String, Object> map) {

		sqlSession.insert("InsertFile", map);
	}

	@Override
	public int SelectBoardID(Board board) {
		int Bno = sqlSession.selectOne("SelectBoardID", board);
		return Bno;
	}

}
