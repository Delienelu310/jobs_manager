package com.ilumusecase.jobs_manager.schedulers;

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

import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;

@Service
public class JobEntityScheduler {
    
    @Autowired
    private Scheduler scheduler;


    public void startIlumGroupLifecycle(IlumGroup ilumGroup) throws SchedulerException{
        JobDetail jobDetail = JobBuilder.newJob(IlumGroupLifecycle.class)
            .withIdentity(new JobKey("IlumGroupLifecycle_" + ilumGroup.getId()))
            .usingJobData("ilumGroupId", ilumGroup.getId())
            .storeDurably()
            .build();


        Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .startNow()
            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(40)
                .repeatForever())
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void stopIlumGroupLifecycle(IlumGroup ilumGroup) throws SchedulerException{
        JobKey jobKey = new JobKey("IlumGroupLifecycle_" + ilumGroup.getId());
     
        if (scheduler.checkExists(jobKey)) {
    
            scheduler.pauseJob(jobKey);


            scheduler.deleteJob(jobKey);
           
        } else {
            throw new RuntimeException("Quartz Job does not exist: " + jobKey.getName());
        }
    }


}
