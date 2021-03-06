package br.com.robertWalters.ArquivoLog.controller;

import java.util.Date;
import java.util.List;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.robertWalters.ArquivoLog.model.LogModel;
import br.com.robertWalters.ArquivoLog.service.LogService;
import br.com.robertWalters.ArquivoLog.utils.FilesIO;

/**
 * @author Vitor Prieto
 * @date 06/03/2021.
 */

@RestController
@RequestMapping("/logs")
public class LogController {

	@Autowired
	private LogService logService;

	@Value("${files.csv.path}")
	private String csvFilesPath;

	@PostMapping("/create-by-batch")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> saveLogsFromFile(@RequestParam MultipartFile log) {

		Long now = System.currentTimeMillis();
		String logName = "log_" + now + ".csv";
		String logFullPath = csvFilesPath + logName;

		try {
			// Save File
			FilesIO.salvar(csvFilesPath, logName, log);

			// Run Job
			BatchStatus status = logService.runBatch(now, logFullPath);
			
			if (status == BatchStatus.COMPLETED)
				return new ResponseEntity<>(status.toString(), HttpStatus.CREATED);
			else if (status == BatchStatus.FAILED)
				return new ResponseEntity<>(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, status.toString()),
						HttpStatus.INTERNAL_SERVER_ERROR);
			else
				return new ResponseEntity<>(new ErrorPage(HttpStatus.BAD_REQUEST, status.toString()),
						HttpStatus.BAD_REQUEST);
			
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException | RuntimeException e) {
			return new ResponseEntity<>(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/find-by-id/{id}")
	public ResponseEntity<Object> findById(@PathVariable("id") Long id) {
		try {
			LogModel logModel = logService.findById(id);
			if (logModel == null) {
				return new ResponseEntity<>(new ErrorPage(HttpStatus.NOT_FOUND, "Failed to find Log!"),
						HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<>(logModel, HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/find-all")
	public ResponseEntity<Object> findAll() {
		try {
			List<LogModel> logs = logService.findAll();
			if (logs == null || logs.size() == 0) {
				return new ResponseEntity<>(new ErrorPage(HttpStatus.NOT_FOUND, "Failed to find Logs!"),
						HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<>(logs, HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/find-by-ip/{ip}")
	public ResponseEntity<Object> findByIp(@PathVariable("ip") String ip) {
		try {
			List<LogModel> logs = logService.findByIp(ip);
			if (logs == null || logs.size() == 0) {
				return new ResponseEntity<>(new ErrorPage(HttpStatus.NOT_FOUND, "Failed to find Logs!"),
						HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<>(logs, HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/find-by-status/{statusLog}")
	public ResponseEntity<Object> findByStatus(@PathVariable("statusLog") Integer statusLog) {
		try {
			List<LogModel> logs = logService.findByStatus(statusLog);
			if (logs == null || logs.size() == 0) {
				return new ResponseEntity<>(new ErrorPage(HttpStatus.NOT_FOUND, "Failed to find Logs!"),
						HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<>(logs, HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/find-by-createdat-between/{from}/{to}")
	public ResponseEntity<Object> findByCreatedAtBetween(@PathVariable("from") String from,
			@PathVariable("to") String to) {
		try {
			List<LogModel> logs = logService.findByCreatedAtBetween(from, to);
			if (logs == null || logs.size() == 0) {
				return new ResponseEntity<>(new ErrorPage(HttpStatus.NOT_FOUND, "Failed to find Logs!"),
						HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<>(logs, HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> save(@RequestBody LogModel logModel) {
		try {
			logModel.setCreatedAt(new Date());
			logService.saveOrUpdate(logModel);
			return new ResponseEntity<>(logModel, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/update")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> update(@RequestBody LogModel logModel) {
		try {
			if (!logService.existsById(logModel.getId())) {
				return new ResponseEntity<>(new ErrorPage(HttpStatus.NOT_FOUND, "Failed to find Log!"),
						HttpStatus.NOT_FOUND);
			} else {
				logService.saveOrUpdate(logModel);
				return new ResponseEntity<>(logModel, HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> delete(@PathVariable("id") String id) {
		try {
			if (!logService.existsById(Long.parseLong(id))) {
				return new ResponseEntity<>(new ErrorPage(HttpStatus.NOT_FOUND, "Failed to delete, log not found!"),
						HttpStatus.NOT_FOUND);
			} else {
				logService.delete(Long.parseLong(id));
				return new ResponseEntity<>("Successful to delet log!", HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
