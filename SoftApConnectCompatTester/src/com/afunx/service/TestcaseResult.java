package com.afunx.service;

public class TestcaseResult {

	private final Testcase mTestcase;
	private final boolean mIsSuc;
	private final long mConsumeTimestamp;

	private TestcaseResult(Testcase testcase, boolean isSuc,
			long consumeTimestamp) {
		mTestcase = testcase;
		mIsSuc = isSuc;
		mConsumeTimestamp = consumeTimestamp;
	}

	public static TestcaseResult createSucResult(Testcase testcase,
			long consumeTimestamp) {
		return new TestcaseResult(testcase, true, consumeTimestamp);
	}

	public static TestcaseResult createFailResult(Testcase testcase) {
		return new TestcaseResult(testcase, false, -1);
	}

	public String getPrevTestcaseStr() {
		if (mIsSuc) {
			return "Prev Testcase:\n" + mTestcase + " suc in " + mConsumeTimestamp + " ms";
		} else {
			return "Prev Testcase:\n" + mTestcase + " fail";
		}
	}
	
	public String getCurTestcaseStr() {
		return "Cur Testcase:" + mTestcase + " is executing";
	}
}
