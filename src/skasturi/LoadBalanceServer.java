/**
 * Copyright 2012 <i>Sasidhar Kasturi</i>
 *
 * This file ALoadBalanceServer.java is part of the project LoadBalancer.
 * Project LoadBalancer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Project LoadBalancer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with the project LoadBalancer. If not, see <http://www.gnu.org/licenses/>. 
 **/
package skasturi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalanceServer {
	ServerSocket server = null;
	public static final AtomicInteger next = new AtomicInteger(0);
	long last;

	public void listenSocket() {
		try {
			server = new ServerSocket(PropertyKeys.SERVER_PORT);
		} catch (IOException e) {
			System.out.println("Could not listen on port " + PropertyKeys.SERVER_PORT);
			System.exit(-1);
		}
		while (true) {
			ClientWorker w;
			try {
				w = new ClientWorker(server.accept());
				final long now = System.currentTimeMillis();
				if (now - last > TimeUnit.MINUTES.toMillis(1l)) {
					last = now;
					System.out.println("Processed: " + next.get());
				}
				Thread t = new Thread(w);
				t.start();
			} catch (IOException e) {
				System.out
						.println("Accept failed: " + PropertyKeys.SERVER_PORT);
				System.exit(-1);
			}
		}
	}

	protected void finalize() {
		try {
			server.close();
		} catch (IOException e) {
			System.out.println("Could not close socket");
			System.exit(-1);
		}
	}

	class ClientWorker implements Runnable {
		private Socket client;

		ClientWorker(Socket client) {
			this.client = client;
		}

		public void run() {
			String line;
			BufferedReader in = null;
			PrintWriter out = null;

			try {
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				out = new PrintWriter(client.getOutputStream(), true);
			} catch (IOException e) {
				System.out.println("in or out failed");
				System.exit(-1);
			}
			try {
				line = in.readLine();
				if (line.compareTo("next") == 0) {
					final int next = LoadBalanceServer.next.getAndIncrement();
					System.out.println(client.getInetAddress() + " working on " + next);
					out.println(Integer.toString(next));
				}
				in.close();
				out.close();
			} catch (IOException e) {
				System.out.println("Read failed");
				System.exit(-1);
			}
		}
	}

	public static void main(String[] args) {
		LoadBalanceServer server = new LoadBalanceServer();
		server.listenSocket();
	}

}
