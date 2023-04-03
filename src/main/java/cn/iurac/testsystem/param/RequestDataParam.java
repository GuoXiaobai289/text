package cn.iurac.testsystem.param;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class RequestDataParam<T> implements Serializable {

    private String msg;

    private Integer code;

    @Valid
    @NotNull(
            message = "data required"
    )
    private T data;
}