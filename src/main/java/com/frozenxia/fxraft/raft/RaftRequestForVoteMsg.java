/**
 * @Title: RaftRequestForVoteMsg.java
 * @date:Sep 21, 2016 11:05:56 AM
 * @Description:TODO
 */
package com.frozenxia.fxraft.raft;

/**
 *
 * @Description TODO
 * @date Sep 21, 2016 11:05:56 AM
 *
 */
public class RaftRequestForVoteMsg {
	private long term;
	private long candidateId;
	private long lastLogIndex;
	private long lastLogTerm;
	public long getTerm() {
		return term;
	}
	public void setTerm(long term) {
		this.term = term;
	}
	public long getCandidateId() {
		return candidateId;
	}
	public void setCandidateId(long candidateId) {
		this.candidateId = candidateId;
	}
	public long getLastLogIndex() {
		return lastLogIndex;
	}
	public void setLastLogIndex(long lastLogIndex) {
		this.lastLogIndex = lastLogIndex;
	}
	public long getLastLogTerm() {
		return lastLogTerm;
	}
	public void setLastLogTerm(long lastLogTerm) {
		this.lastLogTerm = lastLogTerm;
	}
	
}
