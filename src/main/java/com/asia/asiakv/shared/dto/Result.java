package com.asia.asiakv.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Result {
    SUCCESS("S", "SUCCESS", "Success"),
    PARAM_ILLEGAL("F", "PARAM_ILLEGAL", "Bad Parameter"),
    RESOURCE_NOT_FOUND("F", "RESOURCE_NOT_FOUND", "Resource Not Found"),
    INTERNAL_ERROR("F", "INTERNAL_ERROR", "Internal Error");

    private final String result;
    private final String code;
    private final String description;
}
