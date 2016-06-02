package com.brownfield.vre.jobs;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * The Class JobScheduler.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class JobScheduler {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger("JobScheduler.class");

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		JobScheduler js = new JobScheduler();
		js.runCronScheduler();
	}

	/**
	 * Run cron scheduler.
	 */
	public void runCronScheduler() {
		// First we must get a reference to a scheduler
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched;
		try {
			LOGGER.info("------- Initializing ----------------------");
			sched = sf.getScheduler();

			LOGGER.info("------- Initialization Complete -----------");

			LOGGER.info("------- Scheduling Job  -------------------");

			// define the job and tie it to our JobsMonitor class
			JobDetail job = newJob(JobsMonitor.class).withIdentity("MonitorJobs", "VRE").build();

			// Trigger the job to run on the next round minute
			CronTrigger trigger = newTrigger().withIdentity("trigger1", "VRE")
					.withSchedule(cronSchedule("0 0/5 2-16 * * ?")).build();
			sched.scheduleJob(job, trigger);
			LOGGER.info(job.getKey() + " will run every five minutes, but only between 9am and 11am");

			// Start up the scheduler (nothing can actually run until the
			// scheduler has been started)
			sched.start();

			LOGGER.info("------- Started Scheduler -----------------");

		} catch (SchedulerException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

}
