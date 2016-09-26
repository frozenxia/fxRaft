/**
 * @Title: RaftRequestForVoteMsgResp.java
 * @date:Sep 21, 2016 11:11:10 AM
 * @Description:TODO
 */
package com.frozenxia.fxraft.raft;

/**
 *
 * @Description TODO
 * @date Sep 21, 2016 11:11:10 AM
 *
 */
public class RaftRequestForVoteMsgResp {
	public static final int GRANTED_SUCCESS = 2;
	public static final int GRANTED_FAILED = 3;
	public static final int GRANTED_UNKNOWN = 4;
	private long term;
	private int voteGranted;

	public long getTerm() {
		return term;
	}

	public void setTerm(long term) {
		this.term = term;
	}

	public int getVoteGranted() {
		return voteGranted;
	}

	public void setVoteGranted(int voteGranted) {
		this.voteGranted = voteGranted;
	}
}
