package com.example.PetMatch.exception;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.example.PetMatch.model.payload.MensajeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<MensajeResponse> handleNotFound(
            NoHandlerFoundException ex
    ) {

        return new ResponseEntity<>(
                MensajeResponse.builder()
                        .mensaje("El endpoint que intenta buscar no existe")
                        .object(null)
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<MensajeResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {

        return new ResponseEntity<>(
                MensajeResponse.builder()
                        .mensaje("El ID debe ser un número válido")
                        .object(null)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<MensajeResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex
    ) {

        return new ResponseEntity<>(
                MensajeResponse.builder()
                        .mensaje("El método HTTP no está permitido para este endpoint")
                        .object(null)
                        .build(),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }
}