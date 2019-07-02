package com.kitri.single.group.contorller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kitri.single.group.model.GroupDto;
import com.kitri.single.group.service.GroupService;
import com.kitri.single.user.model.UserDto;

@Controller
@RequestMapping("/group")
@SessionAttributes("userInfo")
public class GroupController {
	
	//서비스 부분
	@Autowired
	private GroupService groupService;
	
	//로그
	private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
	
	//첫페이지용
	@RequestMapping(method = RequestMethod.GET)
	public String grouplist(Model model, HttpSession session) {
		UserDto userInfo = (UserDto) session.getAttribute("userInfo");
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("page", "1");
		if(userInfo != null) {
			parameter.put("userId", userInfo.getUserId());
		}
		parameter.put("groupNum", null);
		parameter.put("key", null);
		parameter.put("word", null);
		
		List<GroupDto> list = groupService.getGroupList(parameter);
		int size = list.size();
		
		System.out.println(parameter);
		model.addAttribute("parameter", parameter);
		model.addAttribute("size", size);
		model.addAttribute("groupList", list);
		
		return "group/grouplist";
	}
	
	@RequestMapping(value = "/groupdetail", method = RequestMethod.GET)
	public @ResponseBody String groupDetail(@RequestParam(name = "groupNum") int groupNum){
		//String json = groupService.getGrou(parameter);
		//System.out.println(groupNum);
		String json = groupService.getGroupDetail(groupNum);
		//System.out.println(json);
		return json;
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public void groupCreate(GroupDto groupDto, @SessionAttribute("userInfo") UserDto userInfo) {
		
	}
}
