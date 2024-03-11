package com.example.springbatch.job.processor;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.springbatch.entity.Teacher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 여러개의 ItemProcessor를 사용하려면..?
 * CompositeItemProcessor
 */

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProcessorCompositeJobConfiguration {
	public static final String JOB_NAME = "ProcessorCompositeBatch";
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
			.start(step3())
			.build();
	}

	@Bean(BEAN_PREFIX + "step")
	@JobScope
	public Step step3() {
		return stepBuilderFactory.get(BEAN_PREFIX + "step")
			.<Teacher, String>chunk(chunkSize)
			.reader(reader3())
			.processor(compositeProcessor())
			.writer(writer3())
			.build();
	}

	@Bean
	public JpaPagingItemReader<Teacher> reader3() {
		return new JpaPagingItemReaderBuilder<Teacher>()
			.name(BEAN_PREFIX+"reader")
			.entityManagerFactory(emf)
			.pageSize(chunkSize)
			.queryString("SELECT t FROM Teacher t")
			.build();
	}

	/**
	 * 이부분으로 순서를 정해서
	 * Processor를 여러개 순차적으로 진행시킨다.
	 * 이때 첫 processor는 <Teacher, String>
	 * 다음 processor는 <String,String> 이므로
	 * 제너릭은 명시를 해주지 않았지만 할수 있으면 안전성을 위해 해주는게 좋음
	 */
	@Bean
	public CompositeItemProcessor compositeProcessor() {
		List<ItemProcessor> delegates = new ArrayList<>(2);
		delegates.add(processor1());
		delegates.add(processor2());

		CompositeItemProcessor processor = new CompositeItemProcessor<>();

		processor.setDelegates(delegates);

		return processor;
	}

	public ItemProcessor<Teacher, String> processor1() {
		return Teacher::getName;
	}

	public ItemProcessor<String, String> processor2() {
		return name -> "안녕하세요. "+ name + "입니다.";
	}

	private ItemWriter<? super String>  writer3() {
		return items -> {
			for (String item : items) {
				log.info("Teacher Name={}", item);
			}
		};
	}
}
