package com.example.springbatch.job.processor;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.springbatch.entity.Teacher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Processor 필터
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProcessorNullJobConfiguration {
	public static final String JOB_NAME = "ProcessorNullBatch";
	public static final String BEAN_PREFIX = JOB_NAME + "_";

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory emf;

	@Value("${chunkSize:1000}")
	private int chunkSize;

	@Bean(JOB_NAME)
	public Job job() {
		return jobBuilderFactory.get(JOB_NAME)
			.preventRestart()
			.start(step())
			.build();
	}

	@Bean(BEAN_PREFIX + "step")
	@JobScope
	public Step step() {
		return stepBuilderFactory.get(BEAN_PREFIX + "step")
			.<Teacher, Teacher>chunk(chunkSize)
			.reader(reader2())
			.processor(processor2())
			.writer(writer2())
			.build();
	}

	@Bean
	public JpaPagingItemReader<Teacher> reader2() {
		return new JpaPagingItemReaderBuilder<Teacher>()
			.name(BEAN_PREFIX+"reader")
			.entityManagerFactory(emf)
			.pageSize(chunkSize)
			.queryString("SELECT t FROM Teacher t")
			.build();
	}

	/**
	 * null로 넘겨주면 필터가 되어서
	 * 다음 단계가 진행이 되지 않는다.
	 * @return
	 */
	@Bean
	public ItemProcessor<Teacher, Teacher> processor2() {
		return teacher -> {

			boolean isIgnoreTarget = teacher.getId() % 2 == 0L;
			if(isIgnoreTarget){
				log.info(">>>>>>>>> Teacher name={}, isIgnoreTarget={}", teacher.getName(), isIgnoreTarget);
				return null;
			}

			return teacher;
		};
	}

	private ItemWriter<? super Teacher> writer2() {
		return items -> {
			for (Teacher item : items) {
				log.info("Teacher Name={}", item.getName());
			}
		};
	}
}
