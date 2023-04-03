package cn.iurac.testsystem.param.request.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserRequestParam implements Serializable {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    private Integer role;

    private String name;

    @Pattern(regexp = "(13\\d|14[579]|15[^4\\D]|17[^49\\D]|18\\d)\\d{8}", message = "手机号格式错误")
    private String phone;

    @Email(message = "邮箱格式错误")
    private String email;

}
