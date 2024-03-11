package com.example.springbatch.job.writer;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.springbatch.entity.Pay;
import com.example.springbatch.entity.Pay2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaItemWriterJobConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory; // JPA 사용

	private static final int chunkSize = 10;

	@Bean
	public Job jpaItemWriterJob() {
		return jobBuilderFactory.get("jpaItemWriterJob")
			.start(jpaItemWriterStep())
			.build();
	}

	@Bean
	public Step jpaItemWriterStep(){
		return stepBuilderFactory.get("jpaItemWriterStep")
			.<Pay, Pay2>chunk(chunkSize)
			.reader(jpaItemWriterReader())
			.processor(jpaItemProcessor())
			.writer(jpaItemWriter())
			.build();
	}

	@Bean
	public JpaPagingItemReader<Pay> jpaItemWriterReader() {
		return new JpaPagingItemReaderBuilder<Pay>()
			.name("jpaItemWriterReader")
			.entityManagerFactory(entityManagerFactory) // JPA
			.pageSize(chunkSize)
			.queryString("SELECT p FROM Pay p") // Jpql
			.build();
	}

	/**
	 * 중간 처리 processor
	 * @return
	 */
	@Bean
	public ItemProcessor<Pay, Pay2> jpaItemProcessor() {
		return pay -> new Pay2(pay.getAmount(), pay.getTxName(), pay.getTxDateTime());
	}

	@Bean
	public JpaItemWriter<Pay2> jpaItemWriter() {
		JpaItemWriter<Pay2> jpaItemWriter = new JpaItemWriter<>();
		jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
		return jpaItemWriter;
	}

	/**
	 * JdbcBatchItemWriter에 비해 필수값이 Entity Manager 뿐이라 체크할 요소가 적다는 것
	 * rowMapper 삭제
	 */

}
