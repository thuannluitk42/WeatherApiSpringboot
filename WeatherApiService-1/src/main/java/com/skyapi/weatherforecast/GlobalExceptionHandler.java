package com.skyapi.weatherforecast;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import com.skyapi.weatherforecast.hourly.BadRequestException;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorDTO handleGenericException(HttpServletRequest request, Exception exception) {
		ErrorDTO errorDTO = new ErrorDTO();
		errorDTO.setTimestamp(new Date());
		errorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		errorDTO.addErrors(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		errorDTO.setPath(request.getServletPath());

		LOGGER.error(exception.getMessage(), exception);
		return errorDTO;
	}
	
	@ExceptionHandler({BadRequestException.class, GeolocationException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorDTO handleBadRequestException(HttpServletRequest request, Exception exception) {
		ErrorDTO errorDTO = new ErrorDTO();
		errorDTO.setTimestamp(new Date());
		errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
		errorDTO.addErrors(exception.getMessage());
		errorDTO.setPath(request.getServletPath());

		LOGGER.error(exception.getMessage(), exception);
		return errorDTO;
	}
	
	@ExceptionHandler(LocationNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorDTO handleLocationNotFoundException(HttpServletRequest request, Exception exception) {
		ErrorDTO errorDTO = new ErrorDTO();
		
		errorDTO.setTimestamp(new Date());
		errorDTO.setStatus(HttpStatus.NOT_FOUND.value());
		errorDTO.addErrors(exception.getMessage());
		errorDTO.setPath(request.getServletPath());

		LOGGER.error(exception.getMessage(), exception);
		return errorDTO;
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorDTO ConstraintViolation(HttpServletRequest request, Exception exception) {
		ConstraintViolationException violationException = (ConstraintViolationException) exception;
		
		ErrorDTO errorDTO = new ErrorDTO();
		errorDTO.setTimestamp(new Date());
		errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
		errorDTO.setPath(request.getServletPath());
		
		var constraintViolations = violationException.getConstraintViolations();
		constraintViolations.forEach(constraint -> {
			errorDTO.addErrors(constraint.getPropertyPath() + ": "+constraint.getMessage());
		});

		LOGGER.error(exception.getMessage(), exception);
		return errorDTO;
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		
		LOGGER.error(ex.getMessage(), ex);
		
		ErrorDTO errorDTO = new ErrorDTO();
		errorDTO.setTimestamp(new Date());
		errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
		errorDTO.setPath(((ServletWebRequest) request).getRequest().getServletPath());
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		fieldErrors.forEach(fieldError -> {
			errorDTO.addErrors(fieldError.getDefaultMessage());
		});
		
		return new ResponseEntity<>(errorDTO,headers,status);
	}

}
