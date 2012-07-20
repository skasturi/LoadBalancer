/**
* Copyright 2012 <i>Sasidhar Kasturi</i>
*
* This file LoadBalanceClient.java is part of the project LoadBalancer.
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
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author sasidhark
 */
public class LoadBalanceClient {
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	final int countTill;

	public LoadBalanceClient(final int countFill) {
		this.countTill = countFill;
	}

	public int getNext() throws Exception {
		try {
			socket = new Socket(PropertyKeys.SERVER_HOSTNAME, PropertyKeys.SERVER_PORT);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: " + PropertyKeys.SERVER_HOSTNAME);
		} catch (IOException e) {
			System.out.println("No I/O");
			throw e;
		}
		out.println("next");
		final int next = Integer.parseInt(in.readLine());
		in.close();
		out.close();
		socket.close();
		if (next < countTill) {
			return next;
		} else {
			return -1;
		}
	}
	
	public static void main(String[] args) throws Exception {
        LoadBalanceClient client = new LoadBalanceClient(10);         
        System.out.println(client.getNext());           
}

}
