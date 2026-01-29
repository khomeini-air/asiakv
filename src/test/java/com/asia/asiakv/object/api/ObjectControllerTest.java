package com.asia.asiakv.object.api;

import com.asia.asiakv.object.dto.KeyValueDto;
import com.asia.asiakv.object.service.KeyValueService;
import com.asia.asiakv.shared.dto.PaginationDTO;
import com.asia.asiakv.shared.dto.Result;
import com.asia.asiakv.shared.exception.ResourceNotFoundException;
import com.asia.asiakv.shared.mapper.PaginationMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ObjectController.class)
class ObjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KeyValueService keyValueService;

    @MockitoBean
    private PaginationMapper paginationMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrUpdate_shouldReturn200_whenSingleEntryMap() throws Exception {
        JsonNode valueNode = objectMapper.readTree("""
                    {
                      "country": "Singapore"
                    }
                """);

        KeyValueDto dto = new KeyValueDto(
                "mykey",
                valueNode,
                1L,
                123L
        );

        when(keyValueService.createOrUpdate(
                eq("mykey"),
                any(JsonNode.class)
        )).thenReturn(dto);

        // when / then
        mockMvc.perform(post("/objects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "mykey": {
                                        "country": "Singapore"
                                      }
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.code").value(Result.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.key").value("mykey"))
                .andExpect(jsonPath("$.data.value.country").value("Singapore"))
                .andExpect(jsonPath("$.data.version").value(1));
    }

    @Test
    void createOrUpdate_shouldReturn400_whenEmptyMap() throws Exception {
        mockMvc.perform(post("/objects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.code").value(Result.PARAM_ILLEGAL.getCode()));
    }

    @Test
    void createOrUpdate_shouldReturn400_whenNullMap() throws Exception {
        mockMvc.perform(post("/objects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.code").value(Result.PARAM_ILLEGAL.getCode()));
    }

    @Test
    void createOrUpdate_shouldReturn400_whenMultipleEntries() throws Exception {
        mockMvc.perform(post("/objects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                { "k1": "v1", "k2": "v2" }
            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.code").value(Result.PARAM_ILLEGAL.getCode()));
    }

    @Test
    void createOrUpdate_shouldReturn400_whenValueNull() throws Exception {
        mockMvc.perform(post("/objects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    { "k1": null }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.code").value(Result.PARAM_ILLEGAL.getCode()));
    }

    @Test
    void createOrUpdate_shouldReturn400_whenValueEmpty() throws Exception {
        mockMvc.perform(post("/objects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    { "k1": {}  }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.code").value(Result.PARAM_ILLEGAL.getCode()));
    }

    @Test
    void createOrUpdate_shouldReturn400_whenValueArray() throws Exception {
        mockMvc.perform(post("/objects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    { "k1": [1, 2]  }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.code").value(Result.PARAM_ILLEGAL.getCode()));
    }

    @Test
    void getByKey_shouldReturnLatest_whenTimestampDefault() throws Exception {
        JsonNode valueNode = objectMapper.readTree("""
                    { "v": "value2" }
                """);

        KeyValueDto dto = new KeyValueDto("mykey", valueNode, 2L, 123L);

        when(keyValueService.findByKeyAndTimestamp("mykey", 0L))
                .thenReturn(dto);

        mockMvc.perform(get("/objects/mykey"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.code").value(Result.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.key").value("mykey"))
                .andExpect(jsonPath("$.data.version").value(2))
                .andExpect(jsonPath("$.data.value.v").value("value2"));
    }

    @Test
    void getByKey_shouldReturnHistoricalValue_whenTimestampProvided() throws Exception {
        long ts = 1_700_000_000L;

        JsonNode valueNode = objectMapper.readTree("""
                    { "v": "value1" }
                """);

        KeyValueDto dto = new KeyValueDto("mykey", valueNode, 1L, ts);

        when(keyValueService.findByKeyAndTimestamp("mykey", ts))
                .thenReturn(dto);

        mockMvc.perform(get("/objects/mykey")
                        .param("timestamp", String.valueOf(ts)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.code").value(Result.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.key").value("mykey"))
                .andExpect(jsonPath("$.data.version").value(1))
                .andExpect(jsonPath("$.data.value.v").value("value1"));
    }

    @Test
    void getByKey_shouldReturn400_whenTimestampNegative() throws Exception {
        mockMvc.perform(get("/objects/mykey")
                        .param("timestamp", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.code").value(Result.PARAM_ILLEGAL.getCode()));
    }

    @Test
    void getByKey_shouldReturn404_whenKeyNotFound() throws Exception {
        when(keyValueService.findByKeyAndTimestamp("missing", 0L))
                .thenThrow(new ResourceNotFoundException("Key missing not found"));

        mockMvc.perform(get("/objects/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result.code").value(Result.RESOURCE_NOT_FOUND.getCode()));
    }

    @Test
    void getAll_shouldReturnPaginatedResponse() throws Exception {
        JsonNode valueNode = objectMapper.readTree("""
                    { "v": "v" }
                """);

        KeyValueDto dto = new KeyValueDto("k", valueNode, 1L, 123L);
        Page<KeyValueDto> page = new PageImpl<>(List.of(dto));

        PaginationDTO paginationDTO = new PaginationDTO(1, 10, 1L, 1);

        when(keyValueService.findAll(any()))
                .thenReturn(page);

        when(paginationMapper.toDTO(page))
                .thenReturn(paginationDTO);

        // sorting default - ascending
        mockMvc.perform(get("/objects")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.code").value(Result.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data[0].key").value("k"))
                .andExpect(jsonPath("$.data[0].value.v").value("v"))
                .andExpect(jsonPath("$.pagination.totalElements").value(1));

        // sorting descending
        mockMvc.perform(get("/objects")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortDirection", "DESCENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.code").value(Result.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data[0].key").value("k"))
                .andExpect(jsonPath("$.data[0].value.v").value("v"))
                .andExpect(jsonPath("$.pagination.totalElements").value(1));
    }

    @Test
    void getAll_shouldReturn400_when() throws Exception {
        mockMvc.perform(get("/objects")
                        .param("size", "-10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.code").value(Result.PARAM_ILLEGAL.getCode()));
    }

    @Test
    void createOrUpdate_shouldReturn500_whenThrowException() throws Exception {
        mockMvc.perform(get("/test/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result.code").value(Result.INTERNAL_ERROR.getCode()));
    }
}
