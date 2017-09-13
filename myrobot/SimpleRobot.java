package com.example.project;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;


/**
 * SimpleRobot 可以追随目标扫描 并发射子弹 但是通常会发射空弹 
 */
public class SimpleRobot extends AdvancedRobot 
{
	
    public void run() 
    { 
        setAdjustGunForRobotTurn( true ); 
        setAdjustRadarForGunTurn( true );         
        while(true){
        	turnRadarLeft(360);
        }
    }
    
    public void onScannedRobot(ScannedRobotEvent e) 
    { 
         double bearingRadian = e.getBearingRadians();
         double distance = e.getDistance();
         double direction = bearingRadian + getHeadingRadians(); 

         double Offset = rectify(direction - getRadarHeadingRadians()); 
         setTurnRadarRightRadians( Offset*1.5); 
         
         double gunTurn = direction - getGunHeadingRadians();
         setTurnGunRightRadians(rectify(gunTurn)*1.5);
         
         double headTurn = direction - getHeadingRadians();
         setTurnRightRadians(rectify(headTurn));

         if (distance > 200) {
     		setAhead(50);
     	 }else{
     		setBack(200-distance);
         }
         execute();
         fire(400/distance);
    }
    public double rectify ( double angle ) 
    { 
    	if ( angle < -Math.PI ) 
    		angle += 2*Math.PI; 
    	if ( angle > Math.PI ) 
    		angle -= 2*Math.PI; 
    	return angle; 
    }
    	
}