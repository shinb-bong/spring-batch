package com.example.springbatch.job.writer;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.example.springbatch.entity.Pay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JdbcBatchItemWriterJobConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dataSource; // DataSource DI

	private static final int chunkSize = 10;

	@Bean
	public Job jdbcBatchItemWriterJob() {
		return jobBuilderFactory.get("jdbcBatchItemWriterJob")
			.start(jdbcBatchItemWriterStep())
			.build();
	}

	@Bean
	public Step jdbcBatchItemWriterStep() {
		return stepBuilderFactory.get("jdbcBatchItemWriterStep")
			.<Pay, Pay>chunk(chunkSize)
			.reader(jdbcBatchItemWriterReader())
			.writer(jdbcBatchItemWriter())
			.build();
	}

	@Bean
	public JdbcCursorItemReader<Pay> jdbcBatchItemWriterReader() {
		return new JdbcCursorItemReaderBuilder<Pay>()
			.fetchSize(chunkSize)
			.dataSource(dataSource)
			.rowMapper(new BeanPropertyRowMapper<>(Pay.class))
			.sql("SELECT id, amount, tx_name, tx_date_time FROM pay")
			.name("jdbcBatchItemWriter")
			.build();
	}

	/**
	 * reader에서 넘어온 데이터를 하나씩 출력하는 writer
	 */
	@Bean // beanMapped()을 사용할때는 필수
	public JdbcBatchItemWriter<Pay> jdbcBatchItemWriter() {
		/**
		 * new JdbcBatchItemWriterBuilder<Map<String, Object>>() // Map 사용
		 * 				.columnMapped()
		 * 				.dataSource(this.dataSource)
		 * 				.sql("insert into pay2(amount, tx_name, tx_date_time) values (:amount, :txName, :txDateTime)")
		 * 				.build();
		 */

		return new JdbcBatchItemWriterBuilder<Pay>()
			.dataSource(dataSource)
			.sql("insert into pay2(amount, tx_name, tx_date_time) values (:amount, :txName, :txDateTime)")
			.beanMapped() // bean mapping
			.build();
	}



	/**
	 * Pay2 테이블에 데이터를 넣은 Writer이지만 선언된 제네릭 타입은 Reader/Processor에서 넘겨준 Pay클래스입니다.
	 */

	/**
	 * create table pay2 (
	 *   id         bigint not null auto_increment,
	 *   amount     bigint,
	 *   tx_name     varchar(255),
	 *   tx_date_time datetime,
	 *   primary key (id)
	 * ) engine = InnoDB;
	 */
}