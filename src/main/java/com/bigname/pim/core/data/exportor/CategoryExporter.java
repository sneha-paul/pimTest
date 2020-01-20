package com.bigname.pim.core.data.exportor;

import com.bigname.pim.core.domain.Category;
import com.bigname.pim.core.domain.RelatedCategory;
import com.bigname.pim.core.persistence.dao.mongo.RelatedCategoryDAO;
import com.bigname.pim.core.service.CategoryService;
import com.m7.xtreme.xcore.util.BlackBox;
import com.m7.xtreme.common.util.POIUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.xcore.data.exporter.BaseExporter;
import com.m7.xtreme.xcore.util.Criteria;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sanoop on 31/01/2019.
 */
@Component
public class CategoryExporter implements BaseExporter<Category, CategoryService>, Job {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RelatedCategoryDAO relatedCategoryDAO;

    @Override
    public String getFileName(Type fileType) {
        return "CategoryExport" + PlatformUtil.getTimestamp() + fileType.getExt();
    }

    public boolean exportData(String filePath, Criteria criteria) {

        //Map<String, Category> categoriesLookupMap = categoryService.findAll(criteria, true).stream().collect(Collectors.toMap(Category::getId, e -> e)); TODO - In phase 2
        Map<String, Category> categoriesLookupMap = categoryService.getAll(null, true).stream().collect(Collectors.toMap(Category::getId, e -> e));
        Map<String, RelatedCategory> relatedCategoriesLookupMap = relatedCategoryDAO.findAll().stream().collect(Collectors.toMap(e -> e.getSubCategoryId(), e -> e));

        List<Map<String, Object>> hierarchy =  categoryService.getCategoryHierarchy(true);
        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList("CATEGORY_ID", "NAME", "PARENT_ID", "DESCRIPTION"));

        for (Iterator<Map.Entry<String,Category>> iter = categoriesLookupMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, Category> element = iter.next();
            String categoryKey = element.getKey();
            Category categoryData = categoriesLookupMap.get(categoryKey);
            if (relatedCategoriesLookupMap.containsKey(categoryKey)) {
                String parentId = relatedCategoriesLookupMap.get(categoryKey).getCategoryId();
                Category parentData = categoriesLookupMap.get(parentId);
                String parentCategoryName = parentData.getExternalId();
                data.add(Arrays.asList(categoryData.getExternalId(),categoryData.getCategoryName(),parentCategoryName,categoryData.getDescription()));

            } else {
                data.add(Arrays.asList(categoryData.getExternalId(),categoryData.getCategoryName(),null,categoryData.getDescription()));
            }
        }
        POIUtil.writeData(filePath,"Category", data);
        return  true;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        BlackBox logger = new BlackBox();
        boolean success = false;

        String fileName = getFileName(BaseExporter.Type.XLSX);
        String fileLocation = "/usr/local/pim/uploads/data/export/";
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String jobInstanceId = jobDataMap.getString("jobId");
        JobInstanceService jobInstanceService = (JobInstanceService) jobDataMap.get("jobInstanceService");
        JobInstance jobInstance = jobInstanceService.get(ID.INTERNAL_ID(jobInstanceId), false).orElse(null);
        logger.info("Job " + jobInstance.getJobName() + " started");
        jobInstance.setLogs(logger.toLogMessage());
        jobInstanceService.updateJobsDetails(jobInstance);

        CategoryService categoryService = (CategoryService) jobDataMap.get("service");
        Criteria criteria = Criteria.fromJson(jobDataMap.get("searchCriteria").toString());

        //Map<String, Category> categoriesLookupMap = categoryService.findAll(criteria, true).stream().collect(Collectors.toMap(Category::getId, e -> e)); TODO - In phase 2
        Map<String, Category> categoriesLookupMap = categoryService.getAll(null, true).stream().collect(Collectors.toMap(Category::getId, e -> e));
        Map<String, RelatedCategory> relatedCategoriesLookupMap = categoryService.getAll().stream().collect(Collectors.toMap(e -> e.getSubCategoryId(), e -> e));

        List<Map<String, Object>> hierarchy =  categoryService.getCategoryHierarchy(true);
        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList("CATEGORY_ID", "NAME", "PARENT_ID", "DESCRIPTION"));

        for (Iterator<Map.Entry<String,Category>> iter = categoriesLookupMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, Category> element = iter.next();
            String categoryKey = element.getKey();
            Category categoryData = categoriesLookupMap.get(categoryKey);
            if (relatedCategoriesLookupMap.containsKey(categoryKey)) {
                String parentId = relatedCategoriesLookupMap.get(categoryKey).getCategoryId();
                Category parentData = categoriesLookupMap.get(parentId);
                String parentCategoryName = parentData.getExternalId();
                data.add(Arrays.asList(categoryData.getExternalId(),categoryData.getCategoryName(),parentCategoryName,categoryData.getDescription()));

            } else {
                data.add(Arrays.asList(categoryData.getExternalId(),categoryData.getCategoryName(),null,categoryData.getDescription()));
            }
        }
        POIUtil.writeData(fileLocation + fileName,"Category", data);

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
