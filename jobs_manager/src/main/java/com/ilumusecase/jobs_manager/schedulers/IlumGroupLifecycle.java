package com.ilumusecase.jobs_manager.schedulers;

import java.time.Duration;
import java.time.LocalDateTime;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.manager.Manager;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;

@Component
public class IlumGroupLifecycle implements Job{

    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private Manager manager;
    @Autowired
    private Scheduler scheduler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String ilumGroupId = jobDataMap.getString("ilumGroupId");

        
        IlumGroup ilumGroup = repositoryFactory.getIlumGroupRepository().retrieveById(ilumGroupId);

        //get current job state:
        String state = manager.getJobInfo(ilumGroup.getCurrentJob()).get("state").asText();
        if(!ilumGroup.getCurrentJob().getState().equals(state)){
            repositoryFactory.getJobRepository().updateJobFull(ilumGroup.getCurrentJob());
        }

        //do something depending on the state
        if(ilumGroup.getCurrentJob().getState().equals("FINISHED")){
            if(ilumGroup.getMod().equals("TEST")){
                if(ilumGroup.getCurrentTestingIndex() < ilumGroup.getTestingJobs().size()){
                    ilumGroup.setCurrentTestingIndex(ilumGroup.getCurrentTestingIndex() + 1);
                    ilumGroup.setCurrentJob(ilumGroup.getTestingJobs().get(ilumGroup.getCurrentIndex()));
                    repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

                    manager.submitJob(ilumGroup.getCurrentJob(), null);

                    return;
                }else{
                    if(ilumGroup.getCurrentIndex() < ilumGroup.getJobs().size()){
                        ilumGroup.setCurrentIndex(ilumGroup.getCurrentIndex() + 1);
                        ilumGroup.setCurrentJob(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()));
                        ilumGroup.setMod("NORMAL");
                        repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

                        manager.submitJob(ilumGroup.getCurrentJob(), null);
                    }else{
                        //exit
                        try {
                            scheduler.interrupt(new JobKey(ilumGroup.getId()));
                        } catch (UnableToInterruptJobException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }else{
                ilumGroup.setMod("TEST");
                ilumGroup.setCurrentTestingIndex(0);
                ilumGroup.setCurrentJob(ilumGroup.getTestingJobs().get(0));
                repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);
            
                manager.submitJob(ilumGroup.getCurrentJob(), null);                 
            
                return;
            }      
        }else if(ilumGroup.getCurrentJob().getState().equals("ERROR")){
            if(ilumGroup.getMod().equals("TEST")){
                if(ilumGroup.getCurrentTestingIndex() < ilumGroup.getTestingJobs().size()){
                    ilumGroup.setCurrentTestingIndex(ilumGroup.getCurrentTestingIndex() + 1);
                    ilumGroup.setCurrentJob(ilumGroup.getTestingJobs().get(ilumGroup.getCurrentIndex()));
                    repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

                    manager.submitJob(ilumGroup.getCurrentJob(), null);

                    return;
                }else{
                    if(ilumGroup.getCurrentIndex() < ilumGroup.getJobs().size()){
                        ilumGroup.setCurrentIndex(ilumGroup.getCurrentIndex() + 1);
                        ilumGroup.setCurrentJob(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()));
                        ilumGroup.setMod("NORMAL");
                        repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

                        manager.submitJob(ilumGroup.getCurrentJob(), null);
                    }else{
                        //exit
                        try {
                            scheduler.interrupt(new JobKey(ilumGroup.getId()));
                        } catch (UnableToInterruptJobException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }else if(ilumGroup.getMod().equals("NORMAL")){
                if(ilumGroup.getCurrentIndex() < ilumGroup.getJobs().size()){
                    ilumGroup.setCurrentIndex(ilumGroup.getCurrentIndex() + 1);
                    ilumGroup.setCurrentJob(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()));
                    ilumGroup.setMod("NORMAL");
                    repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

                    manager.submitJob(ilumGroup.getCurrentJob(), null);
                }else{
                    //exit
                    try {
                        JobKey jobKey = new JobKey(ilumGroup.getId());
                        scheduler.interrupt(jobKey);
                        scheduler.deleteJob(jobKey);
                    } catch (UnableToInterruptJobException e) {
                        throw new RuntimeException(e);
                    } catch (SchedulerException e) {
                        throw new RuntimeException(e);
                    }
                }                
                
                return;
            }else{
                throw new RuntimeException("Invalid mod value");
            }     
        }else{
            long timePassed = Duration.between(LocalDateTime.now(), ilumGroup.getCurrentStartTime()).getSeconds();
            
            if(timePassed >= ilumGroup.getIlumGroupConfiguraion().getMaxJobDuration()){
                manager.stopJob(ilumGroup.getCurrentJob());
                if(ilumGroup.getMod().equals("NORMAL")){
                    ilumGroup.setMod("TEST");
                    ilumGroup.setCurrentTestingIndex(0);
                    ilumGroup.setCurrentJob(ilumGroup.getTestingJobs().get(0));
                    repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);
                
                    manager.submitJob(ilumGroup.getCurrentJob(), null);                 
                
                    return;
                }else if(ilumGroup.getMod().equals("TEST")){
                    if(ilumGroup.getCurrentTestingIndex() < ilumGroup.getTestingJobs().size()){
                        ilumGroup.setCurrentTestingIndex(ilumGroup.getCurrentTestingIndex() + 1);
                        ilumGroup.setCurrentJob(ilumGroup.getTestingJobs().get(ilumGroup.getCurrentIndex()));
                        repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

                        manager.submitJob(ilumGroup.getCurrentJob(), null);

                        return;
                    }else{
                        if(ilumGroup.getCurrentIndex() < ilumGroup.getJobs().size()){
                            ilumGroup.setCurrentIndex(ilumGroup.getCurrentIndex() + 1);
                            ilumGroup.setCurrentJob(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()));
                            ilumGroup.setMod("NORMAL");
                            repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

                            manager.submitJob(ilumGroup.getCurrentJob(), null);
                        }else{
                            //exit
                            try {
                                JobKey jobKey = new JobKey(ilumGroup.getId());
                                scheduler.interrupt(jobKey);
                                scheduler.deleteJob(jobKey);
                            } catch (UnableToInterruptJobException e) {
                                throw new RuntimeException(e);
                            } catch (SchedulerException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }else{
                    throw new RuntimeException("Mod has invalid value");
                }
                
            }else{
                return;
            }
        }
    }
    
}
