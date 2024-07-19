package mju.iphak.maru_egg.question.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;

import scala.collection.JavaConverters;
import scala.collection.Seq;

public class PhraseExtractionUtils {
	public static List<String> extractTokens(List<KoreanTokenizer.KoreanToken> tokens) {
		return tokens.stream()
			.filter(token -> token.pos().toString().equals("Noun") || token.pos().toString().equals("Number"))
			.map(token -> token.text().toString())
			.collect(Collectors.toList());
	}

	public static String extractPhrases(String text) {
		CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);
		Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
		List<KoreanTokenizer.KoreanToken> tokenList = JavaConverters.seqAsJavaList(tokens);
		List<String> phrases = extractTokens(tokenList);
		return String.join(" ", phrases);
	}
}