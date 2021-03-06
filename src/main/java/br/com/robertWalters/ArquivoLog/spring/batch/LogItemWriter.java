package br.com.robertWalters.ArquivoLog.spring.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.robertWalters.ArquivoLog.model.LogModel;
import br.com.robertWalters.ArquivoLog.service.LogService;

/**
 * @author Vitor Prieto
 * @date 06/03/2021.
 */

@Component
public class LogItemWriter implements ItemWriter<LogModel> {

    @Autowired
    private LogService logService;

    @Override
    public void write(List<? extends LogModel> logs) throws Exception {
        System.out.println("Data Saved for logs: " + logs);
        logService.saveAll(logs);
    }
}
