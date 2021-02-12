package common;

import java.io.IOException;
import java.util.Random;

import org.apache.commons.mail.HtmlEmail;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import member.MemberVO;

@Service
public class CommonService {
	// 드라마 정보 크롤링 메소드
	public void drama_crawling(Model model, String url) {
		String naverUrl = "https://search.naver.com/search.naver";
		Document doc = null;
		
		try {
			doc = Jsoup.connect(naverUrl + url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Elements element = doc.select("div.cs_common_module");
		
		for(Element el : element.select("div.cs_common_module")) {
			model.addAttribute("drama", el.outerHtml());
		}
	} //drama_crawling()
	
	// TV 속 이 상품 크롤링 메소드
	public void shopping_crawling(Model model) {
		String naverUrl = "https://search.naver.com/search.naver";
		String onairUrl = "?sm=tab_hty.top&where=nexearch&query=드라마&oquery=방영중한국드라마&tqi=hu6q0sprvTossj4s6rGssssstW4-400427";
		Document doc = null;
		int index1 = 0, index2 = 0;
		String[] dramaUrl = new String[3];
		
		try {
			doc = Jsoup.connect(naverUrl + onairUrl).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Elements element = doc.select("a._text");
		for(Element el1 : element.select("a._text")) { 
            //System.out.print(el1.text() + " : ");	//방영중 드라마 이름
            //System.out.println(el1.attr("href"));	//방영중 드라마 url
            try {
				doc = Jsoup.connect(naverUrl + el1.attr("href")).get();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
            element = doc.select("h2.api_title");
            for(Element el2: element.select("h2.api_title")) {	//TV 속 이상품 태그
            	//System.out.println(el2.text());
            	if(el2.text().contains("TV 속 이 상품")) {
            		if (index1 == 3) break;
            		dramaUrl[index1] = el1.attr("href");
            		index1++;
            	}
            } //foreach
		} //foreach
		
		// TV 속 이 상품 태그 구하기
		for (int i = 0; i < dramaUrl.length; i++) {
			try {
				doc = Jsoup.connect(naverUrl + dramaUrl[i]).get();
			} catch (IOException e) {
				e.printStackTrace();
			}
			element = doc.select("section._au_2th_shopping_in_tv");
			for(Element el3 : element.select("section._au_2th_shopping_in_tv")) {
				model.addAttribute("drama"+index2, el3.outerHtml());
				index2++;
			}
		}		
	} //shopping_crawling()
	
	// 난수 값(토큰)을 발급해주는 메소드
	public String getRandomCode( int length ){
		char[] charaters = {'a','b','c','d','e','f','g','h','i','j','k','l','m',
							'n','o','p','q','r','s','t','u','v','w','x','y','z',
	        				'0','1','2','3','4','5','6','7','8','9'};
	    StringBuffer sb = new StringBuffer();
	    Random rn = new Random();

	    for( int i = 0 ; i < length ; i++ ){
	    	sb.append( charaters[ rn.nextInt( charaters.length ) ] );
	    }
	        
	    return sb.toString();
	} //getRandomPassword()
	
	// 회원가입 인증 메일 보내기
	public void emailAuthSend(MemberVO vo, String code) {
		HtmlEmail mail = new HtmlEmail();
		mail.setHostName("smtp.naver.com");
		mail.setCharset("utf-8");
		mail.setDebug(true);
		//mail.setSmtpPort(465);
		//mail.setStartTLSEnabled(true);

		mail.setAuthentication("tjdms5322", "qkr2684!");
		mail.setSSLOnConnect(true);

		try {
			mail.setFrom("tjdms5322@naver.com", "관리자");
			mail.addTo(vo.getMember_id(), vo.getMember_name());

			mail.setSubject("[Deuqoo] 회원가입 이메일 인증메일");
			StringBuffer msg = new StringBuffer();
			msg.append("<html>");
			msg.append("<body>");
			msg.append("<h1 style='margin-bottom: 20px;'>이메일 인증</h1>");
			msg.append("<div>");
			msg.append("안녕하세요 [Deuqoo]입니다.<br/> 아래 버튼을 누르고 회원가입을 진행해주세요.");
			msg.append("</div>");
			msg.append("<a href='http://192.168.35.241/deuqoo/emailAuth?code=" + code + "&member_id=" + vo.getMember_id() + "'>이메일 인증하기</a>");
			msg.append("</body>");
			msg.append("</html>");
			mail.setHtmlMsg(msg.toString());

			mail.send();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	} // emailAuthSend()

	
}
