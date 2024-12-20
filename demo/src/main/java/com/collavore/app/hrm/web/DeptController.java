package com.collavore.app.hrm.web;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.collavore.app.hrm.service.DeptService;
import com.collavore.app.hrm.service.HrmVO;
import com.collavore.app.hrm.service.MemberService;
import com.collavore.app.hrm.service.PosiService;
import com.collavore.app.service.HomeService;
import com.collavore.app.service.HomeVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DeptController {

	private final DeptService deptService;
	private final MemberService memberService;
	private final PosiService posiService;
	private final HomeService homeService;
	private static final Logger logger = LoggerFactory.getLogger(DeptController.class);

	@ModelAttribute
	public void addAttributes(Model model, HttpSession session) {
		List<HomeVO> employeesInfo = homeService.empList();
		model.addAttribute("employees", employeesInfo);
		
		String userAdmin = (String) session.getAttribute("userAdmin");
		model.addAttribute("userAdmin", userAdmin);

		@SuppressWarnings("unchecked")
		List<String> menuAuth = (List<String>) session.getAttribute("menuAuth");
		model.addAttribute("menuAuth", menuAuth);
		
		model.addAttribute("sidemenu", "member_sidebar");
	}

	// 부서 등록
	@PostMapping("/dept/register")
	@ResponseBody
	public String registerDept(@RequestBody HrmVO dept) {
		try {
			logger.info("Registering new department: {}", dept);
			int result = deptService.insertDept(dept);
			return result > 0 ? "success" : "failure";
		} catch (Exception e) {
			logger.error("Error registering department: {}", dept, e);
			return "error";
		}
	}

	// 부서 수정
	@PostMapping("/dept/update")
	@ResponseBody
	public String updateDept(@RequestBody HrmVO dept) {
		try {
			logger.info("Updating department: {}", dept);
			int result = deptService.updateDept(dept);
			return result > 0 ? "success" : "failure";
		} catch (Exception e) {
			logger.error("Error updating department: {}", dept, e);
			return "error";
		}
	}

	// 부서 추가 및 수정
	@PostMapping("/dept/save")
	@ResponseBody
	public String saveDepts(@RequestBody List<HrmVO> deptList) {

		try {
			int result = 0;
			for (HrmVO hrmVO : deptList) {
				if (hrmVO.getDeptNo() != null) {
					logger.info("Updating department: {}", hrmVO);
					result += deptService.updateDept(hrmVO);
				} else {
					logger.info("Inserting new department: {}", hrmVO);
					result += deptService.insertDept(hrmVO);
				}
			}
			logger.info("Save operation result: {}", result);
			return result > 0 ? "success" : "failure";
		} catch (Exception e) {
			logger.error("Error during department save operation", e);
			return "error";
		}
	}

	// 부서별 사원 목록 조회
	@GetMapping("/employees/byDept/{deptNo}")
	@ResponseBody
	public Map<String, Object> getEmployeesByDept(@PathVariable Integer deptNo) {
		List<HrmVO> deptEmpList = deptService.getEmployeesByDept(deptNo);
		HrmVO deptMgrInfo = deptService.getMgrByDept(deptNo);
		// null 처리
		deptEmpList = (deptEmpList != null) ? deptEmpList : Collections.emptyList();
		deptMgrInfo = (deptMgrInfo != null) ? deptMgrInfo : new HrmVO(); // 기본 생성자 사용 또는 null 반환이 필요한 경우 그대로 유지

		return Map.of("deptEmpList", deptEmpList, "deptMgrInfo", deptMgrInfo);
	}

	// 부서장 업데이트
	@PostMapping("/departments/updateManager")
	@ResponseBody
	public String updateManager(@RequestBody Map<String, Integer> payload) {
		Integer deptNo = payload.get("deptNo");
		Integer empNo = payload.get("empNo");

		int result = deptService.updateManager(deptNo, empNo);
		return result > 0 ? "success" : "failure";
	}

	// 부서 삭제
	@DeleteMapping("/dept/delete/{deptNo}")
	@ResponseBody
	public String deleteDepts(@PathVariable Integer deptNo) {
		logger.info("Attempting to delete department with deptNo: {}", deptNo);

		// 부서에 사원이 있는지 확인하여 삭제 불가 메시지 반환
		if (deptService.hasEmployeesInDept(deptNo)) {
			logger.warn("Cannot delete department {}: Employees are assigned to this department", deptNo);
			return "cannot_delete"; // 사원이 있으면 삭제 불가 응답
		}

		try {
			int result = deptService.deleteDept(deptNo);
			if (result > 0) {
				logger.info("Successfully deleted department with deptNo: {}", deptNo);
				return "success";
			} else {
				logger.warn("Failed to delete department with deptNo: {}", deptNo);
				return "failure";
			}
		} catch (Exception e) {
			logger.error("Error occurred while deleting department with deptNo: {}", deptNo, e);
			return "error";
		}
	}

	// 조직도 페이지 이동
	@GetMapping("/deptManager")
	public String organizationChart(Model model) {
		List<HrmVO> deptList = deptService.getExistingDepts(); // 부서 목록 조회
		List<HrmVO> memberList = memberService.getAllMembers(); // 사원 목록 조회

		// 각 부서에 속한 사원 및 직위 정보 추가
		Map<Integer, List<HrmVO>> memberGroupedByDept = memberList.stream()
				.collect(Collectors.groupingBy(HrmVO::getDeptNo)); // 사원을 부서별로 그룹화

		model.addAttribute("deptList", deptList);
		model.addAttribute("memberGroupedByDept", memberGroupedByDept);

		return "member/deptManager"; // 조직도 템플릿 반환
	}

	// 조직도 데이터를 계층 구조로 반환
	@GetMapping("/api/deptManager")
	@ResponseBody
	public Map<String, Object> getOrganizationChartData() {
		List<HrmVO> deptList = deptService.getExistingDepts(); // 부서 목록
		List<HrmVO> memberList = memberService.getAllMembers(); // 사원 목록
		List<HrmVO> posiList = posiService.getExistingPositions(); // 직위 목록
		// 각 부서에 속한 사원 및 직위 정보
		Map<Integer, List<HrmVO>> memberGroupedByDept = memberList.stream()
				.collect(Collectors.groupingBy(HrmVO::getDeptNo));

		// 결과 데이터를 JSON 형태로 변환하여 반환
		return Map.of("departments", deptList, "membersByDept", memberGroupedByDept, "positions", posiList);
	}

	// 부서정보관련

	// 페이지이동
	@GetMapping("/deptMember")
	public String webdeptManager() {
		return "member/deptMember";
	}

	// 부서와 부서장 목록 조회
	@GetMapping("/api/deptMgr")
	@ResponseBody
    public List<HrmVO> getDepartmentsWithManager() {
    	List<HrmVO> deptMgrs = deptService.getMgrList();
        return deptMgrs;
    }

	// 부서에 속한 사원 조회
	@GetMapping("/api/deptMember")
	@ResponseBody
    public List<HrmVO> getEmployeesByDepartment(@RequestParam Integer deptNo) {
    	List<HrmVO> deptMembers = deptService.getMemberList(deptNo);
        return deptMembers;
    }

}