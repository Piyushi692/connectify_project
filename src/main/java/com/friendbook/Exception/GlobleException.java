package com.friendbook.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobleException {

	@ExceptionHandler(UserException.class)
	public ResponseEntity<ErrorDetails> UserExceptionHandler(UserException userException, WebRequest req) {
		ErrorDetails error = new ErrorDetails(userException.getMessage(), req.getDescription(false),
				LocalDateTime.now());

		return new ResponseEntity<ErrorDetails>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PostException.class)
	public ResponseEntity<ErrorDetails> PostExceptionHandler(PostException postException, WebRequest req) {
		ErrorDetails error = new ErrorDetails(postException.getMessage(), req.getDescription(false),
				LocalDateTime.now());

		return new ResponseEntity<ErrorDetails>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(CommentException.class)
	public ResponseEntity<ErrorDetails> CommentExceptionHandler(CommentException commentException, WebRequest req) {
		ErrorDetails error = new ErrorDetails(commentException.getMessage(), req.getDescription(false),
				LocalDateTime.now());
		return new ResponseEntity<ErrorDetails>(error, HttpStatus.BAD_REQUEST);
	}
//
//	@ExceptionHandler(StoryException.class)
//	public ResponseEntity<ErrorDetails> StoryExceptionHandler(StoryException ex, WebRequest req) {
//		ErrorDetails error = new ErrorDetails(ex.getMessage(), req.getDescription(false), LocalDateTime.now());
//
//		return new ResponseEntity<ErrorDetails>(error, HttpStatus.BAD_REQUEST);
//	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDetails> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		ErrorDetails errorDetails = new ErrorDetails(ex.getBindingResult().getFieldError().getDefaultMessage(),
				"validation error", LocalDateTime.now());
		return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> OtherExceptionHandler(Exception ex, WebRequest req) {
		ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), req.getDescription(false), LocalDateTime.now());
		return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.BAD_REQUEST);
	}
}
