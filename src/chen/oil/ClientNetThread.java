package chen.oil;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.Socket;
import java.util.ArrayList;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;


public class ClientNetThread extends Thread{
	MainActivity activity = null;
	Socket s = null;
	DataOutputStream dout;//输出流
	DataInputStream din;//输入流
	boolean flag = true;//是否循环处理标志
	public ClientNetThread(MainActivity activity){//构造器
		this.activity = activity;
	} 
	public void run(){
		try{//连接网络并打开流
	        s = new Socket("59.64.156.176", 9999);
	        dout = new DataOutputStream(s.getOutputStream());
	        din = new DataInputStream(s.getInputStream());
		}
		catch(Exception e){//捕获异常
			e.printStackTrace();//打印异常
		}
		while(flag){
			try{
				String msg=din.readUTF();//接收服务器发送来的消息
                System.out.println("server msg = " + msg);
				if(msg.startsWith("<#SEARCHINFO#>")){//美食搜索结果
					msg=msg.substring(14);//截取子串
					String[][] strs = new String[Integer.parseInt(msg)][7];
					ArrayList oilInfos = new ArrayList();
					for(int i=0; i<strs.length; i++){
						String temp = din.readUTF();
						System.out.println(temp);
						String[] str = temp.split("\\|");//分割字符串
						oilInfos.add(str);
					}
					
					
					Message message = new Message();//创建消息
					Bundle data = new Bundle();//创建数据
					data.putStringArrayList("oilInfo",oilInfos);//向bundle中添加数据
					message.what = 2;//设置消息的what值
					message.setData(data);//存放数据
					activity.myHandler.sendMessage(message);//发送消息
				}
				else if(msg.startsWith("<#FAVOURITEINFO#>")){//我的收藏搜索结果
					msg=msg.substring(17);//截取子串
					String[][] strs = new String[Integer.parseInt(msg)][7];
					ArrayList oilInfos = new ArrayList();
					for(int i=0; i<strs.length; i++){
						String temp = din.readUTF();
						System.out.println(temp);
						String[] str = temp.split("\\|");//分割字符串
						oilInfos.add(str);
					}
					
					Message message = new Message();//创建消息
					Bundle data = new Bundle();//创建数据
					data.putStringArrayList("oilInfo",oilInfos);//向bundle中添加数据
					message.what = 1;//设置消息的what值
					message.setData(data);//存放数据
					activity.myHandler.sendMessage(message);//发送消息					
				}
				else if(msg.startsWith("<#RECOMMENDINFO#>")){//推荐
					msg=msg.substring(17);//截取子串
					String[][] strs = new String[Integer.parseInt(msg)][7];
					ArrayList oilInfos = new ArrayList();
					for(int i=0; i<strs.length; i++){
						String temp = din.readUTF();
						System.out.println(temp);
						String[] str = temp.split("\\|");//分割字符串
						oilInfos.add(str);
					}
					
					Message message = new Message();//创建消息
					Bundle data = new Bundle();//创建数据
					data.putStringArrayList("oilInfo",oilInfos);//向bundle中添加数据
					message.what = 3;//设置消息的what值
					message.setData(data);//存放数据
					activity.myHandler.sendMessage(message);//发送消息						
				}
				else if(msg.startsWith("<#OILSORTINFO#>")){//加油种类
					msg=msg.substring(15);//截取子串
					String[] type = msg.split("\\|");
					int size = Integer.parseInt(type[0]);
					ArrayList oilSorts = new ArrayList();
					for(int i=0; i<size; i++){
						String temp = din.readUTF();
						Log.d("a", temp);
						String[] str = temp.split("\\|");//分割字符串
						oilSorts.add(str);
					}					
					Message message = new Message();//创建消息
					Bundle data = new Bundle();//创建数据
					data.putStringArrayList("oilSorts",oilSorts);//向bundle中添加数据
					if(Integer.parseInt(type[1]) == 1){
						message.what = 8;//设置消息的what值
					}
					else {
						message.what = 7;//设置消息的what值
					}
					message.setData(data);//存放数据
					activity.myHandler.sendMessage(message);//发送消息						
				}
			}
			catch(EOFException e){
				try{
					if(din != null){
						din.close();
						din = null;
					}
				}
				catch(Exception ea){
					ea.printStackTrace();
				}
				try{
					if(dout != null){
						dout.close();
						dout = null;
					}
				}
				catch(Exception ea){
					ea.printStackTrace();
				}
				try{
					if(s != null){
						s.close();
						s = null;
					}
				}
				catch(Exception ea){
					ea.printStackTrace();
				}		
				flag = false;
			}
			catch(Exception e){//捕获异常
				e.printStackTrace();//打印异常信息 
			}
		}
	}
}
