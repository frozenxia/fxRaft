/**
 * @Title: RaftNodeEntity.java
 * @date:Sep 21, 2016 10:37:57 AM
 * @Description:TODO
 */
package com.frozenxia.fxraft.raft;

/**
 *
 * @Description TODO
 * @date Sep 21, 2016 10:37:57 AM
 *
 */
public class RaftNodeEntity {
	public static final long VOTING_NODE = 2;
	public static final long VOTE_FOR_ME = 4;
	private long nextIndex;
	private long matchIndex;
	private long nodeId;
	private long flag;
	private Object data;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setVotingStatus(boolean status) {
		if (status) {
			flag |= VOTING_NODE;
		} else {
			flag &= ~VOTING_NODE;
		}
	}

	public void setVoteForMe(boolean status) {
		if (status) {
			flag |= VOTE_FOR_ME;
		} else {
			flag &= ~VOTE_FOR_ME;
		}
	}

	public boolean getVoteForMeStatus() {
		return (flag & VOTE_FOR_ME) > 0;
	}

	public boolean getVotingStatus() {
		return (flag & VOTING_NODE) > 0;
	}

	public long getNextIndex() {
		return nextIndex;
	}

	public void setNextIndex(long nextIndex) {
		this.nextIndex = nextIndex;
	}

	public long getMatchIndex() {
		return matchIndex;
	}

	public void setMatchIndex(long matchIndex) {
		this.matchIndex = matchIndex;
	}

	public long getNodeId() {
		return nodeId;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

}
