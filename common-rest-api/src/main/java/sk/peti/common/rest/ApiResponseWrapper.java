package sk.peti.common.rest;

import io.soabase.recordbuilder.core.RecordBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;

@RecordBuilder
@Schema(name = "ApiResponseWrapper", description = "Generic success response")
public record ApiResponseWrapper<T>(@NotNull String message, @NotNull HttpStatus status, T data) {

    public static <T> ApiResponseWrapper<T> ok(T data) {
        return withStatus(HttpStatus.OK, data);
    }

    public static <T> ApiResponseWrapper<T> withStatus(HttpStatus status, T data) {
        return ApiResponseWrapperBuilder.<T>builder().status(status).data(data).build();
    }
}
