package com.ilumusecase.jobs_manager.repositories.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.JobsFileRepositoryInterface;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.MongoJobsFile;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;

@Repository
public class JobsFileMongo implements JobsFileRepositoryInterface {

    @Autowired
    private MongoJobsFile mongoJobsFile;

    @Override
    public JobsFile retrieveJobsFileById(String id) {
        return mongoJobsFile.findById(id).get();
    }

    @Override
    public List<JobsFile> retrieveJobsFilesByAuthorUsername(String username) {
        return mongoJobsFile.findByAuthorUsername(username);
    }

    @Override
    public List<JobsFile> retrieveJobsFilesByJobNodeId(String id) {
        return mongoJobsFile.findByJobNodeId(id);
    }

    @Override
    public JobsFile updateJobsFileFull(JobsFile jobsFile) {
        return mongoJobsFile.save(jobsFile);
    }

    @Override
    public void deleteJobsFileById(String id) {
        mongoJobsFile.deleteById(id);
    }

    @Override
    public List<JobsFile> retrieveJobsFilesOfJobNode(
        String jobNodeId, String query, String extenstion,
        String className, String publisher, Integer pageSize, Integer pageNumber
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return mongoJobsFile.retrieveJobsFilesOfJobNode(jobNodeId, query, extenstion, className, publisher, pageable);
    }

    @Override
    public long countJobsFilesOfJobNode(
        String jobNodeId, String query, String extenstion, String className, String publisher
    ) {
        return mongoJobsFile.countJobsFilesOfJobNode(jobNodeId, query, extenstion, className, publisher);
    }
    
}
