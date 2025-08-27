package com.example.backend.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignIpAddressRequest {
    private Integer studentId;
    private List<Long> ipAddressIds;
}
