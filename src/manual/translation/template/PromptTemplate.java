package manual.translation.template;

import java.text.MessageFormat;

public class PromptTemplate {
	
	public static String buildPrompt(String glossaryContent, String koreanAdoc) {

		String template = "You are a professional technical translator.\r\n"
        		+ "\r\n"
        		+ "        Task:\r\n"
        		+ "        Translate the following Korean AsciiDoc (adoc) manual into English.\r\n"
        		+ "\r\n"
        		+ "        Rules:\r\n"
        		+ "        - Preserve all AsciiDoc syntax exactly.\r\n"
        		+ "        - Do NOT modify headings, lists, tables, code blocks, anchors, or attributes.\r\n"
        		+ "        - Translate only the Korean text.\r\n"
        		+ "        - Keep technical terminology consistent with the glossary.\r\n"
        		+ "        - Use professional documentation tone.\r\n"
        		+ "\r\n"
        		+ "        Glossary (must be followed):\r\n"
        		+ "        -----------------------------\r\n"
        		+ "        {0}\r\n"
        		+ "        -----------------------------\r\n"
        		+ "\r\n"
        		+ "        AsciiDoc to translate:\r\n"
        		+ "        -----------------------------\r\n"
        		+ "        {1}\r\n"
        		+ "        -----------------------------";
		return MessageFormat.format(template, new Object[] {glossaryContent, koreanAdoc});
    }
}
