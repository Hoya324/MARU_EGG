package mju.iphak.maru_egg.common.utils.NLP;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;

public class CustomTFIDFSimilarity extends TFIDFSimilarity {
	@Override
	public float coord(int overlap, int maxOverlap) {
		return overlap / (float)maxOverlap;
	}

	@Override
	public float queryNorm(float sumOfSquaredWeights) {
		return 1.0f;
	}

	@Override
	public float tf(float freq) {
		return (float)Math.sqrt(freq);
	}

	@Override
	public float idf(long docFreq, long docCount) {
		return (float)(Math.log((docCount + 1) / (double)(docFreq + 1)) + 1.0);
	}

	@Override
	public float lengthNorm(FieldInvertState state) {
		return 1 / (float)Math.sqrt(state.getLength());
	}

	@Override
	public float sloppyFreq(int distance) {
		return 1.0f;
	}

	@Override
	public float scorePayload(int doc, int start, int end, BytesRef payload) {
		return 1.0f;
	}

	@Override
	public float decodeNormValue(long norm) {
		return 1.0f;
	}

	@Override
	public long encodeNormValue(float f) {
		return 1L;
	}
}
