package com.bigname.pim.data.exportor;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.service.CatalogService;
import com.m7.xtreme.common.criteria.model.SimpleCriteria;
import com.m7.xtreme.common.util.BlackBox;
import com.m7.xtreme.common.util.POIUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.data.exporter.BaseExporter;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xplatform.domain.JobInstance;
import com.m7.xtreme.xplatform.service.JobInstanceService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by sruthi on 25-01-2019.
 */
@Component
public class CatalogExporter implements BaseExporter<Catalog, CatalogService>, Job {

    @Autowired
    private CatalogService catalogService;

    @Override
    public String getFileName(Type fileType) {
        return "CatalogExport" + PlatformUtil.getTimestamp() + fileType.getExt();
    }

    @Override
    public boolean exportData(String filePath) {
        List<Catalog> catalogData = catalogService.getAll(null,true);

        Map<String, Object[]> data = new TreeMap<>();

        data.put("1", new Object[]{"CATALOG_ID", "CATALOG_NAME", "DESCRIPTION", "ACTIVE", "DISCONTINUED", "ID" });
        int i=2;
        for (Iterator<Catalog> iter = catalogData.iterator(); iter.hasNext(); ) {
            Catalog element = iter.next();
            data.put(Integer.toString(i), new Object[]{element.getExternalId(), element.getCatalogName(), element.getDescription(), element.getActive(), element.getDiscontinued(), element.getId() });
            i++;
        }
        POIUtil.writeData(filePath, "Catalog", data);
        return true;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        BlackBox logger = new BlackBox();
        boolean success = false;
        String fileName = getFileName(BaseExporter.Type.XLSX);
        String fileLocation = "/usr/local/pim/uploads/data/export/";
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        //String jobInstanceId = jobExecutionContext.getJobDetail().getKey().getName();
        String jobInstanceId = jobDataMap.getString("jobId");
        JobInstanceService jobInstanceService = (JobInstanceService) jobDataMap.get("jobInstanceService");
        JobInstance jobInstance = jobInstanceService.get(ID.INTERNAL_ID(jobInstanceId), false).orElse(null);
        if(ValidationUtil.isNotEmpty(jobInstance)) {
            logger.info("Job " + jobInstance.getJobName() + " started");
            jobInstance.setLogs(logger.toLogMessage());
            jobInstanceService.updateJobsDetails(jobInstance);
        }
        CatalogService catalogService = (CatalogService) jobDataMap.get("service");
        //List<Catalog> catalogData = catalogService.getAll(null,true);
        SimpleCriteria criteria = (SimpleCriteria) jobDataMap.get("searchCriteria");
        List<Catalog> catalogData = catalogService.findAll(criteria,true);

        Map<String, Object[]> data = new TreeMap<>();

        data.put("1", new Object[]{"CATALOG_ID", "CATALOG_NAME", "DESCRIPTION", "ACTIVE", "DISCONTINUED", "ID" });
        int i=2;
        for (Iterator<Catalog> iter = catalogData.iterator(); iter.hasNext(); ) {
            Catalog element = iter.next();
            data.put(Integer.toString(i), new Object[]{element.getExternalId(), element.getCatalogName(), element.getDescription(), element.getActive(), element.getDiscontinued(), element.getId() });
            i++;
        }

        POIUtil.writeData(fileLocation + fileName, "Catalog", data);
        success = true;
        logger.info("Job completed");
        System.out.println("============== : "+jobExecutionContext.getJobDetail().getJobDataMap().getString("jobService"));

        System.out.println("Job Executed");
        String jobName = jobExecutionContext.getJobDetail().getKey().getName();
        String jobType = jobExecutionContext.getJobDetail().getKey().getGroup();
        System.out.println("JobType : " + jobType);
        System.out.println("========" + jobExecutionContext.getFireInstanceId());
        System.out.println(jobExecutionContext.getTrigger().getStartTime());
        Instant endDateInstant = Instant.ofEpochMilli(jobExecutionContext.getScheduledFireTime().getTime());
        LocalDateTime endTime = LocalDateTime.ofInstant(endDateInstant, ZoneId.systemDefault());
        System.out.println("End Time : " + endTime);
        Instant startDateInstant = Instant.ofEpochMilli(jobExecutionContext.getTrigger().getStartTime().getTime());
        LocalDateTime startDateTime = LocalDateTime.ofInstant(startDateInstant, ZoneId.systemDefault());
        if(success) {
            JobInstance jobInstance1 = jobInstanceService.get(ID.INTERNAL_ID(jobInstanceId), false).orElse(null);
            jobInstance1.setLogs(logger.toLogMessage());
            jobInstance1.setStatus("Completed");
            jobInstance1.setCompletedTime(endTime);
            jobInstance1.setActualStartTime(startDateTime);
            jobInstanceService.updateJobsDetails(jobInstance1);
        }
    }
}
