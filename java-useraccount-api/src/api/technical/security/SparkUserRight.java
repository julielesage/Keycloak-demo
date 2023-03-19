package com.yrnet.spark.digitalaccount.api.technical.security;


import com.yrnet.spark.digitalaccount.api.technical.security.role.Role;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SparkUserRight implements Serializable {
    private String scope;
    private String context;
    private Role role;
}
