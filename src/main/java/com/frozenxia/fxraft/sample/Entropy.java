/**
 * @Title: Entropy.java
 * @date:Sep 26, 2016 4:58:32 PM
 * @Description:TODO
 */
package com.frozenxia.fxraft.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.frozenxia.fxraft.raft.LogEntry;
import com.frozenxia.fxraft.raft.RaftEntryMsgResp;
import com.frozenxia.fxraft.raft.RaftServerManager;
import com.frozenxia.fxraft.raft.RaftServerState;

/**
 *
 * @Description TODO
 * @date Sep 26, 2016 4:58:32 PM
 *
 */
public class Entropy {
	private RaftServerManager manager = new RaftServerManager();
	private int port;

	public Entropy(int port) {
		this.port = port;
	}

	public void start() {
		manager.setRaftHandler(new SimpleRaftPins(port));
	}

	public void setResult(Map<String, Object> result, int code, String msg, Object data) {
		result.put("code", code);
		result.put("msg", msg);
		result.put("data", data);
	}

	public Object getId() {
		Map<String, Object> result = new HashMap<String, Object>();
		if (manager.getServerState() != RaftServerState.LEADER) {
			this.setResult(result, -1, "not leader", manager.getCurrentLeader().getData());
			return result;
		}
		if (manager.getCurrentLeader() == null) {
			this.setResult(result, -1, "no leader", "");
			return result;
		}
		LogEntry current_entry = manager.getLogEntryByIndex(manager.getCurrentCommitIndex());
		long current_dt = (Long) current_entry.getData();
		long next_dt = current_dt + 1;

		LogEntry next_entry = new LogEntry();
		next_entry.setLogId(new Random().nextInt());
		next_entry.setData(next_dt);
		next_entry.setTermId(manager.getCurrentterm());
		next_entry.setType(LogEntry.RAFT_LOGTYPE_NORMAL);
		RaftEntryMsgResp resp = new RaftEntryMsgResp();
		manager.raftRecvEntry(next_entry, resp);
		return null;
	}
}
