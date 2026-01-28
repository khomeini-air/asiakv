package com.asia.asiakv.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@EqualsAndHashCode()
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class PaginationDTO {
    private Integer currentPage;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
}
