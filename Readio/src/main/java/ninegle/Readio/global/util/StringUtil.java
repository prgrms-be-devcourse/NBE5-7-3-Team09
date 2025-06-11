package ninegle.Readio.global.util;

import org.slf4j.helpers.MessageFormatter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtil {
	public static String format(String str, Object... args) {
		return MessageFormatter.arrayFormat(str, args).getMessage();
	}
}