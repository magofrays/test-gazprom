package by.magofrays.exception

import org.springframework.http.HttpStatus

class BusinessException(
    val status: HttpStatus,
    message: String
) : RuntimeException(message)