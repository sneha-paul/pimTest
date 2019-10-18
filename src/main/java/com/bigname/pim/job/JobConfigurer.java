package com.bigname.pim.job;

import com.bigname.pim.api.domain.ProductVariant;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Configuration
@EnableBatchProcessing
@Component
public class JobConfigurer {
    private static final String JOB_NAME = "ElasticSearch";

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private final ItemWriter<ProductVariant> itemWriter;
    private final ItemReader<ProductVariant> itemReader;

    @Autowired
    public JobConfigurer(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, ItemWriter<ProductVariant> itemWriter, ItemReader<ProductVariant> itemReader) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;

        this.itemWriter = itemWriter;
        this.itemReader = itemReader;
    }

    public Job synchroniseJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(step())
                .build();
    }

    private Step step() {
        return stepBuilderFactory.get("Elastic-Search-load")
                .<ProductVariant, ProductVariant>chunk(100)
                .reader(itemReader)
                .writer(itemWriter)
                .build();
    }
}
