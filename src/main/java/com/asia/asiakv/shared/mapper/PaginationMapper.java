package com.asia.asiakv.shared.mapper;

import com.asia.asiakv.shared.dto.PaginationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

/**
 * MapStruct mapper responsible for converting Spring Data {@link Page} objects
 * into {@link PaginationDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaginationMapper {

    @Mapping(target="currentPage", source = "number")
    @Mapping(target="pageSize", source = "size")
    PaginationDTO toDTO(Page page);
}
