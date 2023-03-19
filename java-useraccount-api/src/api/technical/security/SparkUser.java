package com.yrnet.spark.digitalaccount.api.technical.security;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;


@Setter
@Getter
public class SparkUser extends User {

    List<SparkUserRight> sparkUserRights;


    public SparkUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
}
