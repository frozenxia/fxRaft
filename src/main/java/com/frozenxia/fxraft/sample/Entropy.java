/**
 * @Title: Entropy.java
 * @date:Sep 26, 2016 4:58:32 PM
 * @Description:TODO
 */
package com.frozenxia.fxraft.sample;

import java.util.HashMap;
import java.util.Map;

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
		return null;
	}
}
