// package com.example.springbatch.jobv3;
//
// import org.springframework.batch.core.Job;
// import org.springframework.batch.core.Step;
// import org.springframework.batch.core.StepContribution;
// import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
// import org.springframework.batch.core.job.builder.JobBuilder;
// import org.springframework.batch.core.repository.JobRepository;
// import org.springframework.batch.core.scope.context.ChunkContext;
// import org.springframework.batch.core.step.builder.StepBuilder;
// import org.springframework.batch.core.step.tasklet.Tasklet;
// import org.springframework.batch.repeat.RepeatStatus;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.transaction.PlatformTransactionManager;
//
// import lombok.RequiredArgsConstructor;
//
// @Configuration
// @RequiredArgsConstructor
// public class HelloJobConfiguration extends DefaultBatchConfiguration {
//
// 	@Bean
// 	public Job job(JobRepository jobRepository, Step helloStep1, Step helloStep2){
// 		return new JobBuilder("myJob",jobRepository)
// 			.start(helloStep1)
// 			.next(helloStep2)
// 			.build();
// 	}
//
// 	@Bean
// 	public Step helloStep1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
// 		return new StepBuilder("helloStep1",jobRepository)
// 			.tasklet(new Tasklet() {
// 				@Override
// 				public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
// 					System.out.println("====================");
// 					System.out.println(">> Hello Spring Batch!");
// 					System.out.println("====================");
//
// 					return RepeatStatus.FINISHED;
// 				}
// 			},platformTransactionManager)
// 			.build();
// 	}
//
// 	@Bean
// 	public Step helloStep2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
// 		return new StepBuilder("helloStep2", jobRepository).tasklet((contribution, chunkContext) -> {
// 			System.out.println("======================");
// 			System.out.println(" >>step2 was executed");
// 			System.out.println("======================");
//
// 			return RepeatStatus.FINISHED;
// 		}, platformTransactionManager).build();
// 	}
// }
