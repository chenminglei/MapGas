package chen.oil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends Activity  implements OnItemClickListener, OnClickListener{
	int span = 5;//ÿҳ������
	int currentPageNo = 1;
	int totleNumber = 0;
	
	String infoValues;
	int searchSort;
	String startPrice;
	String endPrice;
	int uid;
	
	Socket s = null;
	DataOutputStream dout;//�����
	DataInputStream din;//������
	ArrayList oilInfos = null;//��ʳ��Ϣ
	//ArrayList mstxImages = null;//��ʳͼƬ
	ListView lv;
	
	ImageButton previousButton;//��һҳ��ť
	ImageButton next_button;//��һҳ��ť
	ImageButton back_button;//���ذ�ť
	
	TextView searchResultTextView;
	
	public Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {//��д�ķ������ڽ���Handler��Ϣ
			if(msg.what == 1){
				Bundle data = msg.getData();
				oilInfos = (ArrayList)data.get("oilInfo");
				//mstxImages = (ArrayList)data.get("mstxImages");
		        //ΪListView׼������������
				MyBaseAdapter mba = new MyBaseAdapter(oilInfos);
			    lv.setAdapter(mba);//ΪListView��������������	
			    mba.notifyDataSetChanged();
			}
		}
	};
	
    public void onCreate(Bundle savedInstanceState) {//Activity����ʱ������
        super.onCreate(savedInstanceState);
		//ȫ��
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Intent intent = this.getIntent();//�õ�Intent
		Bundle bundle = intent.getExtras();//�õ�Intent�е�bundle
		
		infoValues = bundle.getString("infoValues");
		searchSort = bundle.getInt("searchSort");
		startPrice = bundle.getString("startPrice");
		endPrice = bundle.getString("endPrice");
		uid = bundle.getInt("uid");
		new Thread(){
			public void run(){
				try{//�������粢����
			        s = new Socket("59.64.156.176", 9999);
			        dout = new DataOutputStream(s.getOutputStream());
			        din = new DataInputStream(s.getInputStream());
			        sendToServerMsg();
				}
				catch(Exception e){//�����쳣
					e.printStackTrace();//��ӡ�쳣
				}			
			}
		}.start();

		this.setContentView(R.layout.search_result);
		
		previousButton = (ImageButton)findViewById(R.id.previousButton);
		next_button = (ImageButton)findViewById(R.id.nextButton);
		back_button = (ImageButton)findViewById(R.id.backButton);
		previousButton.setOnClickListener(this);
		next_button.setOnClickListener(this);
		back_button.setOnClickListener(this);
		
		searchResultTextView = (TextView)findViewById(R.id.searchResultTextView);
		searchResultTextView.setText("������ҵ���"+infoValues+"�������ʳ"+totleNumber+"����");
		lv = (ListView)findViewById(R.id.searchResultListView);
		lv.setOnItemClickListener(SearchActivity.this);
		
	    previousButton.setEnabled(false);
	    if((currentPageNo*span)>=totleNumber){
	    	next_button.setEnabled(false);
	    }
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == back_button){//���ذ�ť
			this.finish();
		}
		else if(v == previousButton){//��һҳ
			currentPageNo = currentPageNo - 1;
			if(currentPageNo == 1){
				previousButton.setEnabled(false);
			}
		    if((currentPageNo*span) < totleNumber){
		    	next_button.setEnabled(true);
		    }
		    sendToServerMsg();
		}
		else if(v == next_button){//��һҳ
			currentPageNo = currentPageNo + 1;
			if(currentPageNo != 1){
				previousButton.setEnabled(true);
			}
		    if((currentPageNo*span) >= totleNumber){
		    	next_button.setEnabled(false);
		    }
		    sendToServerMsg();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		String[] oilStrs = (String[]) oilInfos.get(arg2);
		Intent intent = new Intent();//����Intent
		intent.setClass(SearchActivity.this, InfoActivity.class);
		Bundle bundle = new Bundle();//��������Bundle
		bundle.putInt("action", 3);
		bundle.putInt("uid", uid);
		bundle.putStringArray("oilInfo", oilStrs);
		intent.putExtras(bundle);//�������
		startActivity(intent);//����Activity
	}

	
	public class MyBaseAdapter extends BaseAdapter{
		ArrayList oilInfos = null;
        public MyBaseAdapter(ArrayList oilInfos) {//������
            super();   
            this.oilInfos = oilInfos;    
        } 
		@Override
		public int getCount() {//�ܹ����ٸ�ѡ��
			return oilInfos.size();
		}
		@Override
		public Object getItem(int arg0) {
			return null;
		}
		@Override
		public long getItemId(int arg0) {
			return 0;
		}
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {//��̬����ÿ���������Ӧ��View
			//��ʼ��LinearLayout
			LinearLayout linearLayout1 = new LinearLayout(SearchActivity.this);
			linearLayout1.setOrientation(LinearLayout.HORIZONTAL);//���ó���	
			linearLayout1.setPadding(5,5,5,5);//������������

			LinearLayout linearLayout2 = new LinearLayout(SearchActivity.this);
			linearLayout2.setOrientation(LinearLayout.VERTICAL);//���ó���	
			linearLayout2.setPadding(5,1,5,1);//������������
			linearLayout2.setGravity(Gravity.TOP);//���ö��䷽ʽ

			//��ʼ��TextView,��ʳ����
			TextView title=new TextView(SearchActivity.this);
			String[] oilStrs = (String[]) oilInfos.get(arg0);
			title.setText(oilStrs[0]);//��������
			title.setTextSize(18);//���������С
			title.setTextColor(Color.RED);
			title.setPadding(5,1,5,1);//������������
			title.setMaxLines(1);//��������
			title.setGravity(Gravity.LEFT);//���뷽ʽ
		    linearLayout2.addView(title);//��ӵ�LinearLayout��
		    
			//��ʼ��TextView
			TextView date = new TextView(SearchActivity.this);
			date.setText("�۸�"+oilStrs[2]);
			date.setTextSize(10);//���������С
			date.setTextColor(Color.GRAY);//���ֵ���ɫ
			date.setPadding(5,1,5,1);//������������
			title.setMaxLines(1);//��������
			date.setGravity(Gravity.LEFT);//���뷽ʽ
		    linearLayout2.addView(date);//��ӵ�LinearLayout��
		    
			//��ʼ��TextView
			TextView info_dis = new TextView(SearchActivity.this);
			info_dis.setText("����"+oilStrs[3]);
			info_dis.setTextSize(13);//���������С
			info_dis.setTextColor(Color.BLACK);//���ֵ���ɫ
			info_dis.setPadding(5,1,5,1);//������������
			info_dis.setGravity(Gravity.LEFT);//���뷽ʽ
			info_dis.setMaxLines(3);//�������
		    linearLayout2.addView(info_dis);//��ӵ�LinearLayout��
		    linearLayout1.addView(linearLayout2);
			return linearLayout1;
		}
	}

    public void sendToServerMsg(){//�����������������Ϣ
    	new Thread(){
    		public void run(){
    	    	try{
    	    		dout.writeUTF("<#SEARCH#>"+infoValues+"|"+searchSort+"|"+startPrice+"|"+endPrice+"|"+span+"|"+currentPageNo);//�����������������Ϣ
    	    		String msg=din.readUTF();//���շ���������������Ϣ
					if(msg.startsWith("<#SEARCHINFO#>")){//��ʳ�������
						msg=msg.substring(14);//��ȡ�Ӵ�
						String[] number = msg.split("\\|");//�ָ��ַ���
						System.out.println("searchactivity"+number[1]);
						totleNumber = Integer.parseInt(number[1]);//�õ�����������ӵ��ܸ���
						String[][] strs = new String[Integer.parseInt(number[0])][9];
						ArrayList oilInfos = new ArrayList();
						for(int i=0; i<strs.length; i++){
							String temp = din.readUTF();
							System.out.println(temp);
							String[] str = temp.split("\\|");//�ָ��ַ���
							oilInfos.add(str);
						}
						
						searchResultTextView.setText("������ҵ���"+infoValues+"�������ʳ"+totleNumber+"����");
						Message message = new Message();//������Ϣ
						Bundle data = new Bundle();//��������
						data.putStringArrayList("oilInfo",oilInfos);//��bundle���������
						message.what = 1;//������Ϣ��whatֵ
						message.setData(data);//�������
						myHandler.sendMessage(message);//������Ϣ
					}
    	    	}
    	    	catch(Exception e){
    	    		e.printStackTrace();
    	    	}  			
    		}
    	}.start();
    }
}