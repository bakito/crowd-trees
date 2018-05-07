package ch.bakito.crowd.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.atlassian.crowd.exception.ObjectNotFoundException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {ObjectNotFoundException.class})
  protected ResponseEntity<Object> handleCrowdObjectNotFound(ObjectNotFoundException ex, WebRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_PLAIN);
    return handleExceptionInternal(ex, ex.getMessage(), headers, HttpStatus.NOT_FOUND, request);
  }
}