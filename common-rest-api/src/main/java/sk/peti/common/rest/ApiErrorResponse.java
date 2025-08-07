package sk.peti.common.rest;

import io.soabase.recordbuilder.core.RecordBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

@RecordBuilder
@Schema(name = "ApiErrorResponse", description = "Generic error response")
public record ApiErrorResponse(
        @NotNull String message,
        @NotNull Instant timestamp,
        @NotNull Integer status,
        @Nullable String path,
        @Nullable List<String> details) {
}
