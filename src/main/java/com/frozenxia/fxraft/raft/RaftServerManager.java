/**
 * @Title: RaftServerManager.java
 * @date:Sep 21, 2016 2:19:03 PM
 * @Description:TODO
 */
package com.frozenxia.fxraft.raft;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @Description TODO
 * @date Sep 21, 2016 2:19:03 PM
 *
 */
public class RaftServerManager {
	private RaftServerEntity server;
	// private RaftPins handler;

	public RaftServerManager() {
		initializeServer();
	}

	public final RaftServerEntity getServer() {
		return server;
	}

	public void setRaftHandler(RaftPins handler) {
		this.server.setHandler(handler);
	}

	private void initializeServer() {
		server = new RaftServerEntity();
		server.setCommitIndex(0);
		server.setCurrentTerm(0);
		server.setElectionTimeElapsed(0);
		// default election timeout is 100 mili_seconds
		server.setElectionTimeOut(1000);
		server.setLastApplied(0);
		server.setLog(new RaftLogEntity());
		server.setNodes(new ArrayList<RaftNodeEntity>());
		server.setRequestTimeOut(200);
		server.setVoteFor(-1);
		server.setVotingCfgChangeIndex(-1);

		setServerState(RaftServerState.FOLLOWER);
		server.setLeader(null);
	}

	public void setServerState(RaftServerState state) {
		if (state == RaftServerState.LEADER) {
			server.setLeader(server.getSelfNode());
		}
		server.setState(state);
	}

	public void addVotingNode(Object data, long nodeId, boolean is_self) {
		RaftNodeEntity node = new RaftNodeEntity();
		node.setVotingStatus(true);
		node.setNodeId(nodeId);
		node.setData(data);
		server.addNode(node);
		if (is_self) {
			server.setSelfNode(node);
		}
	}

	public void addNonVotingNode(Object data, long nodeId, boolean is_self) {
		RaftNodeEntity node = new RaftNodeEntity();
		node.setVotingStatus(false);
		node.setNodeId(nodeId);
		node.setData(data);
		server.addNode(node);
		if (is_self) {
			server.setSelfNode(node);
		}
	}

	public void becomeFollower() {
		this.setServerState(RaftServerState.FOLLOWER);
	}

	public void electionStart() {
		this.becomeCandidate();
	}

	public void becomeCandidate() {
		this.setServerState(RaftServerState.CANDIDATE);
		this.setCurrentTerm(this.getCurrentterm() + 1);
		for (RaftNodeEntity entity : server.getNodes()) {
			entity.setVoteForMe(false);
		}
		this.serverVoteForNode(server.getSelfNode());
		long electionTimeElapsed = server.getElectionTimeOut()
				- 2 * (new Random().nextInt() % server.getElectionTimeOut());
		server.setElectionTimeElapsed(electionTimeElapsed);
		for (RaftNodeEntity entity : server.getNodes()) {
			this.sendRequestVoteMsg(entity);
		}
	}

	public void sendRequestVoteMsg(RaftNodeEntity node) {
		RaftRequestForVoteMsg msg = new RaftRequestForVoteMsg();
		msg.setCandidateId(server.getNodeId());
		msg.setTerm(server.getCurrentTerm());
		msg.setLastLogTerm(this.getLastLogTerm());
		msg.setLastLogIndex(this.getLastLogIndex());
		if (server.getHandler() != null) {
			server.getHandler().sendRequestVote(node, msg);
		}
	}

	public int grandRequestVoteMsg(RaftNodeEntity node, RaftRequestForVoteMsg msg) {
		// already vote
		if (server.getVoteFor() != -1) {
			return -1;
		}
		// not current term
		if (msg.getTerm() < server.getCurrentTerm()) {
			return -1;
		}
		// server is not a voting node
		if (!server.getVotingStatus()) {
			return -1;
		}

		// server log is update to candidate

		long logIndex = this.getLastLogIndex();
		if (logIndex == 0) {
			return 1;
		}
		LogEntry entry = this.getLogEntryByIndex(logIndex);
		if (entry.getTermId() < msg.getLastLogTerm()) {
			return 1;
		}
		if (entry.getTermId() == msg.getLastLogTerm() && logIndex <= msg.getLastLogIndex()) {
			return 1;
		}
		return -1;
	}

	public RaftRequestForVoteMsgResp recvRequestVoteMsg(RaftNodeEntity node, RaftRequestForVoteMsg msg) {
		RaftRequestForVoteMsgResp resp = new RaftRequestForVoteMsgResp();
		if (node == null) {
			node = server.getNodeById(msg.getCandidateId());
		}
		if (this.getCurrentterm() < msg.getTerm()) {
			this.setCurrentTerm(msg.getTerm());
			this.setServerState(RaftServerState.FOLLOWER);
		}
		if (grandRequestVoteMsg(node, msg) > 0) {
			this.serverVoteForNode(node);
			server.setLeader(null);
			server.setElectionTimeElapsed(0);
			resp.setVoteGranted(RaftRequestForVoteMsgResp.GRANTED_SUCCESS);
		} else {
			if (node != null) {
				resp.setVoteGranted(RaftRequestForVoteMsgResp.GRANTED_FAILED);
			} else {
				resp.setVoteGranted(RaftRequestForVoteMsgResp.GRANTED_UNKNOWN);
			}
		}
		resp.setTerm(this.getCurrentterm());
		return resp;
	}

	public int recvRequestVoteMsgResp(RaftNodeEntity node, RaftRequestForVoteMsgResp msg) {
		if (this.getServerState() != RaftServerState.CANDIDATE) {
			return 0;
		} else if (this.getCurrentterm() < msg.getTerm()) {
			this.becomeFollower();
			this.setCurrentTerm(msg.getTerm());
			return 0;
		} else if (this.getCurrentterm() != msg.getTerm()) {
			return 0;
		}
		if (RaftRequestForVoteMsgResp.GRANTED_SUCCESS == msg.getVoteGranted()) {
			if (node != null) {
				node.setVoteForMe(true);
			}
			if (isVoteMajority()) {
				this.becomeLeader();
			}
			return 1;
		}
		return 0;
	}

	public void becomeLeader() {
		this.setServerState(RaftServerState.LEADER);
		long current_index = this.getLastLogIndex();

		for (RaftNodeEntity nd : server.getNodes()) {
			if (!nd.getVotingStatus() || nd.getNodeId() == server.getNodeId()) {
				continue;
			}
			nd.setNextIndex(current_index + 1);
			nd.setMatchIndex(0);
			this.raftSendAppendEntriesMsg(nd);
		}
	}

	public void raftSendAppendEntriesMsg(RaftNodeEntity node) {
		RaftAppendEntriesMsg msg = new RaftAppendEntriesMsg();
		msg.setLeaderCommit(this.getCurrentCommitIndex());
		msg.setLeaderId(server.getNodeId());
		msg.setTerm(this.getCurrentterm());
		if (node.getNextIndex() > 1) {
			msg.setPrevLogIndex(node.getNextIndex() - 1);
			msg.setPrevLogTerm(this.getLogEntryByIndex(node.getNextIndex() - 1).getTermId());
		} else {
			msg.setPrevLogTerm(0);
			msg.setPrevLogIndex(0);
		}
		msg.setEntries(this.getLogEntriesFromIndex(node.getNextIndex()));
		if (server.getHandler() != null) {
			server.getHandler().sendAppendEntriesMsg(node, msg);
		}
	}

	public RaftAppendEntriesMsgResp raftRecvAppendEntriesMsg(RaftNodeEntity node, RaftAppendEntriesMsg msg) {
		// stop from election out
		this.setElectionTimeElapsed(0);

		RaftAppendEntriesMsgResp resp = new RaftAppendEntriesMsgResp();
		if (this.getCurrentterm() > msg.getTerm()) {
			resp.setTerm(this.getCurrentterm());
			resp.setSuccess(RaftAppendEntriesMsgResp.GRANT_FAIL);
			resp.setFirstIndex(0);
			resp.setCurrentIndex(this.getLastLogIndex());
			return resp;
		} else if (this.getCurrentterm() < msg.getTerm()) {
			this.becomeFollower();
			this.setCurrentTerm(msg.getTerm());
		} else if (this.getCurrentterm() == msg.getTerm() && this.getServerState() == RaftServerState.CANDIDATE) {
			this.becomeFollower();
			server.setVoteFor(-1);
		}

		if (msg.getPrevLogIndex() > 0) {
			long prevLogIndex = msg.getPrevLogIndex();
			LogEntry et = this.getLogEntryByIndex(prevLogIndex);
			if (et == null) {
				resp.setTerm(this.getCurrentterm());
				resp.setSuccess(RaftAppendEntriesMsgResp.GRANT_FAIL);
				resp.setFirstIndex(0);
				resp.setCurrentIndex(this.getLastLogIndex());
				return resp;
			}
			if (this.getLastLogIndex() < msg.getPrevLogIndex()) {
				resp.setTerm(this.getCurrentterm());
				resp.setSuccess(RaftAppendEntriesMsgResp.GRANT_FAIL);
				resp.setFirstIndex(0);
				resp.setCurrentIndex(this.getLastLogIndex());
				return resp;
			}
			if (et.getTermId() != msg.getPrevLogTerm()) {
				resp.setTerm(this.getCurrentterm());
				resp.setSuccess(RaftAppendEntriesMsgResp.GRANT_FAIL);
				resp.setFirstIndex(0);
				resp.setCurrentIndex(msg.getPrevLogIndex() - 1);
				deleteLogEntriesFromIndex(msg.getPrevLogIndex());
				return resp;
			}
		}

		// delete conflict logs
		if (msg.getEntries().size() == 0 && msg.getPrevLogIndex() > 0
				&& msg.getPrevLogIndex() + 1 < this.getLastLogIndex()) {
			this.deleteLogEntriesFromIndex(msg.getPrevLogIndex());
		}

		int log_index = 0;
		for (; log_index < msg.getEntries().size(); log_index++) {
			LogEntry m_log = msg.getEntries().get(log_index);
			LogEntry s_log = this.getLogEntryByIndex(msg.getPrevLogIndex() + log_index + 1);
			if (s_log != null && s_log.getTermId() != m_log.getTermId()) {
				this.deleteLogEntriesFromIndex(msg.getPrevLogIndex() + log_index + 1);
			} else if (s_log != null) {
				break;
			}
		}
		// same term and same index, so we consider it as same log
		for (; log_index < msg.getEntries().size(); log_index++) {
			int e = this.raftAppendEntry(msg.getEntries().get(log_index));
			if (e < 0) {
				resp.setTerm(this.getCurrentterm());
				resp.setSuccess(RaftAppendEntriesMsgResp.GRANT_FAIL);
				resp.setFirstIndex(0);
				resp.setCurrentIndex(this.getLastLogIndex());
				return resp;
			}
			resp.setCurrentIndex(msg.getPrevLogIndex() + 1 + log_index);
		}

		if (this.getCurrentCommitIndex() < msg.getLeaderCommit()) {
			long last_log_index = this.getLastLogIndex();
			this.setCurrentCommitIndex(Math.min(msg.getLeaderCommit(), last_log_index));
		}
		// recv commit log so append last log
		this.setCurrentLeader(node);
		resp.setFirstIndex(msg.getPrevLogIndex() + 1);
		resp.setSuccess(RaftAppendEntriesMsgResp.GRANT_SUCCESS);
		resp.setTerm(this.getCurrentterm());
		return resp;
	}

	public int raftRecvAppendEntriesResp(RaftNodeEntity node, RaftAppendEntriesMsgResp resp) {
		if (resp.getCurrentIndex() != 0 && resp.getCurrentIndex() < node.getMatchIndex()) {
			return 0;
		}
		if (this.getCurrentterm() < resp.getTerm()) {
			this.setCurrentTerm(resp.getTerm());
			this.becomeFollower();
			return 0;
		} else if (this.getCurrentterm() != resp.getTerm()) {
			return 0;
		}

		if (!resp.isSuccess()) {
			assert(node.getNextIndex() >= 0);
			long next_idx = node.getNextIndex();
			if (resp.getCurrentIndex() < next_idx - 1) {
				node.setNextIndex(Math.min(resp.getCurrentIndex() + 1, this.getLastLogIndex()));
			} else {
				node.setNextIndex(next_idx - 1);
			}
			this.raftSendAppendEntriesMsg(node);
			return 0;
		}

		node.setNextIndex(resp.getCurrentIndex() + 1);
		node.setMatchIndex(resp.getCurrentIndex());

		/**
		 * 
		 * update commit index
		 * 
		 */

		long current_index = resp.getCurrentIndex();
		int votes = 1;
		for (RaftNodeEntity nd : server.getNodes()) {
			if (!nd.getVotingStatus() || nd.getNodeId() == server.getNodeId()) {
				continue;
			}
			long match_index = nd.getMatchIndex();
			if (match_index > 0) {
				LogEntry et = this.getLogEntryByIndex(match_index);
				if (et != null && et.getTermId() == resp.getTerm() && match_index <= current_index) {
					votes++;
				}
			}
		}
		if (this.isMajority(votes, this.getVotingNodesNums())) {
			this.setCurrentCommitIndex(current_index);
		}
		// send appendentries repeatly
		if (this.getLogEntryByIndex(node.getNextIndex()) != null) {
			this.raftSendAppendEntriesMsg(node);
		}
		return 1;
	}

	public int raftRecvEntry(LogEntry entry, RaftEntryMsgResp resp) {
		if (entry.isVotingCfgChange()) {
			if (server.isVotingCfgChange()) {
				return RaftServerEntity.RAFT_ERROR_ONE_VOTING_CFG_CONLY;
			}
		}
		if (this.getServerState() != RaftServerState.LEADER) {
			return RaftServerEntity.RAFT_ERROR_LEADER_ONLY;
		}

		LogEntry et = new LogEntry();
		et.setTermId(this.getCurrentterm());
		et.setType(entry.getType());
		et.setData(entry.getData());
		et.setLogId(entry.getLogId());
		this.raftAppendEntry(et);

		for (RaftNodeEntity nd : server.getNodes()) {
			if (!nd.getVotingStatus() || nd.getNodeId() == server.getNodeId()) {
				continue;
			}
			long next_idx = nd.getNextIndex();
			/**
			 * send to nodes that is up-to-date with leader
			 */
			if (next_idx == this.getLastLogIndex()) {
				this.raftSendAppendEntriesMsg(nd);
			}
		}

		// commit log if only one node
		if (1 == this.getVotingNodesNums()) {
			this.setCurrentCommitIndex(this.getLastLogIndex());
		}
		resp.setId(et.getLogId());
		resp.setTerm(this.getLastLogTerm());
		resp.setIdx(this.getLastLogIndex());
		if (entry.isVotingCfgChange()) {
			server.setVotingCfgChangeIndex(this.getLastLogIndex());
		}
		return 0;
	}

	public int raftSendAppendEntriesToAll() {
		for (RaftNodeEntity nd : server.getNodes()) {
			if (nd.getNodeId() != server.getNodeId())
				this.raftSendAppendEntriesMsg(nd);
		}
		return 1;
	}

	public int raftPeriod(int misc_timeout_elapsed) {
		this.setElectionTimeElapsed(server.getElectionTimeElapsed() + misc_timeout_elapsed);
		if (this.getServerState() == RaftServerState.LEADER) {
			if (server.getRequestTimeOut() <= server.getElectionTimeElapsed()) {
				this.raftSendAppendEntriesToAll();
			}
		} else if (server.getElectionTimeOut() <= server.getElectionTimeElapsed()) {
			if (this.getVotingNodesNums() > 1) {
				this.electionStart(); // become candidate
			}
		}
		if (server.getLastApplied() < this.getCurrentCommitIndex()) {
			int ret = -1;
			if ((ret = this.raftApplyEntry()) < 0) {
				return ret;
			}
		}
		return 0;
	}

	public int raftApplyEntry() {
		if (server.getLastApplied() == this.getCurrentCommitIndex()) {
			return -1;
		}
		long applied_index = server.getLastApplied() + 1;
		LogEntry et = this.getLogEntryByIndex(applied_index);
		if (et == null) {
			return -1;
		}
		server.setLastApplied(server.getLastApplied() + 1);
		if (server.getHandler() != null) {
			server.getHandler().offerLogEntry(server, et);
		}
		if (applied_index == server.getVotingCfgChangeIndex()) {
			server.setVotingCfgChangeIndex(-1);
		}
		return 0;
	}

	public void setElectionTimeElapsed(long elapsed) {
		server.setElectionTimeElapsed(elapsed);
	}

	public void setCurrentLeader(RaftNodeEntity node) {
		server.setLeader(node);
	}

	public long getCurrentCommitIndex() {
		return server.getCommitIndex();
	}

	public void setCurrentCommitIndex(long idx) {
		assert(idx <= this.getLastLogIndex());
		server.setCommitIndex(idx);
	}

	public RaftServerState getServerState() {
		return server.getState();
	}

	public boolean isMajority(long part, long total) {
		boolean bret = false;
		if (total / 2 < part) {
			bret = true;
		}
		return bret;
	}

	public boolean isVoteMajority() {
		int node_nums = getVotingNodesNums();
		int vote_nums = getVoteNums();
		return this.isMajority(vote_nums, node_nums);
	}

	public int getVoteNums() {
		int vote_nums = 0;
		for (RaftNodeEntity nd : server.getNodes()) {
			if (nd.getVoteForMeStatus()) {
				vote_nums++;
			}
		}
		return vote_nums;
	}

	public int getVotingNodesNums() {
		int node_nums = 0;
		for (RaftNodeEntity nd : server.getNodes()) {
			if (nd.getVotingStatus()) {
				node_nums++;
			}
		}
		return node_nums;
	}

	public void setCurrentTerm(long term) {
		if (server.getCurrentTerm() < term) {
			server.setVoteFor(-1);
		}
		server.setCurrentTerm(term);
	}

	public long getCurrentterm() {
		return server.getCurrentTerm();
	}

	public void serverVoteForNode(RaftNodeEntity node) {
		if (node != null) {
			server.setVoteFor(node.getNodeId());
		}
		if (node.getNodeId() == server.getNodeId()) {
			node.setVoteForMe(true);
		}
	}

	public long getLastLogIndex() {
		return server.getCurrentLogIndex();
	}

	public LogEntry getLogEntryByIndex(long index) {
		return server.getLogEntryByIndex(index);
	}

	public List<LogEntry> getLogEntriesFromIndex(long index) {
		return server.getLogEntriesFromIndex(index);
	}

	public long getLastLogTerm() {
		return server.getTermByLogIndex(server.getCurrentLogIndex());
	}

	public long deleteLogEntriesFromIndex(long index) {
		return server.deleteLogEntriesFromIndex(index);
	}

	public int raftAppendEntry(LogEntry entry) {
		if (entry.isVotingCfgChange()) {
			server.setVotingCfgChangeIndex(this.getLastLogIndex());
		}
		if (entry.getLogId() == 0) {
			return -1;
		}
		int iret = -1;
		iret = server.appendEntry(entry);
		return iret;
	}

	public RaftNodeEntity getCurrentLeader() {
		return server.getLeader();
	}
}
