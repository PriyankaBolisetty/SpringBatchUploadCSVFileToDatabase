package springbatch_example;

import org.springframework.batch.item.ItemProcessor;

import springbatch_example.model.APSUploadFile;

public class APSUploadFileItemProcessor implements ItemProcessor<APSUploadFile, APSUploadFile>{
	
	@Override
	public APSUploadFile process(APSUploadFile apsUploadFile) throws Exception{
		return apsUploadFile;
	}
	
}
