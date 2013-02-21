package chen.oil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener{
	Button registerButton2 = null;
	Button resetButton = null;
	EditText u_nameEditText = null;
	EditText pwdEditText1 = null;
	EditText pwdEditText2 = null;
	EditText emailEditText = null;
	ProgressDialog myDialog = null;
	Socket s = null;//����Socket������
	DataOutputStream dout = null;//�����
	DataInputStream din = null;//������	
	//������ز�������д
	Handler myHandler = new Handler(){
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			if(msg.what == 1){
				Bundle data = msg.getData();
				String uid = data.getString("uid");
				//System.out.println("registerActivity to MainActivity"+uid);
				String u_name = data.getString("u_name");
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("uid",uid);
				bundle.putString("u_name", u_name);
				intent.putExtras(bundle);
				startActivity(intent);
				RegisterActivity.this.finish();
			}
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("a","register");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String action = bundle.getString("action");
        if(action.equals("register")){
        	setContentView(R.layout.register);
        	registerButton2 = (Button)findViewById(R.id.registerButton2);
        	resetButton = (Button)findViewById(R.id.resetButton);
        	registerButton2.setOnClickListener(this);
        	resetButton.setOnClickListener(this);
        	u_nameEditText = (EditText)findViewById(R.id.u_nameEditText);
        	pwdEditText1 = (EditText)findViewById(R.id.pwdEditText1);
        	pwdEditText2 = (EditText)findViewById(R.id.pwdEditText2);
        	emailEditText = (EditText)findViewById(R.id.emailEditText);
        }
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view == registerButton2){
		    String u_name = u_nameEditText.getText().toString();
		    String u_pwd1 = pwdEditText1.getText().toString();
		    String u_pwd2 = pwdEditText2.getText().toString();
		    if(u_name.trim().equals("")){
		    	Toast.makeText(this, "�û�������Ϊ��", Toast.LENGTH_SHORT).show();
		    	return;
		    }
		    else if(u_pwd1.trim().equals("")){
				Toast.makeText(this, "���벻��Ϊ��", Toast.LENGTH_SHORT).show();
				return;
			}
		    else if(!u_pwd2.trim().equals(u_pwd1.trim())){
				Toast.makeText(this, "���벻��Ϊ��", Toast.LENGTH_SHORT).show();
				return;
			}
		    myDialog = ProgressDialog.show(this, "����", "���ڼ�����", true);
		    new Thread(){
		    	public void run(){
//		    		Intent intent = new Intent();
//		    		intent.setClass(RegisterActivity.this, MainActivity.class);
//		    		Bundle bundle = new Bundle();
//		    		bundle.putString("u_name", u_nameEditText.getText().toString().trim());
//		    		intent.putExtras(bundle);
//		    		startActivity(intent);
		    		try{
		    			s = new Socket("59.64.156.176", 9999);
				        dout = new DataOutputStream(s.getOutputStream());
				        din = new DataInputStream(s.getInputStream());

//		    			Message message = new Message();
//		    			message.what = 1;
//		    			myHandler.sendMessage(message);
		    		}catch (Exception e)
		    		{
		    			e.printStackTrace();
		    		}
		    		String u_name = u_nameEditText.getText().toString();//�û���
					String u_pwd1 = pwdEditText1.getText().toString();//����
					String u_Email = emailEditText.getText().toString();//�ʼ�
					String msg = "<#REGISTER#>"+u_name+"|"+u_pwd1+"|"+u_Email;
					try {
						dout.writeUTF(msg);//�����������ע����Ϣ
						String msg2 = din.readUTF();//���շ���������������Ϣ
						if(msg2.startsWith("<#REGISTEROK#>")){//ע��ɹ�
							msg2=msg2.substring(14);//��ȡ�ִ�
							String[] str = msg2.split("\\|");//�ָ��ַ���
							Message message = new Message();//������Ϣ
							Bundle data = new Bundle();//��������
							data.putString("uid", str[0]);//��bundle���������
							data.putString("u_name", str[1]);//��bundle���������
							message.what = 1;//������Ϣ��whatֵ
							message.setData(data);//�������
							myHandler.sendMessage(message);//������Ϣ
						}
					} catch (IOException e) {//�����쳣
						e.printStackTrace();//��ӡ�쳣
					} finally{
						try{
							if(dout != null){
								dout.close();
								dout = null;
							}
						}
						catch(Exception e){//�����쳣
							e.printStackTrace();//��ӡ�쳣��Ϣ
						}
						try{
							if(din != null){
								din.close();
								din = null;
							}
						}
						catch(Exception e){//�����쳣
							e.printStackTrace();//��ӡ�쳣��Ϣ
						}
						try{
							if(s != null){
								s.close();
								s = null;
							}
						}
						catch(Exception e){//�����쳣
							e.printStackTrace();//��ӡ�쳣��Ϣ
						}
						myDialog.dismiss();
					}				
		    	}
		    }.start();
		    
		}
		else if(view == resetButton){
			u_nameEditText.setText("");
			pwdEditText1.setText("");
			pwdEditText2.setText("");
			emailEditText.setText("");
		}
	}
	
	protected void onDestroy(){
		super.onDestroy();
		try {
			if(dout != null){
				dout.writeUTF("<#ClientDown#>");//֪ͨ�������ͻ����˳�
			}
		} catch (IOException e) {//�����쳣
			e.printStackTrace();//��ӡ�Ų�
		}

	}
}
