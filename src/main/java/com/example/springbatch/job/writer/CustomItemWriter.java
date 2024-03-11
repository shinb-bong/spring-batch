package com.example.springbatch.job.writer;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.springbatch.entity.Pay;
import com.example.springbatch.entity.Pay2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring Batch에서 공식적으로 지원하지 않는 Writer를 사용하고 싶을때 ItemWriter인터페이스를 구현
 * Reader에서 읽어온 데이터를 RestTemplate으로 외부 API로 전달해야할때
 * 임시저장을 하고 비교하기 위해 싱글톤 객체에 값을 넣어야할때
 * 여러 Entity를 동시에 save 해야할때
 *
 */

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CustomItemWriter {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;

	private static final int chunkSize = 10;

	@Bean
	public Job customItemWriterJob() {
		return jobBuilderFactory.get("customItemWriterJob")
			.start(customItemWriterStep())
			.build();
	}

	@Bean
	public Step customItemWriterStep(){
		return stepBuilderFactory.get("customItemWriterStep")
			.<Pay, Pay2>chunk(chunkSize)
			.reader(customItemWriterReader())
			.processor(customItemWriterProcessor())
			.writer(customItemWriter2())
			.build();
	}

	@Bean
	public JpaPagingItemReader<Pay> customItemWriterReader() {
		return new JpaPagingItemReaderBuilder<Pay>()
			.name("customItemWriterReader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(chunkSize)
			.queryString("SELECT p FROM Pay p")
			.build();
	}

	/**
	 * 중간 프로세서
	 */
	@Bean
	public ItemProcessor<Pay, Pay2> customItemWriterProcessor() {
		return pay -> new Pay2(pay.getAmount(), pay.getTxName(), pay.getTxDateTime());
	}

	/**
	 *  ItemWriter인터페이스를 구현
	 */
	@Bean
	public ItemWriter<Pay2> customItemWriter2(){
		// return new ItemWriter<Pay2>() {
		// 	@Override
		// 	public void write(List<? extends Pay2> items) throws Exception {
		// 		for (Pay2 item : items) {
		// 			System.out.println(item);
		// 		}
		// 	}
		// };
		return items -> {
			for (Pay2 item : items) {
				System.out.println(item);
			}
		};
	}

}
