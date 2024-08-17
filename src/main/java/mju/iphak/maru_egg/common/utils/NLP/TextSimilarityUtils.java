package mju.iphak.maru_egg.common.utils.NLP;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.similarity.CosineSimilarity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class TextSimilarityUtils {

	public static double computeCosineSimilarity(Map<CharSequence, Integer> vec1, Map<CharSequence, Integer> vec2) {
		CosineSimilarity cosineSimilarity = new CosineSimilarity();
		return cosineSimilarity.cosineSimilarity(vec1, vec2);
	}

	public static Map<CharSequence, Integer> computeTfIdf(List<String> documents, String query) throws Exception {
		Analyzer analyzer = new WhitespaceAnalyzer();
		Directory directory = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		try (IndexWriter indexWriter = new IndexWriter(directory, config)) {
			for (String document : documents) {
				Document doc = new Document();
				doc.add(new TextField("content", document, Field.Store.YES));
				indexWriter.addDocument(doc);
			}
		}

		try (IndexReader reader = DirectoryReader.open(directory)) {
			TFIDFSimilarity tfidfSimilarity = new CustomTFIDFSimilarity();
			Map<CharSequence, Integer> tfidfMap = new HashMap<>();
			List<String> terms = Arrays.asList(query.split("\\s+"));

			for (String term : terms) {
				int docFreq = reader.docFreq(new Term("content", term));
				float idf = tfidfSimilarity.idf(docFreq, reader.maxDoc());
				tfidfMap.put(term, (int)idf);
			}

			return tfidfMap;
		} finally {
			directory.close();
		}
	}
}