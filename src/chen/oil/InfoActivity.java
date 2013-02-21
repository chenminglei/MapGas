package chen.oil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class InfoActivity extends MapActivity implements OnClickListener{
    MapController myMapController = null;
    MapView myMapView = null;

    double info_lon;
    double info_lat;
    String[] oilInfo;
    ImageButton deleteButton =  null;
    ImageButton favouriteButton = null;
    ImageButton backButton = null;
    int uid;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    			WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
    			WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	Intent intent = this.getIntent();
    	Bundle bundle = intent.getExtras();
    	int action = bundle.getInt("action");
    	uid = bundle.getInt("uid");
    	oilInfo = bundle.getStringArray("oilInfo");
    	System.out.println("infoActivity-uid"+uid);
    	
    	if(action == 0){//我的收藏中某个美食的详细信息
    		this.setContentView(R.layout.favourite_info);
			TextView tv = (TextView)findViewById(R.id.favouriteTitle);
			TextView oilDis = (TextView)findViewById(R.id.oilDis);
			TextView jyzName = (TextView)findViewById(R.id.jyzName);
			TextView oilTime = (TextView)findViewById(R.id.oilTime);
			deleteButton = (ImageButton)findViewById(R.id.deleteButton);//得到删除按钮的引用
			deleteButton.setOnClickListener(this);//添加监听
			backButton = (ImageButton)findViewById(R.id.backButton);//返回按钮
			backButton.setOnClickListener(this);//添加监听
			
			tv.setText(oilInfo[0]);//设置标题
			jyzName.setText(oilInfo[0]);//加油名称
			oilTime.setText(oilInfo[4]);//上传时间
			oilDis.setText(oilInfo[1]);//加油描述
			
			info_lon = Double.parseDouble(oilInfo[2]);//得到经度
			info_lat = Double.parseDouble(oilInfo[3]);//得到纬度
			myMapView = (MapView) this.findViewById(R.id.myMapView);//得到myMapView的引用
			myMapController = myMapView.getController();//获得MapController
			GeoPoint gp = new GeoPoint((int)(info_lat*1E6), (int)(info_lon*1E6));
			myMapController.animateTo(gp);//设置经纬度
			myMapController.setZoom(15);//设置缩放比例
			myMapView.setBuiltInZoomControls(true);
			
			
			MyBallonOverlay myOverlay = new MyBallonOverlay(gp,"加油名称："+oilInfo[0]);
			List<Overlay> overlays = myMapView.getOverlays();
			overlays.clear();
			overlays.add(myOverlay);

			//设置显示模式
//			myMapView.setSatellite(false);//卫星模式
//			myMapView.setTraffic(false);//路况模式
//			myMapView.setStreetView(false);//街景模式
    	}
    	else if(action == 2){//推荐中某个加油的详细信息 
    		this.setContentView(R.layout.search_info);
    		TextView tv = (TextView)findViewById(R.id.favouriteTitle);
    		TextView oilDis = (TextView)findViewById(R.id.oilDis);
    		TextView jyzName = (TextView)findViewById(R.id.jyzName);
    		TextView oilTime = (TextView)findViewById(R.id.oilTime);
    		favouriteButton = (ImageButton)findViewById(R.id.favouriteButton);//得到删除按钮的引用
    		favouriteButton.setOnClickListener(this);//添加监听
    		tv.setText(oilInfo[0]);//设置标题
    		jyzName.setText(oilInfo[0]);//加油名称
    		oilTime.setText(oilInfo[4]);//上传时间
    		oilDis.setText(oilInfo[1]);//加油描述
    		
    		info_lon = Double.parseDouble(oilInfo[2]);//得到经度
    		info_lat = Double.parseDouble(oilInfo[3]);//得到纬度
    		myMapView = (MapView) this.findViewById(R.id.myMapView);//得到myMapView的引用
    		myMapController = myMapView.getController();//获得MapController
    		GeoPoint gp = new GeoPoint((int)(info_lat*1E6), (int)(info_lon*1E6));
    		myMapController.animateTo(gp);//设置经纬度
    		myMapController.setZoom(15);//设置放大等级
    		myMapView.setBuiltInZoomControls(true);
    		backButton = (ImageButton)findViewById(R.id.backButton);//返回按钮
    		backButton.setOnClickListener(this);//添加监听
    		
    		MyBallonOverlay myOverlay = new MyBallonOverlay(gp,"加油名称："+oilInfo[0]);
    		List<Overlay> overlays = myMapView.getOverlays();
    		overlays.clear();
    		overlays.add(myOverlay);		
    	}
    	else if(action == 3){//搜索中某个美食的信息
    		this.setContentView(R.layout.search_info);	
    		TextView tv = (TextView)findViewById(R.id.favouriteTitle);
    		TextView oilDis = (TextView)findViewById(R.id.oilDis);
    		TextView jyzName = (TextView)findViewById(R.id.jyzName);
    		TextView oilTime = (TextView)findViewById(R.id.oilTime);
    		favouriteButton = (ImageButton)findViewById(R.id.favouriteButton);//得到删除按钮的引用
    		favouriteButton.setOnClickListener(this);//添加监听
    		tv.setText(oilInfo[1]);//设置标题
    		jyzName.setText(oilInfo[1]);//美食名称
    		oilTime.setText(oilInfo[5]);//上传时间
    		oilDis.setText(oilInfo[2]);//加油描述
    		
    		info_lon = Double.parseDouble(oilInfo[3]);//得到经度
    		info_lat = Double.parseDouble(oilInfo[4]);//得到纬度
    		myMapView = (MapView) this.findViewById(R.id.myMapView);//得到myMapView的引用
    		myMapController = myMapView.getController();//获得MapController
    		GeoPoint gp = new GeoPoint((int)(info_lat*1E6), (int)(info_lon*1E6));
    		myMapController.animateTo(gp);//设置经纬度
    		myMapController.setZoom(15);//设置放大等级
    		myMapView.setBuiltInZoomControls(true);
    		backButton = (ImageButton)findViewById(R.id.backButton);//返回按钮
    		backButton.setOnClickListener(this);//添加监听
    		
    		MyBallonOverlay myOverlay = new MyBallonOverlay(gp,"店铺名称："+oilInfo[0]);
    		List<Overlay> overlays = myMapView.getOverlays();
    		overlays.clear();
    		overlays.add(myOverlay);			
    	}
    }
   
    protected Dialog onCreateDialog(int id) {//重写onCreateDialog方法
		Dialog dialog = null;//声明一个Dialog对象用于返回
		switch(id){//对id进行判断
		case 1:
			Builder b = new AlertDialog.Builder(this);
			b.setIcon(R.drawable.logo);//设置对话框的图标
			b.setTitle("提示");//设置对话框的标题
			b.setMessage("您确定要删除该加油站？");	//设置对话框的显示内容
			b.setPositiveButton(//添加按钮
				"确定", 
				new DialogInterface.OnClickListener() {//为按钮添加监听器
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new Thread(){//创建线程,将耗时的工作放到单独的线程中
							public void run(){//重写run方法
								Socket s = null;
								DataOutputStream dout = null;
								try {
									s = new Socket("10.201.7.146", 9999);//连接服务器
									dout = new DataOutputStream(s.getOutputStream());
									dout.writeUTF("<#DELETEOILCOL#>"+oilInfo[5]+"|"+oilInfo[4]);
									dout.writeUTF("<#ClientDown#>");
								} catch (Exception e) {//捕获异常
									e.printStackTrace();//打印异常
								} finally{//使用finally保证之后的语句一定被执行
									try{
										if(dout != null){
											dout.close();//关闭输出流
											dout = null;
										}
									}
									catch(Exception e){//捕获异常
										e.printStackTrace();//打印异常
									}
									try{
										if(s != null){
											s.close();//关闭Socket
											s = null;
										}							
									}
									catch(IOException e){//捕获异常
										e.printStackTrace();//打印异常
									}						
								}
							}
						}.start();	
						InfoActivity.this.finish();//释放当前的Activity
					}
			});
			b.setNeutralButton("取消", 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which){}
				}
			);
			dialog = b.create();								//生成Dialog对象
			break;
		case 2:
			Builder b2 = new AlertDialog.Builder(this);
			b2.setIcon(R.drawable.logo);//设置对话框的图标
			b2.setTitle("提示");//设置对话框的标题
			b2.setMessage("您确定要收藏该加油站？");//设置对话框的显示内容
			b2.setPositiveButton(//添加按钮
				"确定", 
				new DialogInterface.OnClickListener() {//为按钮添加监听器
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new Thread(){//将耗时的网络动作放到线程中
							public void run(){//重写的run方法
								Socket s = null;//声明Socket的引用
								DataOutputStream dout = null;//声明输出流的引用
								try {
									s = new Socket("10.201.7.146", 9999);//连接服务器
									dout = new DataOutputStream(s.getOutputStream());//得到输出流
									dout.writeUTF("<#INSERTOILCOL#>"+oilInfo[5]+"|"+uid);
									dout.writeUTF("<#ClientDown#>");
								} catch (Exception e) {//捕获异常
									e.printStackTrace();//打印异常
								} finally{//使用finally保证之后的语句一定被执行
									try{
										if(dout != null){
											dout.close();//关闭输出流
											dout = null;
										}							
									}
									catch(Exception e){//捕获异常
										e.printStackTrace();//打印异常 
									}
									try{
										if(s != null){
											s.close();//关闭Socket连接
											s = null;
										}							
									}
									catch(Exception e){//捕获异常
										e.printStackTrace();//打印异常
									}
								}  
							}
						}.start();
						InfoActivity.this.finish();//释放当前的Activity	
					}
			});
			b2.setNeutralButton("取消", 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which){}
				}
			);
			dialog = b2.create();//生成Dialog对象			
			break;
		default:
			break;
		}
		return dialog;//返回生成Dialog的对象
	}
	//@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == deleteButton){//点击的是删除按钮
			showDialog(1);			//显示普通对话框
		}
		else if(v == favouriteButton){//点击的是收藏按钮
			showDialog(2);			//显示普通对话框
			
		}
		else if(v == backButton){//收藏界面中的返回按钮
			this.finish();//释放当前的Activity	
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
