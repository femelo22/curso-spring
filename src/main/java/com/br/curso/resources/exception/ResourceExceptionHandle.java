package com.br.curso.resources.exception;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.br.curso.services.exception.DataIntegrityException;
import com.br.curso.services.exception.ObjectNotFoundException;


@ControllerAdvice
public class ResourceExceptionHandle {

	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<StandardError> elementNotFound(ObjectNotFoundException e, HttpServletRequest request){
		
		StandardError error = new StandardError();
		error.setTimeStamp(Instant.now());
		error.setStatus(HttpStatus.NOT_FOUND.value());
		error.setMensagem(e.getMessage());
		error.setPath(request.getRequestURI());
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);		
	}
	
	@ExceptionHandler(DataIntegrityException.class)
	public ResponseEntity<StandardError> integrityViolation(DataIntegrityException e, HttpServletRequest request){
		
		StandardError error = new StandardError();
		error.setTimeStamp(Instant.now());
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setMensagem(e.getMessage());
		error.setPath(request.getRequestURI());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);		
	}
	
}
