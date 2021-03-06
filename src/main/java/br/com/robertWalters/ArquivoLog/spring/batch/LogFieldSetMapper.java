package br.com.robertWalters.ArquivoLog.spring.batch;

import java.util.Date;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import br.com.robertWalters.ArquivoLog.model.LogModel;
import br.com.robertWalters.ArquivoLog.utils.Formatting;

/**
 * @author Vitor Prieto
 * @date 06/03/2021.
 */

public class LogFieldSetMapper implements FieldSetMapper<LogModel> {

	@Override
	public LogModel mapFieldSet(FieldSet fieldSet) {
		LogModel logItem = null;
		try {
			Date createdAt = Formatting.stringToDate_yyyy_MM_dd__HH_mm_ss(fieldSet.readString(0));
			String ip = fieldSet.readString(1);
			String request = fieldSet.readString(2);
			Integer status = fieldSet.readInt(3);
			String userAgent = fieldSet.readString(4);
			logItem = new LogModel(createdAt, ip, request, status, userAgent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logItem;
	}

}
