package manual.translation;

import java.util.HashMap;
import java.util.Map;

public class DefaultPromptTemplate {

	private static final String DEFAULT_PROMPT_TEMPLATE = "당신은 한국어 → 영어 기술 문서 전문 번역가입니다.\r\n"
			+ "\r\n"
			+ "아래 용어집을 최우선 기준으로 사용하여,\r\n"
			+ "한국어로 작성된 AsciiDoc(Adoc) 문서를 정확히 영어로 번역하세요.\r\n"
			+ "\r\n"
			+ "번역 규칙:\r\n"
			+ "1. 용어집의 번역어를 반드시 최우선 적용하세요.\r\n"
			+ "2. 기술 문서 스타일로 간결하고 중립적으로 번역하세요.\r\n"
			+ "3. 원문의 의미를 누락·추가·요약하지 마세요. 의역은 최소화합니다.\r\n"
			+ "4. Adoc 문법과 구조는 완벽히 유지해야 합니다.\r\n"
			+ "   - 제목 레벨(=, ==, ===)\r\n"
			+ "   - 목록 기호(*, -, .)\r\n"
			+ "   - 테이블(|===)\r\n"
			+ "   - 코드블록(----, ``` 등)\r\n"
			+ "   - include, xref, link, 속성, 앵커 구문 등\r\n"
			+ "5. 코드, 옵션명, 경로, 변수명, API 명칭 등은 번역하지 말고 그대로 유지하세요.\r\n"
			+ "6. 의미가 불명확한 부분은 추측하지 말고 원문에 맞춰 직역합니다.\r\n"
			+ "7. 설명이나 추가 코멘트 없이, 번역된 영어 Adoc 문서만 출력하세요.\r\n"
			+ "\r\n"
			+ "--------------------\r\n"
			+ "용어집:\r\n"
			+ "{context}\r\n"
			+ "--------------------\r\n"
			+ "한국어 Adoc 문서:\r\n"
			+ "{koreanAdoc}\r\n"
			+ "--------------------\r\n"
			+ "영어 Adoc 문서:";
	
	public static String buildPrompt(String context, String koreanAdoc) {
		Map<String, String> variables = new HashMap<>();
		variables.put("context", context);
		variables.put("koreanAdoc", koreanAdoc);

		return Utils.variableMapping(DEFAULT_PROMPT_TEMPLATE, variables);
    }
}