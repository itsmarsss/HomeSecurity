package com.marsss.homesecurity;

import java.util.Scanner;

public class RunOnceServer extends Thread {

	private boolean online = true;
	
	@Override
	public void run() {
		Scanner sc = new Scanner(System.in);
		String cmd = sc.nextLine().toLowerCase();
		while(true) {
			switch(cmd) {
			case "shutdown":
				sc.close();
				//System.exit(0);
			case "disable":
				setOnline(false);
			case "enable":
				setOnline(true);
			case "toggle":
				setOnline(!isOnline());
			case "connections":
				String param = cmd.replace("connections", "");
				printConnections(param);
			case "viewlogs":
				printLogDir();
			}
			cmd = sc.nextLine().toLowerCase();
		}
	}

	private void printLogDir() {
		// TODO Auto-generated method stub
		
	}

	private void printConnections(String param) {
		// TODO Auto-generated method stub
		
	}

	private boolean isOnline() {
		return online;
	}

	private void setOnline(boolean b) {
		online = b;
	}

}
