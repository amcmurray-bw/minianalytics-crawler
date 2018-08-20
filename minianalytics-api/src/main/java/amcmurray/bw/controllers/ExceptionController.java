package amcmurray.bw.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import amcmurray.bw.exceptions.QueryExceptions;

@ControllerAdvice
@RestController
public class ExceptionController {

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Query not found.")  // 404 error
    @ExceptionHandler(QueryExceptions.QueryNotFoundException.class)
    public String queryNotFoundHandler(QueryExceptions.QueryNotFoundException e) {
        return "Query not found with ID: " + e.id;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Query text must not be empty!")  // 400 error
    @ExceptionHandler(QueryExceptions.QuerySearchNullException.class)
    public String querySearchNullException() {
        return "Query text must not be empty!";
    }

}
