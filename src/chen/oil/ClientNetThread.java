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
	DataOutputStream dout;//�����
	DataInputStream din;//������
	boolean flag = true;//�Ƿ�ѭ�������־
	public ClientNetThread(MainActivity activity){//������
		this.activity = activity;
	} 
	public void run(){
		try{//�������粢����
	        s = new Socket("59.64.156.176", 9999);
	        dout = new DataOutputStream(s.getOutputStream());
	        din = new DataInputStream(s.getInputStream());
		}
		catch(Exception e){//�����쳣
			e.printStackTrace();//��ӡ�쳣
		}
		while(flag){
			try{
				String msg=din.readUTF();//���շ���������������Ϣ
                System.out.println("server msg = " + msg);
				if(msg.startsWith("<#SEARCHINFO#>")){//��ʳ�������
					msg=msg.substring(14);//��ȡ�Ӵ�
					String[][] strs = new String[Integer.parseInt(msg)][7];
					ArrayList oilInfos = new ArrayList();
					for(int i=0; i<strs.length; i++){
						String temp = din.readUTF();
						System.out.println(temp);
						String[] str = temp.split("\\|");//�ָ��ַ���
						oilInfos.add(str);
					}
					
					
					Message message = new Message();//������Ϣ
					Bundle data = new Bundle();//��������
					data.putStringArrayList("oilInfo",oilInfos);//��bundle���������
					message.what = 2;//������Ϣ��whatֵ
					message.setData(data);//�������
					activity.myHandler.sendMessage(message);//������Ϣ
				}
				else if(msg.startsWith("<#FAVOURITEINFO#>")){//�ҵ��ղ��������
					msg=msg.substring(17);//��ȡ�Ӵ�
					String[][] strs = new String[Integer.parseInt(msg)][7];
					ArrayList oilInfos = new ArrayList();
					for(int i=0; i<strs.length; i++){
						String temp = din.readUTF();
						System.out.println(temp);
						String[] str = temp.split("\\|");//�ָ��ַ���
						oilInfos.add(str);
					}
					
					Message message = new Message();//������Ϣ
					Bundle data = new Bundle();//��������
					data.putStringArrayList("oilInfo",oilInfos);//��bundle���������
					message.what = 1;//������Ϣ��whatֵ
					message.setData(data);//�������
					activity.myHandler.sendMessage(message);//������Ϣ					
				}
				else if(msg.startsWith("<#RECOMMENDINFO#>")){//�Ƽ�
					msg=msg.substring(17);//��ȡ�Ӵ�
					String[][] strs = new String[Integer.parseInt(msg)][7];
					ArrayList oilInfos = new ArrayList();
					for(int i=0; i<strs.length; i++){
						String temp = din.readUTF();
						System.out.println(temp);
						String[] str = temp.split("\\|");//�ָ��ַ���
						oilInfos.add(str);
					}
					
					Message message = new Message();//������Ϣ
					Bundle data = new Bundle();//��������
					data.putStringArrayList("oilInfo",oilInfos);//��bundle���������
					message.what = 3;//������Ϣ��whatֵ
					message.setData(data);//�������
					activity.myHandler.sendMessage(message);//������Ϣ						
				}
				else if(msg.startsWith("<#OILSORTINFO#>")){//��������
					msg=msg.substring(15);//��ȡ�Ӵ�
					String[] type = msg.split("\\|");
					int size = Integer.parseInt(type[0]);
					ArrayList oilSorts = new ArrayList();
					for(int i=0; i<size; i++){
						String temp = din.readUTF();
						Log.d("a", temp);
						String[] str = temp.split("\\|");//�ָ��ַ���
						oilSorts.add(str);
					}					
					Message message = new Message();//������Ϣ
					Bundle data = new Bundle();//��������
					data.putStringArrayList("oilSorts",oilSorts);//��bundle���������
					if(Integer.parseInt(type[1]) == 1){
						message.what = 8;//������Ϣ��whatֵ
					}
					else {
						message.what = 7;//������Ϣ��whatֵ
					}
					message.setData(data);//�������
					activity.myHandler.sendMessage(message);//������Ϣ						
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
			catch(Exception e){//�����쳣
				e.printStackTrace();//��ӡ�쳣��Ϣ 
			}
		}
	}
}
