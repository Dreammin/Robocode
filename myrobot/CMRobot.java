package CMTrank;
import robocode.*;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;



// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * CMRobot - a robot by (your name here)
 */
public class CMRobot extends Robot
{
	/**
	 * run: CMRobot's default behavior
	 */
	int count = 0; // Keeps track of how long we've
	// been searching for our target
	double gunTurnAmt; // How much to turn our gun when searching
	String trackName; // Name of the robot we're currently tracking

	boolean left = true;
	boolean right;
	/**
	 * run:  Tracker's main run function
	 */
	public void run() {
		// Set colors
		setBodyColor(new Color(128, 128, 50));
		setGunColor(new Color(50, 50, 20));
		setRadarColor(new Color(200, 200, 70));
		setScanColor(Color.blue);
		setBulletColor(Color.white);

		// Prepare gun
		trackName = null; // Initialize to not tracking anyone
		setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
		gunTurnAmt = 10; // Initialize gunTurn to 10

		
		// Loop forever
		while (true) {

			// turn the Gun (looks for enemy)
			turnGunRight(gunTurnAmt);
			// Keep track of how long we've been looking
			count++;
			// If we've haven't seen our target for 2 turns, look left
			if (count > 2) {
				gunTurnAmt = -10;
			}
			// If we still haven't seen our target for 5 turns, look right
			if (count > 5) {
				gunTurnAmt = 10;
			}
			// If we *still* haven't seen our target after 10 turns, find another target
			if (count > 11) {
				trackName = null;
			}

		}
	}

	/**
	 * onScannedRobot:  Here's the good stuff
	 */
	public void onScannedRobot(ScannedRobotEvent e) {

		double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

		// If we have a target, and this isn't it, return immediately
		// so we can get more ScannedRobotEvents.
		
		if (trackName != null && !e.getName().equals(trackName)) {
			return;
		}

		// If we don't have a target, well, now we do!
		if (trackName == null) {
			trackName = e.getName();
			out.println("Tracking " + trackName);
		}
		// This is our target.  Reset count (see the run method)
		count = 0;
		// If our target is too far away, turn and move toward it.
		if (e.getDistance() > 450) {
			
			gunTurnAmt = bearingFromGun;

			turnGunRight(gunTurnAmt); // Try changing these to setTurnGunRight,
			turnRight(e.getBearing()); // and see how much Tracker improves...
			// (you'll have to make Tracker an AdvancedRobot)
			ahead(e.getDistance() - 400);
			fire(1);
			return;
		}

		// Our target is close.
		gunTurnAmt = bearingFromGun;
		turnGunRight(gunTurnAmt);
		fire(2);

		// Our target is too close!  Back up.
		if (e.getDistance() < 250) {
			if (e.getBearing() > -90 && e.getBearing() <= 90) {
				back(40);
			} else {
				ahead(40);
			}
			fire(4);
		}
		if (e.getDistance() < 100) {
			fire(5);
			if (e.getBearing() > -180 && e.getBearing() <= 180) {
				back(40);
			} else {
				ahead(40);
			}
		}
		
		scan();
	}

	/**
	 * onHitRobot:  Set him as our new target
	 */
	public void onHitRobot(HitRobotEvent e) {

		// If he's in front of us, set back up a bit.
		if (e.getBearing() > -90 && e.getBearing() < 90) {
			back(100);
		} // else he's in back of us, so set ahead a bit.
		else {
			ahead(100);
		}
		
	}
	

	/**
	 * onWin:  Do a victory dance
	 */
	public void onWin(WinEvent e) {
		turnRight(36000);
	}
	
	/**
	 * We were hit!  Turn perpendicular to the bullet,
	 * so our seesaw might avoid a future shot.
	 */
	int dist = 50;
	public void onHitByBullet(HitByBulletEvent e) 
	{		

		turnRight(normalRelativeAngleDegrees(90 - (getHeading() - e.getHeading())));
		ahead(dist);
		dist *= -1;
		scan();
		
	}
	/**
	 * onHitWall:  Handle collision with wall.
	 */
	public void onHitWall(HitWallEvent e) {
		// Bounce off!
		back(200);
		turnRight(90);
	}

}
