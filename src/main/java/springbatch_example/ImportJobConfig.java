package springbatch_example;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import springbatch_example.model.APSUploadFile;

@Configuration
public class ImportJobConfig<JobCompletionNotificationListener> {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource dataSource;
	
	private JdbcTemplate jdbcTemplate;
	
	@Bean
	public DataSource dataSource(){
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost/provisioning");
		dataSource.setUsername("root");
		dataSource.setPassword("dbpassword");
		
		return dataSource;
	}
	

    @Bean
    @Scope(value = "step", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public FlatFileItemReader<APSUploadFile> importReader(@Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
        FlatFileItemReader<APSUploadFile> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(pathToFile));
        reader.setLineMapper(new DefaultLineMapper<APSUploadFile>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"GivenName", "LastName", "email", "DID", "Bldg", "location", "OfficialMobile"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<APSUploadFile>() {{
                setTargetType(APSUploadFile.class);
            }});
        }});
        return reader;
    }

    @Bean
    public APSUploadFileItemProcessor processor() {
        return new APSUploadFileItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<APSUploadFile> writer() {
        JdbcBatchItemWriter<APSUploadFile> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(
                new BeanPropertyItemSqlParameterSourceProvider<APSUploadFile>());
        writer.setSql("INSERT INTO 01table(GivenName, LastName, email, DID, Bldg, location, OfficialMobile)"
				+ " VALUES(:givenName, :lastName, :email, :DID, :bldg, :location, :officialMobile)");
        writer.setDataSource(dataSource);
        return writer;
    }
   
    @Bean
    public Job importUserJob(ItemReader<APSUploadFile> importReader) {
    	return jobBuilderFactory.get("importUserJob")
				.incrementer(new RunIdIncrementer())
				.flow(step1(importReader))
				.end()
				.build();
    }
    
    @Bean
    public Step step1(ItemReader<APSUploadFile> importReader) {
        return stepBuilderFactory.get("step1").<APSUploadFile, APSUploadFile>chunk(10).reader(importReader)
                .processor(processor()).writer(writer()).build();
    }

}