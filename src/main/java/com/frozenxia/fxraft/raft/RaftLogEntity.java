/**
 * @Title: RaftLogEntity.java
 * @date:Sep 21, 2016 10:47:31 AM
 * @Description:TODO
 */
package com.frozenxia.fxraft.raft;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @Description TODO
 * @date Sep 21, 2016 10:47:31 AM
 *
 */
public class RaftLogEntity {
	private List<LogEntry> logs;

	public int appendLogEntry(LogEntry et) {
		logs.add(et);
		return 1;
	}

	public long getCurrentLogIndex() {
		return logs.size();
	}

	public LogEntry getLogEntryByIndex(long index) {
		int idx = (int) index;
		LogEntry et = null;
		if (idx > 0 && idx <= getCurrentLogIndex()) {
			et = logs.get(idx - 1);
		}
		return et;
	}

	public List<LogEntry> getLogEntriesFromIndex(long index) {
		int idx = (int) index;
		List<LogEntry> et = new ArrayList<LogEntry>();
		if (idx > 0 && idx <= getCurrentLogIndex()) {
			for (int i = idx - 1; i < logs.size(); i++) {
				et.add(logs.get(i));
			}
		}
		return et;
	}

	public long deleteLogEntriesFromIndex(long index) {
		int idx = (int) index;
		long counts = 0;
		if (idx > 0 && idx <= getCurrentLogIndex()) {
			while (idx <= getCurrentLogIndex()) {
				logs.remove(idx - 1);
				counts++;
			}
		}
		return counts;
	}

	public long getTermByLogIndex(long index) {
		int idx = (int) index;
		long lret = -1;
		if (idx > 0 && idx <= getCurrentLogIndex()) {
			LogEntry et = logs.get(idx - 1);
			lret = et.getTermId();
		}
		return lret;
	}
}
