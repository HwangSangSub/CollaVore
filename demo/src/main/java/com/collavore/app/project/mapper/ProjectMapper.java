package com.collavore.app.project.mapper;

import java.util.List;
import java.util.Map;

import com.collavore.app.project.service.ProjectFilesVO;
import com.collavore.app.project.service.ProjectFoldersVO;
import com.collavore.app.project.service.ProjectVO;

public interface ProjectMapper {
	
	//프로젝트 조회
	public List<ProjectVO> selectProjectAll();
	// 프로젝트 생성
	public int ProjectInsert(ProjectVO projectVO);
	// 프로젝트 삭제
	public int projectDelete(int projNo);
	// 프로젝트 수정
	public Map<String, Object> updateProject(ProjectVO projectVO);
	// 프로젝트 단건조회
	public ProjectVO selectProjectById(int projNo);
	
	public List<ProjectVO> findProjects(int offset, int size);
	
	public long countAllProjects();
	
	// 프로젝트 폴더 리스트
	public List<ProjectFoldersVO> selectfolderAll();
	// 프로젝트 파일 리스트
	public List<ProjectFilesVO> selectfileAll(int pfNo);
	
	public int fileinsert(ProjectFilesVO projectFilesVO);
	
	public ProjectFilesVO filedetail(Long projFileNo);
	
	public List<ProjectVO> projecttree();
	
	// 프로젝트 업무 생성
	public int ProjectwrkInsert(ProjectVO projectVO);
	// 프로젝트 상세업무 생성
	public int ProjectdwrkInsert(ProjectVO projectVO);
	
	// pwNo 조회하기
	public int searchPwNo(int pdwNo);
	
	// 업무 단건 조회
	public ProjectVO selectwrkInfo(int pwNo);
	// 상세업무 조회
	public ProjectVO selectdwrkInfo(int pdwNo);
	
}
