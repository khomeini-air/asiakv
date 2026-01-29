package com.asia.asiakv.shared.mapper;

import com.asia.asiakv.shared.dto.PaginationDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaginationMapperTest {

    private final PaginationMapper mapper = Mappers.getMapper(PaginationMapper.class);

    @Test
    void toDTO_shouldMapAllFieldsCorrectly() {
        // given
        PageRequest pageable = PageRequest.of(2, 3);
        Page<String> page = new PageImpl<>(
                List.of("a", "b", "c"),
                pageable,
                10
        );

        // when
        PaginationDTO dto = mapper.toDTO(page);

        // then
        assertThat(dto.getCurrentPage()).isEqualTo(2);
        assertThat(dto.getPageSize()).isEqualTo(3);
        assertThat(dto.getTotalElements()).isEqualTo(10);
        assertThat(dto.getTotalPages()).isEqualTo(4);
    }
}