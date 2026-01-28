package com.asia.asiakv.object.api;

import com.asia.asiakv.object.dto.KeyValueDto;
import com.asia.asiakv.object.service.KeyValueService;
import com.asia.asiakv.object.validator.SingleEntryMap;
import com.asia.asiakv.object.validator.ValidJsonValue;
import com.asia.asiakv.shared.dto.*;
import com.asia.asiakv.shared.mapper.PaginationMapper;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/objects")
@RequiredArgsConstructor
@Validated
public class ObjectController {
    private final KeyValueService keyValueService;
    private final PaginationMapper paginationMapper;

    /**
     * Get the lastest version of all key-value with pagination
     *
     * @param request query parameters {@link PaginationRequest}
     * @return List of {@link KeyValueDto} and {@link PaginationDTO} that indicates the current page.
     */
    @GetMapping
    public ResponseEntity<ApiPaginationResponse<List<KeyValueDto>>> getAll(@Valid @ModelAttribute PaginationRequest request) {
        Page<KeyValueDto> allData = keyValueService.findAll(request.toPageable());
        PaginationDTO pageDTO = paginationMapper.toDTO(allData);
        ApiPaginationResponse<List<KeyValueDto>> response = ApiPaginationResponse
                .<List<KeyValueDto>>builder()
                .result(Result.SUCCESS)
                .pagination(pageDTO)
                .data(allData.getContent())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Create new versioned Key-Value or update if already exists
     *
     * @param body Map of Key String and {@link JsonNode}
     * @return new {@link KeyValueDto} with version 1, or updated with the newest version.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<KeyValueDto>> createOrUpdate(@Valid @SingleEntryMap @RequestBody Map<String, @ValidJsonValue JsonNode> body) {
        Map.Entry<String, JsonNode> entry = body.entrySet().iterator().next();
        KeyValueDto keyValue = keyValueService.createOrUpdate(entry.getKey(), entry.getValue());
        ApiResponse<KeyValueDto> response = buildSuccessResponse(keyValue);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Get the latest version of the given Key at specific timestamp.
     * <p>
     * If the timestamp is not present, return the latest version.
     * </p>
     *
     * @param key String
     * @param timestamp the UNIX timestamp in milliseconds
     * @return the latest {@link KeyValueDto}
     */
    @GetMapping("/{key}")
    public ResponseEntity<ApiResponse<KeyValueDto>> getByKey(@PathVariable String key,
                                                             @RequestParam (defaultValue = "0") @Min(0) Long timestamp) {
        ApiResponse<KeyValueDto> response = buildSuccessResponse(keyValueService.findByKeyAndTimestamp(key, timestamp));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Construct the successful {@link ApiResponse} for given {@link KeyValueDto}
     *
     * @param keyValueDto the versioned {@link KeyValueDto}
     * @return successful {@link ApiResponse}
     */
    private ApiResponse<KeyValueDto> buildSuccessResponse(KeyValueDto keyValueDto) {
        return ApiResponse.<KeyValueDto>builder()
                .result(Result.SUCCESS)
                .data(keyValueDto)
                .build();
    }
}
