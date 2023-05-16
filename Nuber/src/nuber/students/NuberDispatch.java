package nuber.students;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The core Dispatch class that instantiates and manages everything for Nuber
 *
 * @author james
 *
 */
public class NuberDispatch {

	/**
	 * The maximum number of idle drivers that can be awaiting a booking 
	 */
	private final int MAX_DRIVERS = 999;

	private boolean logEvents = false;

	
	//hashmap to storer the regions
	HashMap<String, NuberRegion> regions = new HashMap<String, NuberRegion>();

	//hashmap to instanitate the regioninfo
	private HashMap<String,Integer> regioninfo;

	//queue to store drivers
	ArrayBlockingQueue<Driver> driverqueue = new ArrayBlockingQueue<Driver>(MAX_DRIVERS);
	
	//for counting bookings
	AtomicInteger bookingCount ;


	/**
	 * Creates a new dispatch objects and instantiates the required regions and any other objects required.
	 * It should be able to handle a variable number of regions based on the HashMap provided.
	 *
	 * @param regionInfo Map of region names and the max simultaneous bookings they can handle
	 * @param logEvents Whether logEvent should print out events passed to it
	 */
	public NuberDispatch(HashMap<String, Integer> regionInfo, boolean logEvents)
	{

		this.regioninfo = regionInfo;
		for (String key : regionInfo.keySet()) {
			this.regions.put(key, new NuberRegion(this, key, regionInfo.get(key)));
		}
		this.logEvents = logEvents;

		//prinitng the nuber dispatch and the regions created
		System.out.println("Creating Nuber Dispatch");
		System.out.println("Creating "+ regionInfo.size() + " regions");
		for (String key : regionInfo.keySet())
			System.out.println("Creating Nuber region for "+key);
		System.out.println("Done creating "+regionInfo.size()+ " regions");
		bookingCount = new AtomicInteger();



	}

	/**
	 * Adds drivers to a queue of idle driver.
	 *
	 * Must be able to have drivers added from multiple threads.
	 *
	 * @param newDriver to add to the queue.
	 * @return Returns true if driver was added to the queue
	 * @throws InterruptedException
	 */
	public boolean addDriver(Driver newDriver) throws InterruptedException

	{
		
		if(driverqueue.size() < 999) {
			driverqueue.put(newDriver);
			return true;
		}
		else {
			return false;
		}

	}

	/**
	 * Gets a driver from the front of the queue
	 *
	 * Must be able to have drivers added from multiple threads.
	 *
	 * @return A driver that has been removed from the queue
	 * @throws InterruptedException
	 */
	public Driver getDriver() throws InterruptedException
	{

		return driverqueue.take();
	}

	/**
	 * Prints out the string
	 * 	    booking + ": " + message
	 * to the standard output only if the logEvents variable passed into the constructor was true
	 *
	 * @param booking The booking that's responsible for the event occurring
	 * @param message The message to show
	 */
	public void logEvent(Booking booking, String message) {

		if (!logEvents) return;
		System.out.println(booking.toString() + ": " + message);

	}

	/**
	 * Books a given passenger into a given Nuber region.
	 *
	 * Once a passenger is booked, the getBookingsAwaitingDriver() should be returning one higher.
	 *
	 * If the region has been asked to shutdown, the booking should be rejected, and null returned.
	 *
	 * @param passenger The passenger to book
	 * @param region The region to book them into
	 * @return returns a Future<BookingResult> object
	 * @throws InterruptedException
	 */
	public Future<BookingResult> bookPassenger(Passenger passenger, String region) throws InterruptedException {

		if(regions.containsKey(region)) {
			return regions.get(region).bookPassenger(passenger);
		} 
		else {
			NuberRegion nuberRegion = new NuberRegion(this, region, regioninfo.get(region));
			regions.put(region, nuberRegion);
			return nuberRegion.bookPassenger(passenger);
		}

	}

	/**
	 * Gets the number of non-completed bookings that are awaiting a driver from dispatch
	 *
	 * Once a driver is given to a booking, the value in this counter should be reduced by one
	 *
	 * @return Number of bookings awaiting driver, across ALL regions
	 */
	public int getBookingsAwaitingDriver()
	{
		//coutning the number of drivers availble region wise
		AtomicInteger awaitingCount = new AtomicInteger();
		for(NuberRegion region : regions.values()) {
			for(Booking booking: region.services) {
				if(booking.driver == null) {
					awaitingCount.incrementAndGet();
				}
			}
		}
		return awaitingCount.get();
	}

	/**
	 * Tells all regions to finish existing bookings already allocated, and stop accepting new bookings
	 */
	public void shutdown() 
	{ 
		//shutting down each region
		for (String key : regioninfo.keySet())
			regions.get(key).shutdown();
	}


}
