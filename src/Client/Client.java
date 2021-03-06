package Client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import Server.Server;
import Utilities.IConnection;
import Utilities.Names;

public class Client {
	IConnection connection;
	String teamName;
	int id;
	
	public class client implements Runnable{
		int count = 1;
		long latency = 0;
		int rate;
		public client(int rate){
			this.rate = rate;
		}
		public void run(){
			while(true){
				long tmp = System.nanoTime();
				getMetalTally(teamName);
				getScore(Names.events[0]);
				latency += System.nanoTime() - tmp;
				count++;
				if(count%10 == 0)
					System.out.println("latency "+(latency/count));
				try {
				Thread.sleep(rate);
				} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
			
			
		}
	}
	public Client(String team, int id, int rate, String address){
		this.id = id;
		this.teamName = team;
		
		try {
			connection = (IConnection) Naming.lookup(address);
			System.out.println("Client " +id + " connected to server......");
			client clientThread = new client(rate);
			Thread t = new Thread(clientThread);
			
			t.start();
//			Thread.sleep(rate*1000);
//			getScore(Names.events[0]);
//			System.out.println("finish");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	public void getMetalTally(String teamName){
		try {
			System.out.println("Client "+ id + ": " + connection.getMedalTally(teamName));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getScore(String eventType){
		try {
			System.out.println("Client "+ id + ": " + connection.getScore(eventType));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		if(args.length < 2){
			System.out.println("args: number of clients, pulling rate(ms), server address");
			return;
		} if (args.length == 2){
			for(int i = 0; i < Integer.parseInt(args[0]); i++){
				new Client(Names.teamNames[i%6], i, Integer.parseInt(args[1]), "rmi://localhost:10001/Olympic");
			}
		} if (args.length == 3){
			for(int i = 0; i < Integer.parseInt(args[0]); i++){
				new Client(Names.teamNames[i%6], i, Integer.parseInt(args[1]), "rmi://"+args[2]+":10001/Olympic");
			}
		} 
		
	}
		
}
