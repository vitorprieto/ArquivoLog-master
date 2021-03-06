package br.com.robertWalters.ArquivoLog.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.robertWalters.ArquivoLog.model.LogModel;

/**
 * @author Vitor Prieto
 * @date 06/03/2021.
 */

public interface LogRepository extends JpaRepository<LogModel, Long> {

	public List<LogModel> findLogModelsByIpIsContaining(String ip);
	
	public List<LogModel> findLogModelsByStatus(Integer status);
	
	public List<LogModel> findLogModelsByCreatedAtBetween(Date from, Date to);

}
