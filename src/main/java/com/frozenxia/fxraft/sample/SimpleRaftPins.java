/**
 * @Title: SimpleRaftPins.java
 * @date:Sep 29, 2016 3:04:20 PM
 * @Description:TODO
 */
package com.frozenxia.fxraft.sample;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.frozenxia.fxraft.raft.LogEntry;
import com.frozenxia.fxraft.raft.RaftAppendEntriesMsg;
import com.frozenxia.fxraft.raft.RaftNodeEntity;
import com.frozenxia.fxraft.raft.RaftPins;
import com.frozenxia.fxraft.raft.RaftRequestForVoteMsg;
import com.frozenxia.fxraft.raft.RaftServerEntity;

/**
 *
 * @Description TODO
 * @date Sep 29, 2016 3:04:20 PM
 *
 */
public class SimpleRaftPins implements RaftPins {
	Map<Long, TcpClient> tcpClients = new HashMap<Long, TcpClient>();
	TcpServer server = new TcpServer();
	public SimpleRaftPins(int port) {
		try {
			server.bind(port);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.frozenxia.fxraft.raft.RaftPins#sendRequestVote(com.frozenxia.fxraft.
	 * raft.RaftNodeEntity, com.frozenxia.fxraft.raft.RaftRequestForVoteMsg)
	 */

	private TcpClient getClient(RaftNodeEntity node) {
		TcpClient client = null;
		long nodeId = node.getNodeId();
		if (tcpClients.containsKey(nodeId)) {
			client = tcpClients.get(nodeId);
		} else {
			TcpNodeInfo info = (TcpNodeInfo) node.getData();
			try {
				TcpClient cc = new TcpClient();
				cc.connect(info.getPort(), info.getAddress());
				client = cc;
				tcpClients.put(node.getNodeId(), cc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}

	public String serializeObject(Object value) {
		ObjectMapper mapper = new ObjectMapper();
		String sret = value.toString();
		try {
			sret = mapper.writeValueAsString(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sret;
	}

	public void sendRequestVote(RaftNodeEntity node, RaftRequestForVoteMsg msg) {
		// TODO Auto-generated method stub
		TcpClient client = getClient(node);
		client.sendMsg(serializeObject(msg));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.frozenxia.fxraft.raft.RaftPins#sendAppendEntriesMsg(com.frozenxia.
	 * fxraft.raft.RaftNodeEntity,
	 * com.frozenxia.fxraft.raft.RaftAppendEntriesMsg)
	 */
	public void sendAppendEntriesMsg(RaftNodeEntity node, RaftAppendEntriesMsg msg) {
		// TODO Auto-generated method stub
		TcpClient client = getClient(node);
		client.sendMsg(serializeObject(msg));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.frozenxia.fxraft.raft.RaftPins#offerLogEntry(com.frozenxia.fxraft.
	 * raft.RaftServerEntity, com.frozenxia.fxraft.raft.LogEntry)
	 */
	public int offerLogEntry(RaftServerEntity server, LogEntry log) {
		// TODO Auto-generated method stub
		return 0;
	}

}
