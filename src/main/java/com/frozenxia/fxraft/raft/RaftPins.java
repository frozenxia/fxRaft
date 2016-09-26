/**
 * @Title: RaftHandler.java
 * @date:Sep 22, 2016 3:08:47 PM
 * @Description:TODO
 */
package com.frozenxia.fxraft.raft;

/**
 *
 * @Description TODO
 * @date Sep 22, 2016 3:08:47 PM
 *
 */
public interface RaftPins {
	void sendRequestVote(RaftNodeEntity node, RaftRequestForVoteMsg msg);
	void sendAppendEntriesMsg(RaftNodeEntity node, RaftAppendEntriesMsg msg);
	int offerLogEntry(RaftServerEntity server, LogEntry log);
}
