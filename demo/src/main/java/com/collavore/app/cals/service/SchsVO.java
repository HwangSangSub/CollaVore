package com.collavore.app.cals.service;


import java.sql.Timestamp;

import org.springframework.format.annotation.DateTimeFormat;

import groovy.transform.builder.Builder;
import lombok.Data;

@Data

@Builder
public class SchsVO {
	private Integer schNo; // 일정번호 
	private String title; // 제목 
	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
	private Timestamp   startDate; // 시작날짜 
	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
	private Timestamp   endDate; // 종료날짜 
	private Integer calNo; //캘린더 넘버 
	private String isAlarm; //알림여부 ('Y' or 'N')
	private Integer type; // 캘린더 타입 (1: 개인, 2: 공유, 3: 프로젝트)
	
}