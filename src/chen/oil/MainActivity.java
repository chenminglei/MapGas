package chen.oil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends TabActivity implements OnClickListener, OnItemClickListener{
	static Bitmap bitmap;
	private TabHost myTabhost;//����tabhost������
	
	ImageButton searchButton = null;//������ť
	ImageButton updateButton = null;//�ϴ���ť
	
	EditText searchEditText;//�����ؼ���
	ClientNetThread clientNetThread = null;
	ListView lv;//��Ϣ�б�
	ArrayList oilInfos = null; //����վ��Ϣ
	List oilSorts = null;
	int uid;
	
	EditText infoPrice1 = null;
	EditText infoPrice2 = null;
	Spinner oilSort = null;   //������������
	
	//�ϴ���������ؼ�
	EditText jyzName = null;
	Spinner oilSort2 = null;   //��������
	EditText oilPrice = null;    //�ͼ�
	EditText oilLocation = null;  //����վλ��
	EditText oilCompName = null;      //����վ��˾
    EditText oilDis = null;     //��������
	
	ProgressDialog myDialog = null;
	
	double lat;  //����վ��γ��
	double lon;
	
	String titleStr = "";    //������ַ�����
	int place = 1;
	//�����handler
	public Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {//��д�ķ������ڽ���Handler��Ϣ
			super.handleMessage(msg);
			if(msg.what == 1 || msg.what == 2 ||msg.what == 3){//�õ���ʳ�������
				Bundle data = msg.getData();
				oilInfos = (ArrayList)data.get("oilInfo");
				//mstxImages = (ArrayList)data.get("mstxImages");
		        //ΪListView׼������������
				MyBaseAdapter mba = new MyBaseAdapter(oilInfos);
			    lv.setAdapter(mba);//ΪListView��������������	
			    mba.notifyDataSetChanged();
			}
			else if(msg.what == 4){//������������ʱ
				MainActivity.this.setTitle(titleStr.substring(0, place));
			}
			else if(msg.what == 5){//����������ȥʱ
				MainActivity.this.setTitle(titleStr.substring(place, titleStr.length()));
			}
			else if(msg.what == 6){//�����ʳ�ϴ�����
				jyzName.setText("");
				oilDis.setText("");
				oilCompName.setText("");
				oilLocation.setText("����һ�ȡ��γ��");
				oilPrice.setText("");			
			}
			else if(msg.what == 7){//��ʳ����
				Bundle data = msg.getData();
				oilSorts = (ArrayList)data.get("oilSorts");
				BaseAdapter ba = new BaseAdapter(){						//ΪSpinner׼������������
					@Override
					public int getCount() {
						return oilSorts.size();
					}
					@Override
					public Object getItem(int arg0) {//��д��getItem����				
						return null; 
					}
					@Override
					public long getItemId(int arg0) {//��д��getItemId����
						return 0; 
					}
					@Override
					public View getView(int arg0, View arg1, ViewGroup arg2) {
						//��ʼ��LinearLayout
						LinearLayout ll=new LinearLayout(MainActivity.this);
						ll.setOrientation(LinearLayout.HORIZONTAL);	//���ó���	
						String[] str = (String[])oilSorts.get(arg0);	
						//��ʼ��TextView
						TextView tv=new TextView(MainActivity.this);						
						tv.setText(str[1]);//��������
						tv.setTextColor(R.color.textword2);
						tv.setTextSize(20);
						ll.addView(tv);	//��ӵ�LinearLayout��						
						return ll;//��LinearLayout����
					}
				};
				oilSort2.setAdapter(ba);
		        ba.notifyDataSetChanged();
			}
			else if(msg.what == 8){//�����������ʳ����
				Bundle data = msg.getData();
				oilSorts = (ArrayList)data.get("oilSorts");
				BaseAdapter ba = new BaseAdapter(){						//ΪSpinner׼������������
					@Override
					public int getCount() {
						return oilSorts.size()+1;
					}
					@Override
					public Object getItem(int arg0) {//��д��getItem����				
						return null; 
					}
					@Override
					public long getItemId(int arg0) {//��д��getItemId����
						return arg0; 
					}

                    @Override
					public View getView(int arg0, View arg1, ViewGroup arg2) {
						String[] str;
						if(arg0 == 0){
							str = new String[]{"-1","��������"};
						}
						else {
							str = (String[])oilSorts.get(arg0-1);
						}
						//��ʼ��LinearLayout
						LinearLayout ll=new LinearLayout(MainActivity.this);
						ll.setOrientation(LinearLayout.HORIZONTAL);	//���ó���	
							
						//��ʼ��TextView
						TextView tv=new TextView(MainActivity.this);						
						tv.setText(str[1]);//��������
						tv.setTextColor(R.color.textword2);
						tv.setTextSize(20);
						ll.addView(tv);	//��ӵ�LinearLayout��						
						return ll;//��LinearLayout����
					}
				};
				oilSort.setAdapter(ba);
		        ba.notifyDataSetChanged();				
			}
		}
	};
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ballon);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		clientNetThread = new ClientNetThread(this);//��ʼ�����紦���߳�
		clientNetThread.start();//�����߳�
		
		Intent intent = this.getIntent();//����һ��Intent
		Bundle bundle = intent.getExtras();//�õ�������ݵ�Bundle
		String u_name = bundle.getString("u_name");//�õ�Bundle�е�u_name����
		uid = Integer.parseInt(bundle.getString("uid"));//�õ��û����
		titleStr = u_name+", ����С���ֻ�ӭ��������ID��Ϊ"+ uid;
		
		new Thread(){ //������߳�
			public void run(){
				boolean control = true;
				while(true){
					if(control){//����ʱ
						myHandler.sendEmptyMessage(4);
						try{
							Thread.sleep(300);//˯��300����
						}
						catch(Exception e){//�����쳣 
							e.printStackTrace();//��ӡ�쳣
						}
						if(place >= titleStr.length()){
							place = 1;
							control = false;
						}
						else {
							place++;
						}
					}
					else{//��ȥ
						myHandler.sendEmptyMessage(5);//����Handler��Ϣ
						try{
							Thread.sleep(200);//˯��300����
						}
						catch(Exception e){//�����쳣 
							e.printStackTrace();//��ӡ�쳣
						}
						if(place >= titleStr.length()){
							place = 1;
							control = true;
						}
						else {
							place++;//��place��һ
						}
					}
				}
			}
		}.start();
		
		myTabhost=this.getTabHost();//��TabActivity�����ȡ����Tab��TabHost
		LayoutInflater.from(this).inflate(R.layout.index, myTabhost.getTabContentView(), true);
		
		myTabhost.addTab(//���һ��ѡ��
				myTabhost.newTabSpec("�ղ�")	//����һ��TabSpec
					.setIndicator("�ղ�", getResources().getDrawable(R.drawable.png1))
					.setContent(R.id.favourite)//���ô�ѡ��Ĳ����ļ�
			);
	    myTabhost.addTab(//���һ��ѡ��
				myTabhost.newTabSpec("����")	//����һ��TabSpec
					.setIndicator("����", getResources().getDrawable(R.drawable.png2))
					.setContent(R.id.search)//���ô�ѡ��Ĳ����ļ�
			);
	    myTabhost.addTab(//���һ��ѡ��
				myTabhost.newTabSpec("�Ƽ�")	//����һ��TabSpec
					.setIndicator("�Ƽ�", getResources().getDrawable(R.drawable.png3))
					.setContent(R.id.recommend)//���ô�ѡ��Ĳ����ļ�
			); 
		myTabhost.addTab(//���һ��ѡ�� 
				myTabhost.newTabSpec("�ϴ�")	//����һ��TabSpec
					.setIndicator("�ϴ�", getResources().getDrawable(R.drawable.png4))
					.setContent(R.id.update)//���ô�ѡ��Ĳ����ļ�
			);
		
		myTabhost.setOnTabChangedListener(new OnTabChangeListener(){//ΪTab��Ӽ���
			@Override
			public void onTabChanged(String tabId) {//�¼�������
				if(tabId.equals("�ղ�")){//�ղؽ���
					System.out.println("favourite");
			        //��ʼ��ListView
			        lv=(ListView)findViewById(R.id.searchlistView01);
			        lv.setOnItemClickListener(MainActivity.this);
					try {
						clientNetThread.dout.writeUTF("<#FAVOURITE#>"+uid);//�������������Ϣ
					} catch (IOException e) {//�����쳣
						e.printStackTrace();//��ӡ�쳣
					}
				}
				else if(tabId.equals("����")){//��������
					oilSort = (Spinner)findViewById(R.id.mySort);//�õ���ʳ����ؼ�
					new Thread(){
						public void run(){
							try {
								clientNetThread.dout.writeUTF("<#OILSORT#>1");//�������������Ϣ
							} catch (IOException e) {//�����쳣
								e.printStackTrace();//��ӡ�쳣
							}							
						}
					}.start();
					searchButton = (ImageButton)findViewById(R.id.searchButton);//�õ�������ť������
					searchEditText = (EditText)findViewById(R.id.infoValues);//�õ������ؼ����ı�������
					searchButton.setOnClickListener(MainActivity.this);//Ϊ��ť��Ӽ���
					infoPrice1 = (EditText)findViewById(R.id.infoPrice1);
					infoPrice2 = (EditText)findViewById(R.id.infoPrice2);
				}
				else if(tabId.equals("�Ƽ�")){//�Ƽ�����
					lv=(ListView)findViewById(R.id.searchlistView03);
					lv.setOnItemClickListener(MainActivity.this);
					new Thread(){
						public void run(){
							try {
								clientNetThread.dout.writeUTF("<#RECOMMEND#>"+uid);//�������������Ϣ
							} catch (IOException e) {//�����쳣
								e.printStackTrace();//��ӡ�쳣
							}							
						}
					}.start();
				}
				else if(tabId.equals("�ϴ�")){//�ϴ�����
					updateButton = (ImageButton)findViewById(R.id.updateButton);//�õ���ť������
					updateButton.setOnClickListener(MainActivity.this);//��Ӱ�ť����

					jyzName = (EditText)findViewById(R.id.jyzName);//�õ�����վ���ƿؼ�
					oilSort2 = (Spinner)findViewById(R.id.mySort2);//�õ�������ؼ�
					oilPrice = (EditText)findViewById(R.id.oilPrice);//�õ��۸�ؼ�
					oilLocation = (EditText)findViewById(R.id.oilLocation);//�õ����ȿؼ�
					oilCompName = (EditText)findViewById(R.id.oilCompName);
					oilDis = (EditText)findViewById(R.id.oilDis);
					
					oilLocation.setOnClickListener(MainActivity.this);
					//msImage = (ImageView)findViewById(R.id.msImage);//�õ�ͼƬ
					//msImage.setClickable(true);//��������Ե��
					//msImage.setOnClickListener(MainActivity.this);//��Ӽ���
					new Thread(){
						public void run(){
							try {
								clientNetThread.dout.writeUTF("<#OILSORT#>2");//�������������Ϣ
							} catch (IOException e) {//�����쳣
								e.printStackTrace();//��ӡ�쳣
							}							
						}
					}.start();
				}
			}
		});
		myTabhost.setCurrentTab(1);//���õ�ǰ��ʾ��Tab
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		String[] oilStrs = (String[]) oilInfos.get(arg2);
		int action = myTabhost.getCurrentTab();//��ǰ���ĸ�Tab
		Intent intent = new Intent();//����Intent
		intent.setClass(MainActivity.this,  InfoActivity.class);
		Bundle bundle = new Bundle();//��������Bundle
		bundle.putInt("action", action);
		bundle.putInt("uid", uid);
		bundle.putStringArray("oilInfo", oilStrs);
		intent.putExtras(bundle);//�������
		startActivity(intent);//����Activity
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view == searchButton){//������ť������ʱ
			String infoValues = searchEditText.getText().toString();
			if(infoValues.trim().equals("")){//�����ؼ���Ϊ��ʱ
				Toast.makeText(this, "�����������ؼ���", Toast.LENGTH_SHORT).show();
				return;
			}
			int searchSort;
			//String searchSort = "a";
			int temp = oilSort.getSelectedItemPosition();
			if(temp == 0){
				searchSort = -1;
			}
			else {
				searchSort = Integer.parseInt(((String[])oilSorts.get(temp-1))[0]);
			}
			
			String startPrice = infoPrice1.getText().toString().trim();
			String endPrice = infoPrice2.getText().toString().trim();
		
			if(!startPrice.equals("") && !endPrice.equals("")){
				if(Integer.parseInt(startPrice) > Integer.parseInt(endPrice)){
					Toast.makeText(this, "��������ȷ�ļ۸�����", Toast.LENGTH_SHORT).show();
					return;
				}				
			}
			
			Intent intent = new Intent();
			Bundle data = new Bundle();
			data.putString("infoValues", infoValues);
			data.putInt("searchSort", searchSort);
			data.putString("startPrice", startPrice);
			data.putString("endPrice", endPrice);
			data.putInt("uid", uid);
			Log.d("infovalue", infoValues);
			Log.d("startPrice", startPrice);
			Log.d("endPrice",endPrice);
			
			intent.putExtras(data);
			intent.setClass(MainActivity.this, SearchActivity.class);
			startActivity(intent);
		}
		else if(view == oilLocation){//���»�ȡ��γ�ȵ��ı���
			myDialog = ProgressDialog.show(this, "����", "���ڼ���...",true);
			new Thread(){
				public void run(){
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, MyMapActivity.class);
					startActivityForResult(intent, 1);		

					myDialog.dismiss();
				}
			}.start();			
		}
		else if(view == updateButton){//�����ϴ���ť
			String info_title = jyzName.getText().toString();
			String info_loc = oilLocation.getText().toString();
			String info_price = oilPrice.getText().toString();
			String info_comp = oilCompName.getText().toString();
			String info_disc = oilDis.getText().toString();
			
			
			if(info_title.trim().equals("")){//����Ϊ��ʱ
				Toast.makeText(MainActivity.this, "�������������", Toast.LENGTH_LONG).show();
				return;					
			}
			else if(info_price.trim().equals("")){//�۸�Ϊ��ʱ
				Toast.makeText(MainActivity.this, "���������ͼ۸�", Toast.LENGTH_LONG).show();
				return;	
			}	
			else if(info_comp.trim().equals("")){//����λ��Ϊ��ʱ
				Toast.makeText(MainActivity.this, "���������վ������˾", Toast.LENGTH_LONG).show();
				return;			
			}
			else if(info_loc.trim().equals("")){//����Ϊ��ʱ
				Toast.makeText(MainActivity.this, "��ͨ��λ�ð�ť��õ������ڵ�λ��", Toast.LENGTH_LONG).show();
				return;	
			}
			/*new Thread(){
				public void run(){
					Looper.prepare();//һ��Ҫ��*/
					String[] tempStr = (String[])oilSorts.get(oilSort2.getSelectedItemPosition());
					try {
						myDialog = ProgressDialog.show(MainActivity.this, "����", "���ڼ���...",true);
				
						clientNetThread.dout.writeUTF("<#INSERTOILINFO#>"+
								jyzName.getText().toString()+"|"+oilDis.getText().toString()+"|"
								+lon+"|"+lat+"|"+uid+"|"+tempStr[0]+"|"+
								oilPrice.getText().toString()+"|"+oilCompName.getText().toString());

//						int size = image.length;//ͼƬ����ĳ���
//						clientNetThread.dout.writeInt(size);//���������������ĳ���
//						clientNetThread.dout.write(image);//�����������ͼƬ�ֽ�����
//						clientNetThread.dout.flush();//��ջ�����,��֤֮ǰ�����ݷ��ͳ�ȥ*/

						myHandler.sendEmptyMessage(6);
					}
					catch (IOException e) {//�����쳣
						e.printStackTrace();//��ӡ�쳣��Ϣ
					}
					finally{
						if(myDialog != null){//��myDialog��Ϊ��ʱ
							myDialog.dismiss();
						}
					}
				//}
			//}.start();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data){
	    if(requestCode == 1){//��ȡ��γ�ȵĽ��
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();//�õ�����
				lat = bundle.getDouble("lat");//�õ���γ��
				lon = bundle.getDouble("lon");
				oilLocation.setText("����Ϊ��"+lon+"\nγ��Ϊ��"+lat);//���þ�γ�ȵ��ı�����
			}		
		}
	}
	
	protected void onDesotry(){
	    super.onDestroy();
	    try{
	    	clientNetThread.dout.writeUTF("<#ClientDown#>");//֪ͨ�������ͻ�������
			clientNetThread.flag = false;//���紦��
	    }catch (IOException e){
	    	e.printStackTrace();
	    }
	    
	}
	
	public class MyBaseAdapter extends BaseAdapter{
		ArrayList oilInfos = null;
        public MyBaseAdapter(ArrayList oilInfos) {//������
            super();   
            this.oilInfos = oilInfos;      
        } 
        
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return oilInfos.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			LinearLayout linearLayout1 = new LinearLayout(MainActivity.this);
			linearLayout1.setOrientation(LinearLayout.HORIZONTAL);//���ó���	
			linearLayout1.setPadding(5,5,5,5);//������������


			LinearLayout linearLayout2 = new LinearLayout(MainActivity.this);
			linearLayout2.setOrientation(LinearLayout.VERTICAL);//���ó���	
			linearLayout2.setPadding(5,1,5,1);//������������
			linearLayout2.setGravity(Gravity.TOP);//���ö��䷽ʽ
			//��ʼ��TextView,��ʳ����
			TextView title=new TextView(MainActivity.this);
			String[] oilStrs = (String[]) oilInfos.get(arg0);
			title.setText(oilStrs[0]);//��������
			title.setTextSize(18);//���������С
			title.setTextColor(Color.RED);
			title.setPadding(5,1,5,1);//������������
			title.setMaxLines(1);//��������
			title.setGravity(Gravity.LEFT);//���뷽ʽ
		    linearLayout2.addView(title);//��ӵ�LinearLayout��
		    
			//��ʼ��TextView
			TextView date = new TextView(MainActivity.this);
			date.setText(oilStrs[5]);
			date.setTextSize(10);//���������С
			date.setTextColor(Color.GRAY);//���ֵ���ɫ
			date.setPadding(5,1,5,1);//������������
			title.setMaxLines(1);//��������
			date.setGravity(Gravity.LEFT);//���뷽ʽ
		    linearLayout2.addView(date);//��ӵ�LinearLayout��
		    
			//��ʼ��TextView
			TextView info_dis = new TextView(MainActivity.this);
			info_dis.setText(oilStrs[1]);
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
}
