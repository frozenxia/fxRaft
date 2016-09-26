/**
 * @Title: LogEntry.java
 * @date:Sep 21, 2016 11:03:40 AM
 * @Description:TODO
 */
package com.frozenxia.fxraft.raft;

/**
 *
 * @Description TODO
 * @date Sep 21, 2016 11:03:40 AM
 *
 */
public class LogEntry {
	public static final long RAFT_LOGTYPE_NORMAL = 0;
	public static final long RAFT_LOGTYPE_ADD_VOTING_NODE = 1;
	public static final long RAFT_LOGTYPE_ADD_NODE = 2;
	public static final long RAFT_LOGTYPE_REMOVE_NODE = 3;
	public static final long RAFT_LOGTYPE_NUM = 4;
	private long logId;
	private long termId;
	private long type;
	private Object data;

	public boolean isVotingCfgChange() {
		return type == RAFT_LOGTYPE_ADD_VOTING_NODE || type == RAFT_LOGTYPE_REMOVE_NODE;
	}

	public long getLogId() {
		return logId;
	}

	public void setLogId(long logId) {
		this.logId = logId;
	}

	public long getTermId() {
		return termId;
	}

	public void setTermId(long termId) {
		this.termId = termId;
	}

	public long getType() {
		return type;
	}

	public void setType(long type) {
		this.type = type;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
