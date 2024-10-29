package com.collavore.app.board.service;

import java.sql.Date;

import lombok.Data;

@Data
public class BodsCfigVO {
	private Integer boardNo; //게시판번호
	private String name; //게시판명
	private String subject; //말머리
	private Integer readAuth; //게시글 읽기 권한
	private Integer writeAuth; //게시글 쓰기 권한
	private Integer commentAuth; //게시글 댓글 권한
	private Date regDate; //등록일 
	private Date modDate; //수정일 
}
