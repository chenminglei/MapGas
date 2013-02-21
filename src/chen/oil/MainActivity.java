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
	private TabHost myTabhost;//声明tabhost的引用
	
	ImageButton searchButton = null;//搜索按钮
	ImageButton updateButton = null;//上传按钮
	
	EditText searchEditText;//搜索关键字
	ClientNetThread clientNetThread = null;
	ListView lv;//信息列表
	ArrayList oilInfos = null; //加油站信息
	List oilSorts = null;
	int uid;
	
	EditText infoPrice1 = null;
	EditText infoPrice2 = null;
	Spinner oilSort = null;   //加油搜索种类
	
	//上传界面各个控件
	EditText jyzName = null;
	Spinner oilSort2 = null;   //加油种类
	EditText oilPrice = null;    //油价
	EditText oilLocation = null;  //加油站位置
	EditText oilCompName = null;      //加油站公司
    EditText oilDis = null;     //加油评价
	
	ProgressDialog myDialog = null;
	
	double lat;  //加油站经纬度
	double lon;
	
	String titleStr = "";    //跑马灯字符串？
	int place = 1;
	//走马灯handler
	public Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {//重写的方法用于接收Handler消息
			super.handleMessage(msg);
			if(msg.what == 1 || msg.what == 2 ||msg.what == 3){//得到美食搜索结果
				Bundle data = msg.getData();
				oilInfos = (ArrayList)data.get("oilInfo");
				//mstxImages = (ArrayList)data.get("mstxImages");
		        //为ListView准备内容适配器
				MyBaseAdapter mba = new MyBaseAdapter(oilInfos);
			    lv.setAdapter(mba);//为ListView设置内容适配器	
			    mba.notifyDataSetChanged();
			}
			else if(msg.what == 4){//跑马灯向外出来时
				MainActivity.this.setTitle(titleStr.substring(0, place));
			}
			else if(msg.what == 5){//跑马灯向里进去时
				MainActivity.this.setTitle(titleStr.substring(place, titleStr.length()));
			}
			else if(msg.what == 6){//清空美食上传界面
				jyzName.setText("");
				oilDis.setText("");
				oilCompName.setText("");
				oilLocation.setText("点击我获取经纬度");
				oilPrice.setText("");			
			}
			else if(msg.what == 7){//美食种类
				Bundle data = msg.getData();
				oilSorts = (ArrayList)data.get("oilSorts");
				BaseAdapter ba = new BaseAdapter(){						//为Spinner准备内容适配器
					@Override
					public int getCount() {
						return oilSorts.size();
					}
					@Override
					public Object getItem(int arg0) {//重写的getItem方法				
						return null; 
					}
					@Override
					public long getItemId(int arg0) {//重写的getItemId方法
						return 0; 
					}
					@Override
					public View getView(int arg0, View arg1, ViewGroup arg2) {
						//初始化LinearLayout
						LinearLayout ll=new LinearLayout(MainActivity.this);
						ll.setOrientation(LinearLayout.HORIZONTAL);	//设置朝向	
						String[] str = (String[])oilSorts.get(arg0);	
						//初始化TextView
						TextView tv=new TextView(MainActivity.this);						
						tv.setText(str[1]);//设置内容
						tv.setTextColor(R.color.textword2);
						tv.setTextSize(20);
						ll.addView(tv);	//添加到LinearLayout中						
						return ll;//将LinearLayout返回
					}
				};
				oilSort2.setAdapter(ba);
		        ba.notifyDataSetChanged();
			}
			else if(msg.what == 8){//搜索界面的美食种类
				Bundle data = msg.getData();
				oilSorts = (ArrayList)data.get("oilSorts");
				BaseAdapter ba = new BaseAdapter(){						//为Spinner准备内容适配器
					@Override
					public int getCount() {
						return oilSorts.size()+1;
					}
					@Override
					public Object getItem(int arg0) {//重写的getItem方法				
						return null; 
					}
					@Override
					public long getItemId(int arg0) {//重写的getItemId方法
						return arg0; 
					}

                    @Override
					public View getView(int arg0, View arg1, ViewGroup arg2) {
						String[] str;
						if(arg0 == 0){
							str = new String[]{"-1","所有种类"};
						}
						else {
							str = (String[])oilSorts.get(arg0-1);
						}
						//初始化LinearLayout
						LinearLayout ll=new LinearLayout(MainActivity.this);
						ll.setOrientation(LinearLayout.HORIZONTAL);	//设置朝向	
							
						//初始化TextView
						TextView tv=new TextView(MainActivity.this);						
						tv.setText(str[1]);//设置内容
						tv.setTextColor(R.color.textword2);
						tv.setTextSize(20);
						ll.addView(tv);	//添加到LinearLayout中						
						return ll;//将LinearLayout返回
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
		clientNetThread = new ClientNetThread(this);//初始化网络处理线程
		clientNetThread.start();//启动线程
		
		Intent intent = this.getIntent();//创建一个Intent
		Bundle bundle = intent.getExtras();//得到存放数据的Bundle
		String u_name = bundle.getString("u_name");//得到Bundle中的u_name数据
		uid = Integer.parseInt(bundle.getString("uid"));//得到用户编号
		titleStr = u_name+", 加油小助手欢迎您！您的ID号为"+ uid;
		
		new Thread(){ //跑马灯线程
			public void run(){
				boolean control = true;
				while(true){
					if(control){//出来时
						myHandler.sendEmptyMessage(4);
						try{
							Thread.sleep(300);//睡觉300毫秒
						}
						catch(Exception e){//捕获异常 
							e.printStackTrace();//打印异常
						}
						if(place >= titleStr.length()){
							place = 1;
							control = false;
						}
						else {
							place++;
						}
					}
					else{//进去
						myHandler.sendEmptyMessage(5);//发送Handler消息
						try{
							Thread.sleep(200);//睡觉300毫秒
						}
						catch(Exception e){//捕获异常 
							e.printStackTrace();//打印异常
						}
						if(place >= titleStr.length()){
							place = 1;
							control = true;
						}
						else {
							place++;//将place加一
						}
					}
				}
			}
		}.start();
		
		myTabhost=this.getTabHost();//从TabActivity上面获取放置Tab的TabHost
		LayoutInflater.from(this).inflate(R.layout.index, myTabhost.getTabContentView(), true);
		
		myTabhost.addTab(//添加一个选项
				myTabhost.newTabSpec("收藏")	//创建一个TabSpec
					.setIndicator("收藏", getResources().getDrawable(R.drawable.png1))
					.setContent(R.id.favourite)//设置此选项的布局文件
			);
	    myTabhost.addTab(//添加一个选项
				myTabhost.newTabSpec("搜索")	//创建一个TabSpec
					.setIndicator("搜索", getResources().getDrawable(R.drawable.png2))
					.setContent(R.id.search)//设置此选项的布局文件
			);
	    myTabhost.addTab(//添加一个选项
				myTabhost.newTabSpec("推荐")	//创建一个TabSpec
					.setIndicator("推荐", getResources().getDrawable(R.drawable.png3))
					.setContent(R.id.recommend)//设置此选项的布局文件
			); 
		myTabhost.addTab(//添加一个选项 
				myTabhost.newTabSpec("上传")	//创建一个TabSpec
					.setIndicator("上传", getResources().getDrawable(R.drawable.png4))
					.setContent(R.id.update)//设置此选项的布局文件
			);
		
		myTabhost.setOnTabChangedListener(new OnTabChangeListener(){//为Tab添加监听
			@Override
			public void onTabChanged(String tabId) {//事件处理方法
				if(tabId.equals("收藏")){//收藏界面
					System.out.println("favourite");
			        //初始化ListView
			        lv=(ListView)findViewById(R.id.searchlistView01);
			        lv.setOnItemClickListener(MainActivity.this);
					try {
						clientNetThread.dout.writeUTF("<#FAVOURITE#>"+uid);//向服务器发送消息
					} catch (IOException e) {//捕获异常
						e.printStackTrace();//打印异常
					}
				}
				else if(tabId.equals("搜索")){//搜索界面
					oilSort = (Spinner)findViewById(R.id.mySort);//得到美食种类控件
					new Thread(){
						public void run(){
							try {
								clientNetThread.dout.writeUTF("<#OILSORT#>1");//向服务器发送消息
							} catch (IOException e) {//捕获异常
								e.printStackTrace();//打印异常
							}							
						}
					}.start();
					searchButton = (ImageButton)findViewById(R.id.searchButton);//得到搜索按钮的引用
					searchEditText = (EditText)findViewById(R.id.infoValues);//得到搜索关键字文本框引用
					searchButton.setOnClickListener(MainActivity.this);//为按钮添加监听
					infoPrice1 = (EditText)findViewById(R.id.infoPrice1);
					infoPrice2 = (EditText)findViewById(R.id.infoPrice2);
				}
				else if(tabId.equals("推荐")){//推荐界面
					lv=(ListView)findViewById(R.id.searchlistView03);
					lv.setOnItemClickListener(MainActivity.this);
					new Thread(){
						public void run(){
							try {
								clientNetThread.dout.writeUTF("<#RECOMMEND#>"+uid);//向服务器发送消息
							} catch (IOException e) {//捕获异常
								e.printStackTrace();//打印异常
							}							
						}
					}.start();
				}
				else if(tabId.equals("上传")){//上传界面
					updateButton = (ImageButton)findViewById(R.id.updateButton);//得到按钮的引用
					updateButton.setOnClickListener(MainActivity.this);//添加按钮监听

					jyzName = (EditText)findViewById(R.id.jyzName);//得到加油站名称控件
					oilSort2 = (Spinner)findViewById(R.id.mySort2);//得到油种类控件
					oilPrice = (EditText)findViewById(R.id.oilPrice);//得到价格控件
					oilLocation = (EditText)findViewById(R.id.oilLocation);//得到经度控件
					oilCompName = (EditText)findViewById(R.id.oilCompName);
					oilDis = (EditText)findViewById(R.id.oilDis);
					
					oilLocation.setOnClickListener(MainActivity.this);
					//msImage = (ImageView)findViewById(R.id.msImage);//得到图片
					//msImage.setClickable(true);//设置其可以点击
					//msImage.setOnClickListener(MainActivity.this);//添加监听
					new Thread(){
						public void run(){
							try {
								clientNetThread.dout.writeUTF("<#OILSORT#>2");//向服务器发送消息
							} catch (IOException e) {//捕获异常
								e.printStackTrace();//打印异常
							}							
						}
					}.start();
				}
			}
		});
		myTabhost.setCurrentTab(1);//设置当前显示的Tab
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		String[] oilStrs = (String[]) oilInfos.get(arg2);
		int action = myTabhost.getCurrentTab();//当前是哪个Tab
		Intent intent = new Intent();//创建Intent
		intent.setClass(MainActivity.this,  InfoActivity.class);
		Bundle bundle = new Bundle();//创建数据Bundle
		bundle.putInt("action", action);
		bundle.putInt("uid", uid);
		bundle.putStringArray("oilInfo", oilStrs);
		intent.putExtras(bundle);//存放数据
		startActivity(intent);//启动Activity
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view == searchButton){//搜索按钮被按下时
			String infoValues = searchEditText.getText().toString();
			if(infoValues.trim().equals("")){//搜索关键字为空时
				Toast.makeText(this, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(this, "请输入正确的价格区间", Toast.LENGTH_SHORT).show();
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
		else if(view == oilLocation){//按下获取经纬度的文本框
			myDialog = ProgressDialog.show(this, "进度", "正在加载...",true);
			new Thread(){
				public void run(){
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, MyMapActivity.class);
					startActivityForResult(intent, 1);		

					myDialog.dismiss();
				}
			}.start();			
		}
		else if(view == updateButton){//按下上传按钮
			String info_title = jyzName.getText().toString();
			String info_loc = oilLocation.getText().toString();
			String info_price = oilPrice.getText().toString();
			String info_comp = oilCompName.getText().toString();
			String info_disc = oilDis.getText().toString();
			
			
			if(info_title.trim().equals("")){//名称为空时
				Toast.makeText(MainActivity.this, "请输入加油名称", Toast.LENGTH_LONG).show();
				return;					
			}
			else if(info_price.trim().equals("")){//价格为空时
				Toast.makeText(MainActivity.this, "请输入汽油价格", Toast.LENGTH_LONG).show();
				return;	
			}	
			else if(info_comp.trim().equals("")){//店铺位置为空时
				Toast.makeText(MainActivity.this, "请输入加油站所属公司", Toast.LENGTH_LONG).show();
				return;			
			}
			else if(info_loc.trim().equals("")){//经度为空时
				Toast.makeText(MainActivity.this, "请通过位置按钮获得店铺所在的位置", Toast.LENGTH_LONG).show();
				return;	
			}
			/*new Thread(){
				public void run(){
					Looper.prepare();//一定要加*/
					String[] tempStr = (String[])oilSorts.get(oilSort2.getSelectedItemPosition());
					try {
						myDialog = ProgressDialog.show(MainActivity.this, "进度", "正在加载...",true);
				
						clientNetThread.dout.writeUTF("<#INSERTOILINFO#>"+
								jyzName.getText().toString()+"|"+oilDis.getText().toString()+"|"
								+lon+"|"+lat+"|"+uid+"|"+tempStr[0]+"|"+
								oilPrice.getText().toString()+"|"+oilCompName.getText().toString());

//						int size = image.length;//图片数组的长度
//						clientNetThread.dout.writeInt(size);//向服务器发送数组的长度
//						clientNetThread.dout.write(image);//向服务器发送图片字节数组
//						clientNetThread.dout.flush();//清空缓冲区,保证之前的数据发送出去*/

						myHandler.sendEmptyMessage(6);
					}
					catch (IOException e) {//捕获异常
						e.printStackTrace();//打印异常信息
					}
					finally{
						if(myDialog != null){//当myDialog不为空时
							myDialog.dismiss();
						}
					}
				//}
			//}.start();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data){
	    if(requestCode == 1){//获取经纬度的结果
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();//得到数据
				lat = bundle.getDouble("lat");//得到经纬度
				lon = bundle.getDouble("lon");
				oilLocation.setText("经度为："+lon+"\n纬度为："+lat);//设置经纬度到文本框中
			}		
		}
	}
	
	protected void onDesotry(){
	    super.onDestroy();
	    try{
	    	clientNetThread.dout.writeUTF("<#ClientDown#>");//通知服务器客户端下线
			clientNetThread.flag = false;//网络处理
	    }catch (IOException e){
	    	e.printStackTrace();
	    }
	    
	}
	
	public class MyBaseAdapter extends BaseAdapter{
		ArrayList oilInfos = null;
        public MyBaseAdapter(ArrayList oilInfos) {//构造器
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
			linearLayout1.setOrientation(LinearLayout.HORIZONTAL);//设置朝向	
			linearLayout1.setPadding(5,5,5,5);//设置四周留白


			LinearLayout linearLayout2 = new LinearLayout(MainActivity.this);
			linearLayout2.setOrientation(LinearLayout.VERTICAL);//设置朝向	
			linearLayout2.setPadding(5,1,5,1);//设置四周留白
			linearLayout2.setGravity(Gravity.TOP);//设置对其方式
			//初始化TextView,美食名称
			TextView title=new TextView(MainActivity.this);
			String[] oilStrs = (String[]) oilInfos.get(arg0);
			title.setText(oilStrs[0]);//设置文字
			title.setTextSize(18);//设置字体大小
			title.setTextColor(Color.RED);
			title.setPadding(5,1,5,1);//设置四周留白
			title.setMaxLines(1);//设置行数
			title.setGravity(Gravity.LEFT);//对齐方式
		    linearLayout2.addView(title);//添加到LinearLayout中
		    
			//初始化TextView
			TextView date = new TextView(MainActivity.this);
			date.setText(oilStrs[5]);
			date.setTextSize(10);//设置字体大小
			date.setTextColor(Color.GRAY);//文字的颜色
			date.setPadding(5,1,5,1);//设置四周留白
			title.setMaxLines(1);//设置行数
			date.setGravity(Gravity.LEFT);//对齐方式
		    linearLayout2.addView(date);//添加到LinearLayout中
		    
			//初始化TextView
			TextView info_dis = new TextView(MainActivity.this);
			info_dis.setText(oilStrs[1]);
			info_dis.setTextSize(13);//设置字体大小
			info_dis.setTextColor(Color.BLACK);//文字的颜色
			info_dis.setPadding(5,1,5,1);//设置四周留白
			info_dis.setGravity(Gravity.LEFT);//对齐方式
			info_dis.setMaxLines(3);//最大行数
		    linearLayout2.addView(info_dis);//添加到LinearLayout中
		    linearLayout1.addView(linearLayout2);
			return linearLayout1;
		}
		
	}
}
