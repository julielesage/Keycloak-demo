package com.yrnet.spark.sparkbxadmin.service.token;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DataRole {
    private String scope;
    private String context;
    private String role;
}
