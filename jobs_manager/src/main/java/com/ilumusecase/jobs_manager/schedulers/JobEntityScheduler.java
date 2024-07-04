package com.ilumusecase.jobs_manager.schedulers;

import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;

@Service
public class JobEntityScheduler {
    
    @Autowired
    private Scheduler scheduler;


    public void startIlumGroupLifecycle(IlumGroup ilumGroup) throws SchedulerException{
        JobDetail jobDetail = JobBuilder.newJob(IlumGroupLifecycle.class)
            .withIdentity(new JobKey(ilumGroup.getId()))
            .storeDurably()
            .build();


        Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.MINUTE))
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }


}
