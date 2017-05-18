package com.cki.filter;

public class BloomCalculations {

	private static final int maxBuckets = 15;
	private static final int minBuckets = 2;
	private static final int minK = 1;
	private static final int maxK = 8;
	private static final int[] optKPerBuckets = new int[] { 1, 1, 1, 2, 3, 3, 4, 5, 5, 6, 7, 8, 8, 8, 8, 8 };

	static final double[][] probs = new double[][] { { 1.0 }, { 1.0, 1.0 }, { 1.0, 0.393, 0.400 }, { 1.0, 0.283, 0.237, 0.253 }, { 1.0, 0.221, 0.155, 0.147, 0.160 },
			{ 1.0, 0.181, 0.109, 0.092, 0.092, 0.101 }, { 1.0, 0.154, 0.0804, 0.0609, 0.0561, 0.0578, 0.0638 }, { 1.0, 0.133, 0.0618, 0.0423, 0.0359, 0.0347, 0.0364 },
			{ 1.0, 0.118, 0.0489, 0.0306, 0.024, 0.0217, 0.0216, 0.0229 }, { 1.0, 0.105, 0.0397, 0.0228, 0.0166, 0.0141, 0.0133, 0.0135, 0.0145 },
			{ 1.0, 0.0952, 0.0329, 0.0174, 0.0118, 0.00943, 0.00844, 0.00819, 0.00846 }, { 1.0, 0.0869, 0.0276, 0.0136, 0.00864, 0.0065, 0.00552, 0.00513, 0.00509 },
			{ 1.0, 0.08, 0.0236, 0.0108, 0.00646, 0.00459, 0.00371, 0.00329, 0.00314 }, { 1.0, 0.074, 0.0203, 0.00875, 0.00492, 0.00332, 0.00255, 0.00217, 0.00199 },
			{ 1.0, 0.0689, 0.0177, 0.00718, 0.00381, 0.00244, 0.00179, 0.00146, 0.00129 }, { 1.0, 0.0645, 0.0156, 0.00596, 0.003, 0.00183, 0.00128, 0.001, 0.000852 } };

	public static int computeBestK(int bucketsPerElement) {
		assert bucketsPerElement >= 0;
		if (bucketsPerElement >= optKPerBuckets.length)
			return optKPerBuckets[optKPerBuckets.length - 1];
		return optKPerBuckets[bucketsPerElement];
	}

	public static final class BloomSpecification {
		final int K;
		final int bucketsPerElement;

		public BloomSpecification(int k, int bucketsPerElement) {
			K = k;
			this.bucketsPerElement = bucketsPerElement;
		}

		@Override
		public String toString() {
			return "BloomSpecification [K=" + K + ", bucketsPerElement=" + bucketsPerElement + "]";
		}
	}

	public static BloomSpecification computeBucketsAndK(double maxFalsePosProb) {

		if (maxFalsePosProb >= probs[minBuckets][minK]) {
			return new BloomSpecification(2, optKPerBuckets[2]);
		}
		if (maxFalsePosProb < probs[maxBuckets][maxK]) {
			return new BloomSpecification(maxK, maxBuckets);
		}

		int bucketsPerElement = 2;
		int K = optKPerBuckets[2];
		while (probs[bucketsPerElement][K] > maxFalsePosProb) {
			bucketsPerElement++;
			K = optKPerBuckets[bucketsPerElement];
		}

		while (probs[bucketsPerElement][K - 1] <= maxFalsePosProb) {
			K--;
		}

		return new BloomSpecification(K, bucketsPerElement);
	}

	public static void main(String[] args) {
		System.out.println(BloomCalculations.computeBestK(100000));
		BloomSpecification computeBucketsAndK = BloomCalculations.computeBucketsAndK(1.0E-5);
		System.out.println(computeBucketsAndK);
		System.out.println(computeBucketsAndK.bucketsPerElement * 200000000L / 8 / 1024 / 1024);
	}
}
