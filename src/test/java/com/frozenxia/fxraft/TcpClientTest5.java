/**
 * @Title: TcpClientTest5.java
 * @date:Sep 28, 2016 3:53:53 PM
 * @Description:TODO
 */
package com.frozenxia.fxraft;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.frozenxia.fxraft.sample.TcpClient;

import io.netty.channel.Channel;

/**
 *
 * @Description TODO
 * @date Sep 28, 2016 3:53:53 PM
 *
 */
public class TcpClientTest5 {
	public static void main(String[] args) throws Exception {
		TcpClient client = new TcpClient();
		Channel ch = client.connect(9080, "localhost");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		class TT {
			public int it;
			public String st;
		}
		TT tt = new TT();
		tt.it = 12;
		tt.st = "just test";
		ch.writeAndFlush(tt.toString()).sync();
		while (true) {
			ch.writeAndFlush(in.readLine());
		}
	}
}
