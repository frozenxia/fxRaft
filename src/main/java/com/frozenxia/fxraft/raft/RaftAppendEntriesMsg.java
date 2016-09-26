/**
 * @Title: RaftAppendEntriesMsg.java
 * @date:Sep 21, 2016 11:21:16 AM
 * @Description:TODO
 */
package com.frozenxia.fxraft.raft;

import java.util.List;

/**
 *
 * @Description TODO
 * @date Sep 21, 2016 11:21:16 AM
 *
 */
public class RaftAppendEntriesMsg {
	private long term;
	private long leaderId;
	private long prevLogIndex;
	private long prevLogTerm;
	private List<LogEntry> entries;
	private long leaderCommit;

	public long getTerm() {
		return term;
	}

	public void setTerm(long term) {
		this.term = term;
	}

	public long getLeaderId() {
		return leaderId;
	}

	public void setLeaderId(long leaderId) {
		this.leaderId = leaderId;
	}

	public long getPrevLogIndex() {
		return prevLogIndex;
	}

	public void setPrevLogIndex(long prevLongIndex) {
		this.prevLogIndex = prevLongIndex;
	}

	public long getPrevLogTerm() {
		return prevLogTerm;
	}

	public void setPrevLogTerm(long prevLogTerm) {
		this.prevLogTerm = prevLogTerm;
	}

	public List<LogEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<LogEntry> entries) {
		this.entries = entries;
	}

	public long getLeaderCommit() {
		return leaderCommit;
	}

	public void setLeaderCommit(long leaderCommit) {
		this.leaderCommit = leaderCommit;
	}
}
