/**
 * @Title: RaftEntryMsgResp.java
 * @date:Sep 26, 2016 2:07:37 PM
 * @Description:TODO
 */
package com.frozenxia.fxraft.raft;

/**
 *
 * @Description TODO
 * @date Sep 26, 2016 2:07:37 PM
 *
 */
public class RaftEntryMsgResp {
	long term;
	long id;
	long idx;

	public long getTerm() {
		return term;
	}

	public void setTerm(long term) {
		this.term = term;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdx() {
		return idx;
	}

	public void setIdx(long idx) {
		this.idx = idx;
	}
}
