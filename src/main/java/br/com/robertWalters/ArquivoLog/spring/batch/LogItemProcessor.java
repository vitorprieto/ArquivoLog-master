package br.com.robertWalters.ArquivoLog.spring.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sun.istack.logging.Logger;

import br.com.robertWalters.ArquivoLog.model.LogModel;

/**
 * @author Vitor Prieto
 * @date 06/03/2021.
 */

@Component
public class LogItemProcessor implements ItemProcessor<LogModel, LogModel> {

	private static Logger logger = Logger.getLogger(LogItemProcessor.class);

	@Override
	public LogModel process(LogModel log) throws Exception {
		LogModel logConverted = new LogModel(log.getCreatedAt(), log.getIp(), log.getRequest(), log.getStatus(),
				log.getUserAgent());
		logger.info("Log converting (" + log + ") into (" + logConverted + ")");
		return logConverted;
	}
}
