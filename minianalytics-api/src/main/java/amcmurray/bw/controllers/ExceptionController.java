package amcmurray.bw.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import amcmurray.bw.twitterdomainobjects.Query;

@ControllerAdvice
@RestController
public class ExceptionController {

    @ExceptionHandler(Query.QueryNotFoundException.class)
    public String queryNotFoundHandler(Query.QueryNotFoundException e) {
        return "Query not found with ID: " + e.id;
    }

    @ExceptionHandler(Query.QuerySearchNullException.class)
    public String querySearchNullException() {
        return "Query text must not be empty!";
    }

}
