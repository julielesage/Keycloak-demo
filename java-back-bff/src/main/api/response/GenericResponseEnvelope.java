package com.yrnet.spark.sparkbxadmin.api.response;

import com.yrnet.spark.bxadmin.model.ResponseEnvelope;
import java.io.Serializable;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class GenericResponseEnvelope<T extends Serializable> extends ResponseEnvelope implements Serializable {

    private T data;

    public GenericResponseEnvelope(final T data) {
        super();
        this.data = data;
    }

    public void addCallId() {
        setCallId(UUID.randomUUID().toString());
    }

}
