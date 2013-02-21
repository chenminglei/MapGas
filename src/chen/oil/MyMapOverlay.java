package chen.oil;

import java.util.List;

import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MyMapOverlay extends Overlay{
    boolean flagMove = false;
    public boolean onTouchEvent(MotionEvent event, MapView mv){
		if(event.getAction()== MotionEvent.ACTION_MOVE){
			flagMove = true;
		}else if(event.getAction()== MotionEvent.ACTION_DOWN){
			flagMove = false;
		}else if(MyBallonOverlay.currentBallon==null&&
				!flagMove&&event.getAction()== MotionEvent.ACTION_UP){
			GeoPoint gp = mv.getProjection().fromPixels(
					(int)event.getX(),
					(int)event.getY());
		//显示点击处的经纬度
        String latStr=Math.round(gp.getLatitudeE6()/1000.00)/1000.0+"";//纬度
    	String longStr=Math.round(gp.getLongitudeE6()/1000.00)/1000.0+"";//经度        	
    	//清除其他气球的showWindow标记
    	List<Overlay> overlays = mv.getOverlays(); 
    	for(Overlay ol:overlays){//清除其他气球的showWindow标记
    		if(ol instanceof MyBallonOverlay){
    			overlays.remove(ol);
    		}
    	} 
    	//在点击位置添加新气球
    	MyBallonOverlay mbo=new MyBallonOverlay(
    			gp,//气球的坐标
    			"店铺位置为：\n经度："+longStr+"\n纬度："+latStr//气球的信息
    	); 
        overlays.add(mbo); 
        return true;
		}
    return false;
}
	//public GeoPoint getGeoPoint() {
		// TODO Auto-generated method stub
	//	return null;
	//}

}