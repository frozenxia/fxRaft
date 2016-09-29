/**
 * @Title: TcpTest.java
 * @date:Sep 28, 2016 11:00:07 AM
 * @Description:TODO
 */
package com.frozenxia.fxraft;

import com.frozenxia.fxraft.sample.TcpClient;
import com.frozenxia.fxraft.sample.TcpServer;

/**
 *
 * @Description TODO
 * @date Sep 28, 2016 11:00:07 AM
 *
 */
public class TcpTest {
	public static void main(String[] args) throws InterruptedException {
		TcpServer server = new TcpServer();
		server.bind(9080);
		// Thread.sleep(5000);
		// server.shutdown();
	}
}
