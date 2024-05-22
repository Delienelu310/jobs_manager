package com.ilumusecase.jobs_manager.schedulers;

import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ilumusecase.jobs_manager.resources.IlumGroup;
import com.ilumusecase.jobs_manager.resources.JobEntity;

@Service
public class JobEntityScheduler {
    
    @Autowired
    private Scheduler scheduler;

    public void scheduleJobEntityStop(JobEntity jobEntity) throws SchedulerException{
        JobDetail jobDetail = JobBuilder.newJob(StopJobEntity.class)
            .withIdentity(new JobKey(jobEntity.getId(), jobEntity.getGroupId()))
            .storeDurably()
            .build();
 

		Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.MINUTE))
            .build();

        scheduler.scheduleJob(jobDetail, trigger);

    }

    public void deleteJobEntityStop(JobEntity jobEntity) throws SchedulerException{
        scheduler.deleteJob(new JobKey(jobEntity.getId(), jobEntity.getGroupId()));
    }

    public void startGroupStatusCheckScheduler(IlumGroup ilumGroup) throws SchedulerException{

        JobDetail jobDetail = JobBuilder.newJob(CheckJobStatus.class)
            .withIdentity(new JobKey(ilumGroup.getId(), ilumGroup.getJobNode().getId()))
            .storeDurably()
            .build();

        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
			.withIntervalInSeconds(10)
			.repeatForever();

		Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withSchedule(simpleScheduleBuilder)
            .build();

        scheduler.scheduleJob(jobDetail, trigger);

    }

    public void deleteGroupStatusCheckScheduler(IlumGroup ilumGroup) throws SchedulerException{
        scheduler.deleteJob(new JobKey(ilumGroup.getId(), ilumGroup.getJobNode().getId()));
    }

}
