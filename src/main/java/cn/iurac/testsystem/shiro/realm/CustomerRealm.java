package cn.iurac.testsystem.shiro.realm;

import cn.hutool.core.util.ObjectUtil;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import cn.iurac.testsystem.enums.RoleEnum;
import cn.iurac.testsystem.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

@Component
@Slf4j
public class CustomerRealm extends AuthorizingRealm {

    @Resource
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //获取身份信息
        User primaryPrincipal = (User) principals.getPrimaryPrincipal();
        //根据主身份信息获取角色 和 权限信息
        RoleEnum roleEnum = RoleEnum.getByCode(primaryPrincipal.getRole());
        //角色信息
        if(ObjectUtil.isNotNull(roleEnum)){
            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
            simpleAuthorizationInfo.addRole(roleEnum.getRole()); //添加角色信息
            return simpleAuthorizationInfo;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        //根据身份信息//从传过来的token获取到的用户名
        String principal = (String) token.getPrincipal();

        //根据身份信息查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",principal);
        User user = userService.getOne(queryWrapper);

        //用户不为空
        if(ObjectUtil.isNotNull(user)){
            if(ObjectUtil.equal(user.getLockFlag(), FieldFlagEnum.ILLEGAL.getCode())){
                throw new AuthenticationException("账户已被锁定，请联系客服");
            }
            System.out.println(user.getPassword());
            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(user, user.getPassword(),
                    new SimpleByteSource(user.getSalt()), this.getName());
            return simpleAuthenticationInfo;
        }

        return null;
    }

    @Override
    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
        return ((User) principals.getPrimaryPrincipal()).getUsername();
    }

    @Override
    protected Object getAuthenticationCacheKey(PrincipalCollection principals) {
        return ((User) principals.getPrimaryPrincipal()).getUsername();
    }
}
