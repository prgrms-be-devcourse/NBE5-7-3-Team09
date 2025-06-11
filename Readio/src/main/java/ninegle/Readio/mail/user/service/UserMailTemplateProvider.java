package ninegle.Readio.mail.user.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ninegle.Readio.global.util.StringUtil;

@Service
public class UserMailTemplateProvider {

	public String buildSignupMailBody(String nickname) {
		// 닉네임이 null 또는 공백일 경우 "회원"으로 대체
		String reNickname = StringUtils.hasText(nickname) ? nickname : "회원";

		return StringUtil.format("""
                안녕하세요, {}님!
                
                ✨ Readio 회원가입을 진심으로 환영합니다! ✨
                이제 다양한 전자책과 함께하는 새로운 독서 경험을 시작해보세요.
                
                🎁 신규 회원 특별 혜택
                • 가입 축하 포인트 15,000P 즉시 지급 완료!
                  (포인트는 구독권 결제에 사용하실 수 있습니다)
                
                💡 Readio에서 즐길 수 있는 서비스
                • 다양한 장르의 e-book 라이브러리 이용
                • 다른 사용자들의 다양한 독후감 공유 및 조회
                • 사용자 맞춤형 뷰어로 편안한 독서 환경 제공
                
                지금 바로 Readio에 접속하여 나만의 독서 여정을 시작해보세요!
                다양한 전자책 콘텐츠와 이야기, Readio는 언제나 여러분을 기다리고 있습니다.
                
                궁금한 점이 있으시면 언제든지 고객센터로 문의해주세요.
                
                즐거운 독서 여정이 되시길 바랍니다!
                
                감사합니다.
                Readio 팀 드림
                
                © 2025. Readio, Inc. All rights reserved.
                본 메일은 발신 전용입니다.
                
                ------------------------------------------------------------
                주식회사 Readio | 프로그래머스 2차 프로젝트 리디오 
                전화번호: 02-123-456 | 전자책서비스를 여러분에게 제공합니다.
                Copyright © 2025 by Readio, Inc. All rights reserved.
                """, reNickname);
	}
}