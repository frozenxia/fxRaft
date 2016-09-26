/**
 * @Title: RaftAppendEntriesMsg.java
 * @date:Sep 21, 2016 11:21:16 AM
 * @Description:TODO
 */
package com.frozenxia.fxraft.raft;

/**
 *
 * @Description TODO
 * @date Sep 21, 2016 11:21:16 AM
 *
 */
public class RaftAppendEntriesMsgResp {
	public static final int GRANT_FAIL = 2;
	public static final int GRANT_SUCCESS = 3;
	private long term;
	private int success;
	private long currentIndex;
	// first log index applied to server this time
	private long firstIndex;

	public long getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(long currentIndex) {
		this.currentIndex = currentIndex;
	}

	public long getFirstIndex() {
		return firstIndex;
	}

	public void setFirstIndex(long firstIndex) {
		this.firstIndex = firstIndex;
	}

	public long getTerm() {
		return term;
	}

	public void setTerm(long term) {
		this.term = term;
	}

	public int getSuccess() {
		return success;
	}

	public boolean isSuccess() {
		return success == GRANT_SUCCESS;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

}
