package nuber.students;

public class Driver extends Person  implements Runnable{

	private Passenger passenger;

	public Driver(String driverName, int maxSleep){
		super(driverName, maxSleep);
	}

	
	/**
	 * Stores the provided passenger as the driver's current passenger and then
	 * sleeps the thread for between 0-maxDelay milliseconds.
	 * 
	 * @param newPassenger Passenger to collect
	 * @throws InterruptedException
	 */
	public void pickUpPassenger(Passenger newPassenger) throws InterruptedException
	{
		this.passenger = newPassenger;
		// sleeping the thread for between 0-maxDelay milliseconds.
		 Thread.sleep((int)(Math.random() * maxSleep));
	}

	/**
	 * Sleeps the thread for the amount of time returned by the current
	 * passenger's getTravelTime() function
	 * 
	 * @throws InterruptedException
	 */
	public void driveToDestination()throws InterruptedException
	{
		//Sleepinh the thread for the amount of time returned by the passenger.getTravelTime()
		Thread.sleep(passenger.getTravelTime());
	}

	@Override
	public void run() {

	
	}
	
	
}
