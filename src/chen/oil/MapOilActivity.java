package chen.oil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MapOilActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */	
    Button loginButton = null;
    Button registerButton = null;
    EditText uid = null;
    EditText pwd = null;
    CheckBox cb = null;
    ProgressDialog myDialog = null;
    Handler myHandler = new Handler(){
    	public void handleMessage(Message msg){
    	    super.handleMessage(msg);
    	    if(msg.what == 1){
    	    	Toast.makeText(MapOilActivity.this, "用户名或密码错误！",
    	        Toast.LENGTH_LONG).show();
    	    }
    	}
    };
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login);
        GradientDrawable grad = new GradientDrawable(
        		Orientation.TOP_BOTTOM, new int[]{R.color.red, R.color.yellow}
        );
        getWindow().setBackgroundDrawable(grad);
        
        loginButton = (Button)findViewById(R.id.loginButton);
        registerButton = (Button)findViewById(R.id.registerButton);
        cb = (CheckBox)findViewById(R.id.cbRemember);
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        uid = (EditText)findViewById(R.id.uid);
        pwd = (EditText)findViewById(R.id.pwd);
        checkIfRemember();
    }

	private void checkIfRemember() {
		// TODO Auto-generated method stub
		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		String uid = sp.getString("uid", null);
		String pwd = sp.getString("pwd", null);
		if(uid != null && pwd != null){
			EditText etUid = (EditText)findViewById(R.id.uid);
			EditText etPwd = (EditText)findViewById(R.id.pwd);
			CheckBox cbRemember = (CheckBox)findViewById(R.id.cbRemember);
			etUid.setText(uid);
			etPwd.setText(pwd);
			cbRemember.setChecked(true);
		}
	}

	public void rememberMe(String uid, String pwd){
		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("uid", uid);
		editor.putString("pwd", pwd);
		editor.commit();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == loginButton){
			String editUid = uid.getText().toString().trim();
			String editPwd = pwd.getText().toString().trim();
			if(editUid.trim().equals("")){
				Toast.makeText(this, "请您输入用户名！",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if(editPwd.trim().equals("")){
				Toast.makeText(this, "请您输入密码！",
						Toast.LENGTH_SHORT).show();
				return;
			}
			myDialog = ProgressDialog.show(this, "进度", "正在加载中...", true);
			
			new Thread(){
				public void run(){
					///网络链接相关，待写！
					Socket s = null;
					DataOutputStream dout = null;
					DataInputStream din = null;
					try{
						s = new Socket("59.64.156.176", 9999);//连接服务器
						dout = new DataOutputStream(s.getOutputStream());
						din = new DataInputStream(s.getInputStream());
						dout.writeUTF("<#LOGIN#>"+uid.getText().toString()+"|"+pwd.getText().toString());
						String msg = din.readUTF();//接收服务器发来的消息
						
						System.out.println("<#LOGIN#>"+uid.getText().toString()+"|"+pwd.getText().toString());
						System.out.println(msg);
						
						if(msg.startsWith("<#LOGINOK#>")){//登录成功
							msg=msg.substring(11);
							Intent intent = new Intent();
							intent.setClass(MapOilActivity.this, MainActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("u_name", msg);
							bundle.putString("uid",uid.getText().toString());
							intent.putExtras(bundle);
							startActivity(intent);
							MapOilActivity.this.finish();
						}
						else if(msg.startsWith("<#LOGINERROR#>")){//登录失败
							myHandler.sendEmptyMessage(1);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					finally{
						try{
							if(din != null){
								din.close();
								din = null;
							}
						}
						catch(Exception e){
							e.printStackTrace();
						}
						try{
							if(dout != null){
								dout.close();
								dout = null;
							}							
						}
						catch(Exception e){
							e.printStackTrace();
						}
						try{
							if(s != null){
								s.close();
								s = null;
							}							
						}
						catch(Exception e){
							e.printStackTrace();
						}	
						myDialog.dismiss();
					}
				}
			}.start();
			if(cb.isChecked()){
				rememberMe(uid.getText().toString().trim(),pwd.getText().toString().trim());
			}
		}
		else if(v == registerButton){
			Intent intent = new Intent();
			intent.setClass(this, RegisterActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("action", "register");
			intent.putExtras(bundle);
			startActivity(intent);
			Log.d("a", "MapOil");
			this.finish();
			Log.d("a", "MapOil2");
			
		}
	}//onClick
}