/**
 * @Title: RaftTest.java
 * @date:Sep 26, 2016 5:18:49 PM
 * @Description:TODO
 */
package com.frozenxia.fxraft;

import com.frozenxia.fxraft.raft.RaftRequestForVoteMsgResp;
import com.frozenxia.fxraft.raft.RaftServerEntity;
import com.frozenxia.fxraft.raft.RaftServerManager;
import com.frozenxia.fxraft.raft.RaftServerState;

/**
 *
 * @Description TODO
 * @date Sep 26, 2016 5:18:49 PM
 *
 */
public class RaftTest {

	public void testRaft_state_trans() {
		RaftServerManager manager = new RaftServerManager();
		RaftServerEntity server = manager.getServer();
		manager.addVotingNode("node1", 1, true);
		manager.addVotingNode("node2", 2, false);
		manager.addVotingNode("node2", 3, false);
		manager.addVotingNode("node2", 4, false);
		manager.addVotingNode("node2", 5, false);
		assert(manager.getServerState() == RaftServerState.FOLLOWER);
		// manager.setCurrentTerm(1);
		assert(manager.getCurrentLeader() == null);
		assert(server.getNodes().size() == 5);
		assert(server.getNodeId() == 1);
		manager.becomeCandidate();
		assert(server.getCurrentTerm() == 1);

		assert(manager.getVoteNums() == 1);

		RaftRequestForVoteMsgResp resp = new RaftRequestForVoteMsgResp();
		resp.setTerm(1);
		resp.setVoteGranted(RaftRequestForVoteMsgResp.GRANTED_SUCCESS);
		manager.recvRequestVoteMsgResp(server.getNodeById(2), resp);
		assert(manager.getVoteNums() == 2);
		assert(manager.getCurrentLeader() == null);
		manager.recvRequestVoteMsgResp(server.getNodeById(3), resp);

		assert(manager.getVoteNums() == 3);
		assert(manager.getCurrentLeader() == server.getNodeById(1));
		assert(manager.getServerState() == RaftServerState.LEADER);
	}
}
