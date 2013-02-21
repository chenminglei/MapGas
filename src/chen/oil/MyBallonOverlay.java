package chen.oil;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import android.graphics.*;

class MyBallonOverlay extends Overlay{
	final static int picWidth=20;  //����ͼ�Ŀ��
	final static int picHeight=34; //����ͼ�ĸ߶�
	final static int arcR=8;//��Ϣ���ڵ�Բ�ǰ뾶
	
	static MyBallonOverlay currentBallon=null;//��ʾ��ǰѡ�е�����
	String msg;	
	
	boolean showWindow=false;
	
	GeoPoint gp;//�������Ӧ�ľ�γ��
   
	public MyBallonOverlay(GeoPoint gp,String msg){
		this.gp=gp;
		this.msg=msg;
	}
	
	public GeoPoint getGeoPoint(){//��ø�����ľ�γ��GeoPoint
		return gp;
	}
	
    @Override 
    public boolean onTouchEvent(MotionEvent event, MapView mv) {
        if(currentBallon!=null&&currentBallon!=this){
        	//����ǰ����Ϊ���Ҳ����Լ���ʲô������
        	return false;
        }
    	if(event.getAction() == MotionEvent.ACTION_DOWN){
    		//���������ϰ��������õ�ǰ����Ϊ�Լ����ҵ�ǰ״̬Ϊ��������      	
        	int x=(int)event.getX();
            int y=(int)event.getY();
            Point p= getPoint(mv); 
            
            int xb=p.x-picWidth/2;
            int yb=p.y-picHeight;
            
            if(x>=xb&&x<xb+picWidth&&y>=yb&&y<yb+picHeight){
            	//�����Լ���������ϰ����������Լ�Ϊ��ǰ����            	
            	currentBallon=this;
            	return true;
            }
        }
    	else if (event.getAction() == MotionEvent.ACTION_MOVE) {
    		//�ƶ��¼����ص�ǰ����״̬ ����ǰ���������򷵻�true ���������ƶ��¼�
    		return currentBallon!=null;
    	}    		
        else if (event.getAction() == MotionEvent.ACTION_UP) {
        	//��ȡ���ر�λ��
            int x=(int)event.getX();
            int y=(int)event.getY();
            
            //��ȡ��������Ļ�ϵ����귶Χ
            Point p= getPoint(mv);             
            int xb=p.x-picWidth/2;
            int yb=p.y-picHeight;           
            
            if(currentBallon==this&&x>=xb&&x<xb+picWidth&&y>=yb&&y<yb+picHeight){
            	//����ǰ����Ϊ�Լ����ڵ�ǰ������̧�𴥿�����ʾ��ǰ��������	
            	//��ʾ�����ݺ���յ�ǰ����
            	currentBallon=null;     
            	showWindow=!showWindow;
            	
            	List<Overlay> overlays = mv.getOverlays();
            	overlays.remove(this);//ɾ�������������
            	overlays.add(this);//�������λ����������
            	for(Overlay ol:overlays){//������������showWindow���
            		if(ol instanceof MyBallonOverlay){
            			if(ol!=this){
            				((MyBallonOverlay)ol).showWindow=false;
            			}             			
            		}
            	} 
            	return true;
            }
            else if(currentBallon==this){
            	//����ǰ����Ϊ�Լ���̧�𴥿ز����Լ������������״̬������true
            	currentBallon=null;
            	return true;            	
            }            
        }
        return false;
    }
    @Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {			
    	//����γ�ȷ������Ļ�ϵ�XY����
    	Point p= getPoint(mapView);  	    	
    	//������ָ��λ�û�������
		canvas.drawBitmap(MainActivity.bitmap, p.x-picWidth/2, p.y-picHeight, null);
		
		if(showWindow){//���showWindowΪtrue����ʾ��Ϣ����
			drawWindow(canvas,p,160);
		}
		//���ø������
		super.draw(canvas, mapView, shadow);
	}
    
    public Point getPoint(MapView mapView){//����γ�ȷ������Ļ�ϵ�XY����
    	Projection projettion = mapView.getProjection();
		Point p = new Point();
		projettion.toPixels(gp, p); 
		return p;
    }
    
	public void drawWindow(Canvas canvas,Point p,int winWidth){//������Ϣ���ڵķ���
		int charSize=15;
		int textSize=16;
		int leftRightPadding=2;
		
		//Ϊ��Ϣ����
		int lineWidth=winWidth-leftRightPadding*2;//ÿ�еĿ��
		int lineCharCount=lineWidth/(charSize);	//ÿ���ַ���
		//ɨ��������Ϣ�ַ�������
		ArrayList<String> alRows=new ArrayList<String>();
		String currentRow="";
		for(int i=0;i<msg.length();i++){			
			char c=msg.charAt(i);
			if(c!='\n'){
				currentRow=currentRow+c;
			}
			else{
				if(currentRow.length()>0){
					alRows.add(currentRow);
				}				
				currentRow="";
			}
			if(currentRow.length()==lineCharCount){
				alRows.add(currentRow);
				currentRow="";
			}
		}
		if(currentRow.length()>0){
			alRows.add(currentRow);
		}
		int lineCount=alRows.size();//������
		int winHeight=lineCount*(charSize)+2*arcR;//����߶�
		//����paint����
		Paint paint=new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(textSize);	
		//���� ��Ϣ����Բ�Ǿ���
		paint.setARGB(255, 255,251,239);
		int x1=p.x-winWidth/2;
		int y1=p.y-picHeight-winHeight-1;
		int x2=x1+winWidth;
		int y2=y1+winHeight;		
		RectF r=new RectF(x1,y1,x2,y2);		
		canvas.drawRoundRect(r, arcR, arcR, paint);
		paint.setARGB(255,198,195,198);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		canvas.drawRoundRect(r, arcR, arcR, paint);
		
		//ѭ������ÿ������
		paint.setStrokeWidth(0);
		paint.setARGB(255, 10, 10, 10);		
		int lineY=y1+arcR+charSize;
		for(String lineStr:alRows){	
System.out.println("lineStr"+lineStr);		
			for(int j=0;j<lineStr.length();j++){
				String colChar=lineStr.charAt(j)+"";
				canvas.drawText(colChar, x1+leftRightPadding+j*charSize, lineY, paint);
			}
			lineY=lineY+charSize;
		}
	}
}