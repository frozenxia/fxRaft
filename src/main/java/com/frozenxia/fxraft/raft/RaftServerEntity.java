/**
 * @Title: RaftServerEntity.java
 * @date:Sep 21, 2016 10:37:46 AM
 * @Description:TODO
 */
package com.frozenxia.fxraft.raft;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @Description TODO
 * @date Sep 21, 2016 10:37:46 AM
 *
 */
public class RaftServerEntity {
	public static final int RAFT_ERROR_ONE_VOTING_CFG_CONLY = -2;
	public static final int RAFT_ERROR_LEADER_ONLY = -3;
	private RaftPins handler;
	private long currentTerm;
	private long voteFor;
	private RaftLogEntity log;
	private long commitIndex;
	private long lastApplied;
	private List<RaftNodeEntity> nodes;
	// recv request timeout before last time
	private long requestTimeOut;
	private long electionTimeOut;
	private long electionTimeElapsed;
	private long votingCfgChangeIndex;

	private RaftServerState state;
	private RaftNodeEntity leader;
	private RaftNodeEntity selfNode;

	// need to handle it
	public RaftPins getHandler() {
		return handler;
	}

	public void setHandler(RaftPins handler) {
		this.handler = handler;
	}

	public boolean getVotingStatus() {
		boolean bret = false;
		if (selfNode != null) {
			bret = selfNode.getVotingStatus();
		}
		return bret;
	}

	public List<LogEntry> getLogEntriesFromIndex(long index) {
		return log.getLogEntriesFromIndex(index);
	}

	public long deleteLogEntriesFromIndex(long index){
		return log.deleteLogEntriesFromIndex(index);
	}
	public long getCurrentLogIndex() {
		return log.getCurrentLogIndex();
	}

	public LogEntry getLogEntryByIndex(long index) {
		return log.getLogEntryByIndex(index);
	}

	public long getTermByLogIndex(long index) {
		return log.getTermByLogIndex(index);
	}

	public RaftNodeEntity getNodeById(long id) {
		RaftNodeEntity nd = null;
		for (RaftNodeEntity rn : nodes) {
			if (rn.getNodeId() == id) {
				nd = rn;
				break;
			}
		}
		return nd;
	}

	public long getNodeId() {
		long lret = -1;
		if (selfNode != null) {
			lret = selfNode.getNodeId();
		}
		return lret;
	}

	public void addNode(RaftNodeEntity nd) {
		if (nodes == null) {
			nodes = new ArrayList<RaftNodeEntity>();
		}
		nodes.add(nd);
	}

	public RaftServerState getState() {
		return state;
	}

	public void setState(RaftServerState state) {
		this.state = state;
	}

	public RaftNodeEntity getLeader() {
		return leader;
	}

	public void setLeader(RaftNodeEntity leader) {
		this.leader = leader;
	}

	public List<RaftNodeEntity> getNodes() {
		return nodes;
	}

	public void setNodes(List<RaftNodeEntity> nodes) {
		this.nodes = nodes;
	}

	public long getVotingCfgChangeIndex() {
		return votingCfgChangeIndex;
	}

	public boolean isVotingCfgChange() {
		return votingCfgChangeIndex == -1;
	}

	public void setVotingCfgChangeIndex(long votingCfgChangeIndex) {
		this.votingCfgChangeIndex = votingCfgChangeIndex;
	}

	public RaftNodeEntity getSelfNode() {
		return selfNode;
	}

	public void setSelfNode(RaftNodeEntity selfNode) {
		this.selfNode = selfNode;
	}

	public long getRequestTimeOut() {
		return requestTimeOut;
	}

	public void setRequestTimeOut(long requestTimeOut) {
		this.requestTimeOut = requestTimeOut;
	}

	public long getElectionTimeOut() {
		return electionTimeOut;
	}

	public void setElectionTimeOut(long electionTimeOut) {
		this.electionTimeOut = electionTimeOut;
	}

	public long getElectionTimeElapsed() {
		return electionTimeElapsed;
	}

	public void setElectionTimeElapsed(long electionTimeElapsed) {
		this.electionTimeElapsed = electionTimeElapsed;
	}

	public long getCurrentTerm() {
		return currentTerm;
	}

	public void setCurrentTerm(long currentTerm) {
		this.currentTerm = currentTerm;
	}

	public long getVoteFor() {
		return voteFor;
	}

	public void setVoteFor(long voteFor) {
		this.voteFor = voteFor;
	}

	public int appendEntry(LogEntry et) {
		if (handler != null) {
			handler.offerLogEntry(this, et);
		}
		return log.appendLogEntry(et);
	}

	// public RaftLogEntity getLog() {
	// return log;
	// }

	public void setLog(RaftLogEntity log) {
		this.log = log;
	}

	public long getCommitIndex() {
		return commitIndex;
	}

	public void setCommitIndex(long commitIndex) {
		this.commitIndex = commitIndex;
	}

	public long getLastApplied() {
		return lastApplied;
	}

	public void setLastApplied(long lastApplied) {
		this.lastApplied = lastApplied;
	}

}
