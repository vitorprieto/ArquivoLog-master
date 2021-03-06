package br.com.robertWalters.ArquivoLog.spring.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import br.com.robertWalters.ArquivoLog.model.LogModel;
import br.com.robertWalters.ArquivoLog.spring.batch.LogFieldSetMapper;

/**
 * @author Vitor Prieto
 * @date 06/03/2021.
 */

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
			ItemReader<LogModel> itemReader, ItemProcessor<LogModel, LogModel> itemProcessor,
			ItemWriter<LogModel> itemWriter) {

		Step step = stepBuilderFactory.get("ETL-file-load").<LogModel, LogModel>chunk(100).reader(itemReader)
				.processor(itemProcessor).writer(itemWriter).build();

		return jobBuilderFactory.get("ETL-Load").incrementer(new RunIdIncrementer()).start(step).build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<LogModel> itemReader(@Value("#{jobParameters[filePath]}") String filePath) {

		FlatFileItemReader<LogModel> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(new FileSystemResource(filePath));
		flatFileItemReader.setName("CSV-Reader");
		flatFileItemReader.setLinesToSkip(0);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
	}

	@Bean
	public LineMapper<LogModel> lineMapper() {

		DefaultLineMapper<LogModel> defaultLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

		lineTokenizer.setDelimiter("|");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames(new String[] { "createdAt", "ip", "request", "status", "userAgent" });

		LogFieldSetMapper fieldSetMapper = new LogFieldSetMapper();

		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);

		return defaultLineMapper;
	}

}
