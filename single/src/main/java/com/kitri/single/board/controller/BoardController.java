package com.kitri.single.board.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kitri.single.board.dao.BoardDao;
import com.kitri.single.board.model.BoardDto;
import com.kitri.single.board.model.BoardPageDto;
import com.kitri.single.board.model.ReplyDto;
import com.kitri.single.board.service.BoardService;
import com.kitri.single.board.service.ReplyService;
import com.kitri.single.common.service.CommonService;
import com.kitri.single.user.model.UserDto;
import com.sun.org.apache.xpath.internal.operations.Mod;

@Controller
@RequestMapping("/board")
@SessionAttributes("userInfo")
public class BoardController {
	
	//로그
	//private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private BoardService boardService;
	
	@Autowired
	private ReplyService replyService;
	
	@Autowired
	private CommonService commonService;

	
	// 로그인 페이지로 가라.
	public String loginCheck(HttpSession session, String path) {
		UserDto userDto = (UserDto)session.getAttribute("userInfo");
		if (userDto == null) {
			path = "board/arror/gologin";
		}
		return path;
	}
	// 싱글 메인
	@RequestMapping("singlemain")
	public String singleMain(HttpSession session,Model model){
		//select를 3번해와야뎀.
		
		List<BoardDto> boardDtoL = new ArrayList<BoardDto>();
		
		//이달의 자취왕
		List<UserDto> userList = boardService.rankingUser();
		
		Map<String, List<BoardDto>> map = new HashMap<String, List<BoardDto>>();
		
		for (int i = 0; i < userList.size(); i++) {
			String userId = userList.get(i).getUserId();
			boardDtoL = boardService.rankingboard(userId);
			map.put(userId, boardDtoL);
		}

		model.addAttribute("userList", userList);
		model.addAttribute("map", map);
		
		
		//이주의 추천순
		List<BoardDto> boardDtoList = boardService.monthList();
		model.addAttribute("weekList", boardDtoList);
		
		String path = "board/singlemain";
		
		//카테고리 최신글와 유저
		categoryNU(model,session);
		
		
		return path;
		
	}
	
	// 단순 글쓰기 ajax
	@RequestMapping(value="/answerwritepage")
	public String answerok(HttpSession session){
		
		String path = "board/write/answerwrite";
		
		path = loginCheck(session, path);
		
		return path;
	}
	// 자취생활 페이지로 이동
	@RequestMapping(value="/singlelifeboard")
	public String singlelifeboard(Model model, HttpSession session){
		List<BoardDto> boardDtoL = new ArrayList<BoardDto>();
		
		//이달의 자취왕
		List<UserDto> userList = boardService.rankingUser();
		
		Map<String, List<BoardDto>> map = new HashMap<String, List<BoardDto>>();
		
		for (int i = 0; i < userList.size(); i++) {
			String userId = userList.get(i).getUserId();
			boardDtoL = boardService.rankingboard(userId);
			map.put(userId, boardDtoL);
		}

		model.addAttribute("userList", userList);
		model.addAttribute("map", map);
		
		//카테고리 최신글와 유저
		categoryNU(model,session);
		
		return "board/singlelifeboard";
	}
	// 요리 레시피 페이지로 이동
	@RequestMapping(value="/singlecookboard")
	public String singlecookboard(Model model, HttpSession session){
		List<BoardDto> boardDtoL = new ArrayList<BoardDto>();
		
		//이달의 자취왕
		List<UserDto> userList = boardService.rankingUser();
		
		Map<String, List<BoardDto>> map = new HashMap<String, List<BoardDto>>();
		
		for (int i = 0; i < userList.size(); i++) {
			String userId = userList.get(i).getUserId();
			boardDtoL = boardService.rankingboard(userId);
			map.put(userId, boardDtoL);
		}

		model.addAttribute("userList", userList);
		model.addAttribute("map", map);
		
		//카테고리 최신글와 유저
		categoryNU(model,session);
		
		return "board/singlecookboard";
	}
	// 글 작성후 자취 or 요리 페이지로 이동
	@RequestMapping(value="/list")
	public String list(@RequestParam Map<String, String> parameter, 
			Model model, HttpSession session){
		
		List<BoardDto> boardDtoL = new ArrayList<BoardDto>();
		
		//이달의 자취왕
		List<UserDto> userList = boardService.rankingUser();
		
		Map<String, List<BoardDto>> map = new HashMap<String, List<BoardDto>>();
		
		for (int i = 0; i < userList.size(); i++) {
			String userId = userList.get(i).getUserId();
			boardDtoL = boardService.rankingboard(userId);
			map.put(userId, boardDtoL);
		}

		model.addAttribute("userList", userList);
		model.addAttribute("map", map);
		
		int boardListNum = Integer.parseInt(parameter.get("boardListNum")); 
		
		String path = "";

		if (boardListNum != 0) {
			if (boardListNum == 1) {
				path = "board/singlelifeboard";
			}else if (boardListNum == 2) {
				path = "board/singlecookboard";
			}
		}else {
			path = "redirect:/index.jsp";
		}
		
		//카테고리 최신글와 유저
		categoryNU(model,session);
				
		return path;
	}
	
	
	
	
	// write 페이지 이동
	@RequestMapping(value="/write",method = RequestMethod.GET)
	public String write(@ModelAttribute("userInfo") UserDto userInfo, 
			@RequestParam("boardListNum") int boardListNum, Model model,
			HttpSession session){

		BoardPageDto bp = new BoardPageDto();
		bp.setBoardListNum(boardListNum);
		
		model.addAttribute("bp", bp);
		
		String path = "board/write";
		
		//카테고리 최신글와 유저
		categoryNU(model,session);

		return path;
	}
	
	
	
	
	@RequestMapping(value="/write",method = RequestMethod.POST)
	public String write(@RequestParam("tags") String tags,
			@RequestParam Map<String, String> parameter,
			Model model, HttpSession session){
		
		String path = "";
		
		BoardDto boardDto = new BoardDto();
		UserDto userDto = (UserDto)session.getAttribute("userInfo");
		
		if (userDto != null) {
			// 2번 적용됨?? - 오라클에 있는 유저랑 인클루드해서 넣어둔 유저랑 겹처서 그런가?

			//int boardNum = commonService.getNextSeq();
			
			//boardDto.setBoardNum(boardNum);
			boardDto.setBoardListNum(Integer.parseInt(parameter.get("boardListNum")));
			boardDto.setUserId(userDto.getUserId());
			boardDto.setUserNickname(userDto.getUserNickname());
			
			
			//boardDto.setBoardSubject(parameter.get("boardSubject"));
			//스크립트 방지해주기
			String boardSubject = parameter.get("boardSubject");
			boardSubject = boardSubject.replaceAll("<", "&lt;");
			boardDto.setBoardSubject(boardSubject);
			
			boardDto.setBoardContent(parameter.get("boardContent"));
			boardDto.setBoardViews(0);
			boardDto.setBoardLike(0);
			
			// 해시태그 dto에 넣어주기.
			String[] hashtags = tags.split("#");
			List<String> hashtagList = new ArrayList<>();
			
			for(int i=1 ; i<hashtags.length ; i++) {
				
				hashtagList.add(hashtags[i]);
				
			}

			boardDto.setHashtagList(hashtagList);
			

			
			// 결과값 반환
			int boardNum = boardService.writeArticle(boardDto);
			
			if (boardNum != 0) {
				model.addAttribute("boardNum",boardNum);
				path = "board/write/writeok";
			} else {
				path = "board/write/writefail";
			}
			
		}else {
			path = "board/write/writefail";
		}
		
		model.addAttribute("parameter",parameter);
		
		return path;
		
	}
	
	
	// 글 세부
	@RequestMapping(value="/view",method = RequestMethod.GET)
	public String view(@RequestParam Map<String, String> parameter, 
			Model model, HttpSession session) { 
		
		// 바로 접속하려 할때.
		if(parameter == null || parameter.get("boardNum") == null) {
			return "redirect:/index.jsp";
		}
		
		int boardNum = Integer.parseInt(parameter.get("boardNum"));
		
		// if문으로 로그인 했는지 안했는지 체크하기
		BoardDto boardDto = boardService.viewArticle(boardNum);
		//System.out.println(boardDto);
		
		UserDto userDto = (UserDto)session.getAttribute("userInfo");
		model.addAttribute("session", userDto );
		
		model.addAttribute("article", boardDto);
		
		// ????
		model.addAttribute("parameter", parameter);
		
		String path = "board/singleview";
		
		
		//카테고리 최신글와 유저
		categoryNU(model,session);
		
		
		return path;

	}
	
	
	
	
	// 전체 페이징 처리
	@RequestMapping(method = RequestMethod.GET)
	public String boardList(@RequestParam Map<String, Object> params, Model model) {
		
		int currentPage = Integer.parseInt((String)params.get("page"));
		// boardListNum로 나눔.
		int boardListNum = Integer.parseInt((String)params.get("boardListNum"));
		String key = (String)params.get("key");
		String word = (String)params.get("word");
		
		BoardPageDto bp = boardService.selectBoardList(currentPage, boardListNum, key, word);
		
		model.addAttribute("bp", bp);
		
		String path = "";
		
		if (boardListNum == 0) {
			path = "board/boardlist/newlistok";
		} else {
			path = "board/boardlist/commonlist";
		}
		model.addAttribute("root", servletContext.getContextPath());
		
		return path;
	}
	
	
	
	
	
	// 답변 글들 보여주기.
	@RequestMapping("/answerview")
	public String answerView(@RequestParam("boardNum") int boardNum, Model model,
			HttpSession session) {
		
		List<ReplyDto> answerList = new ArrayList<ReplyDto>();
		UserDto userDto = (UserDto)session.getAttribute("userInfo");
		
		if (userDto != null) {
			model.addAttribute("userInfo", userDto);
		}
		
		BoardDto boardDto = new BoardDto();
		boardDto.setBoardNum(boardNum);
		
		answerList = replyService.answerView(boardDto);
		
		model.addAttribute("answerList", answerList);
		
		String path = "board/write/answerview";
		
		return path;
	}
	
	// 답변 쓰기.
	@RequestMapping("/answerwrite")
	public @ResponseBody String answerwrite(@RequestParam Map<String, Object> params
			, Model model, HttpSession session) {
		
		ReplyDto replyDto = new ReplyDto();
		UserDto userDto = (UserDto)session.getAttribute("userInfo");
		
		String userId = userDto.getUserId();
		String userNickname = userDto.getUserNickname();
		
		// 띄어쓰기, 스크립트 방지
		String replyContent = (String)params.get("replyContent");
		replyContent = replyContent.replaceAll("<", "&lt;");
		replyContent = replyContent.replaceAll("\n", "<br>"); 
		
		int boardNum = Integer.parseInt((String)params.get("boardNum"));
		
		replyDto.setUserId(userId);
		replyDto.setUserNickname(userNickname);
		replyDto.setReplyContent(replyContent);
		replyDto.setBoardNum(boardNum);
		
		//List<ReplyDto> list = replyService.answerview(replyDto);
		
		//model.addAttribute("list", list);
		int seq = replyService.answerInsert(replyDto);
		
		JSONObject jsonObject = new JSONObject();
		
		if (seq == 0) {
			jsonObject.put("resultCode", 0);
			//jsonObject.put("resultData", "시스템 에러");
		}else {
			jsonObject.put("resultCode", 1);
			//jsonObject.put("resultData", "성공");
		}
		
		
		return jsonObject.toString();
	}
	
	// 답변 삭제
	@RequestMapping(value ="/delete")//, method = RequestMethod.DELETE
	public @ResponseBody String answerDelete(@RequestParam("replyNum") int replyNum
			, Model model, HttpSession session) {
		
		UserDto userDto = (UserDto)session.getAttribute("userInfo");
		JSONObject jsonObject = new JSONObject();
		
		if (userDto != null) {
			int seq = replyService.answerDelete(replyNum);
			
			if (seq == 0) {
				jsonObject.put("resultCode", 0);
			}else {
				jsonObject.put("resultCode", 1);
			}
		} else {
			jsonObject.put("resultCode", 2);
		}
		
		
		return jsonObject.toString();
		
	}
	
	
	// 좋아요
	@RequestMapping(value = "/like", method = RequestMethod.POST)
	public @ResponseBody String like(@RequestBody Map<String, Integer> mapBN
			, Model model, HttpSession session) {
		
		UserDto userDto = (UserDto)session.getAttribute("userInfo");
		JSONObject jsonObject = new JSONObject();
		
		int boardNum = mapBN.get("boardNum");
		
		if (userDto != null) {
			
			String userId = userDto.getUserId();

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", userId);
			map.put("boardNum", boardNum);
			
			
			int seq = replyService.like(map);
			
			if (seq == 0) {
				// 서버 오류로 좋아요가 실패했습니다. 다시 시도해 주세요.
				jsonObject.put("resultCode", 2);
			} else {
				jsonObject.put("resultCode", 1);
			}
			
		} else {
			
			// 로그인 해달라고 모달창 띄워주기
			jsonObject.put("resultCode", 0);
			
		}
		
		
		return jsonObject.toString();
	}
	
	
	
	// 글 삭제
	@RequestMapping("boardDelete")
	public @ResponseBody String boardDelete(@RequestParam("boardNum") int boardNum,
			HttpSession session) {
		
		
		UserDto userDto = (UserDto)session.getAttribute("userInfo");
		JSONObject jsonObject = new JSONObject();
		
		if (userDto != null) { 
			
			int seq = boardService.boardDelete(boardNum);
			
			if (seq == 0) {
				// 업데이트 실패
				jsonObject.put("resultCode", 2);
			} else {
				// 업데이트 성공
				jsonObject.put("resultCode", 1);
			}
		
		} else {
			// 로그인을 해주세요
			jsonObject.put("resultCode", 3);
		}
		
		
		return jsonObject.toString();
	};
	
	
	// 카테고리 뉴스와 유저
	public void categoryNU(Model model,HttpSession session){
		
		BoardDto boardDto = boardService.news();
		
		if (boardDto == null) {
			System.out.println("오늘의 뉴스 에러");
		} else {
			model.addAttribute("categoryN", boardDto);
		}
		
		UserDto userDto = (UserDto)session.getAttribute("userInfo");

		if (userDto != null) {

			model.addAttribute("categoryU", userDto);
			
			String userId = userDto.getUserId();
			
			int seq = boardService.totalCnt(userId);
			model.addAttribute("totalCnt", seq);
			
			int seqL = boardService.totalCntL(userId);
			model.addAttribute("totalCntL", seqL);
			
		}
		
		
	}

	
}









