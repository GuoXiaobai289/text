package cn.iurac.testsystem.param;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class PageRequestParam<T> extends RequestDataParam<T> implements Serializable {

    private Long page;
    private Long limit;
}