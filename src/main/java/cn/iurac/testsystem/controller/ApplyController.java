package cn.iurac.testsystem.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.constant.ImgDefaultName;
import cn.iurac.testsystem.entity.Apply;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.entity.UserClazz;
import cn.iurac.testsystem.entity.excelentity.ExcelData;
import cn.iurac.testsystem.enums.RoleEnum;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.exception.UploadException;
import cn.iurac.testsystem.log.OperationLog;
import cn.iurac.testsystem.mapper.ApplyMapper;
import cn.iurac.testsystem.mapper.UserClazzMapper;
import cn.iurac.testsystem.param.PageRequestParam;
import cn.iurac.testsystem.param.RequestDataParam;
import cn.iurac.testsystem.param.ResponseParam;
import cn.iurac.testsystem.param.request.apply.ApplyPageRequestParam;
import cn.iurac.testsystem.param.request.apply.ApplyRequestParam;
import cn.iurac.testsystem.param.request.user.UserRequestParam;
import cn.iurac.testsystem.param.response.ApplyListResponseParam;
import cn.iurac.testsystem.param.response.page.ApplyPageResponseParam;
import cn.iurac.testsystem.service.ApplyService;
import cn.iurac.testsystem.service.ClazzService;
import cn.iurac.testsystem.service.UserService;
import cn.iurac.testsystem.util.EmployeesListener;
import cn.iurac.testsystem.util.ResponseUtil;
import cn.iurac.testsystem.util.SaltUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@RestController
@Slf4j
@Api("申请controller")
@RequestMapping("/apply")
public class ApplyController {

    @Resource
    private ApplyService applyService;
    @Resource
    private UserService userService;
    @Resource
    private ClazzService clazzService;
    @Resource
    private ApplyMapper applyMapper;
    @Resource
    private UserClazzMapper userClazzMapper;

    @RequiresRoles(value = {"student","teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/listForPage")
    @ApiOperation("申请管理：查询申请")
    @OperationLog("申请管理：查询申请")
    public ResponseParam listForPage(@RequestBody PageRequestParam<ApplyPageRequestParam> req) {
        ApplyPageRequestParam data = req.getData();

        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            data.setTeacherId(user.getId());
        }
        if(ObjectUtil.isNull(user) || RoleEnum.STUDENT.getCode().equals(user.getRole())){
            data.setStudentId(user.getId());
        }

        Page<ApplyPageResponseParam> page = new Page<>(req.getPage(),req.getLimit());
        page = applyService.listForPage(page, data);
        return new ResponseParam(page);
    }

    @RequiresRoles(value = {"student","superadmin"},logical = Logical.OR)
    @GetMapping("/listApply/{page}")
    @ApiOperation("申请管理：查询申请名单")
    @OperationLog("申请管理：查询申请名单")
    public ResponseParam listApply(@PathVariable Integer page, @RequestParam(value = "name", required = false) String name) {
        Page<ApplyListResponseParam> p = new Page<>(page,8);
        p = applyService.listApply(p, name);
        return new ResponseParam(p);
    }

    @RequiresRoles(value = {"student","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/save")
    @ApiOperation("申请管理：添加申请")
    @OperationLog("申请管理：添加申请")
    public ResponseParam save(@Validated @RequestBody RequestDataParam<ApplyRequestParam> req) throws ServiceException {
        ApplyRequestParam data = req.getData();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        ResponseParam responseParam = new ResponseParam();
        data.setStudentId(ObjectUtil.defaultIfNull(data.getStudentId(),user.getId()));
        boolean status = applyService.save(data);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/update/{id}")
    @ApiOperation("申请管理：更新申请")
    @OperationLog("申请管理：更新申请")
    public ResponseParam update(@PathVariable("id") Long id, @Validated @RequestBody RequestDataParam<ApplyRequestParam> req) throws ServiceException {
        ApplyRequestParam data = req.getData();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            if(!ObjectUtil.equal(data.getTeacherId(), user.getId())){
                return ResponseUtil.getErrorResponseParam("您没有权限！");
            }
        }
        ResponseParam responseParam = new ResponseParam();
        boolean status = applyService.update(id, data);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"admin","superadmin"},logical = Logical.OR)
    @PostMapping("/delete/{id}")
    @ApiOperation("申请管理：删除申请")
    @OperationLog("申请管理：删除申请")
    public ResponseParam delete(@PathVariable("id") Long id) {
        boolean status = applyService.removeById(id);
        ResponseParam responseParam = new ResponseParam();
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequestMapping("/add")
    public ResponseParam addlist( MultipartFile file,String id ){
        ArrayList<ExcelData> excelData = new ArrayList<>();
        try {
            EasyExcel.read(file.getInputStream(), ExcelData.class, new AnalysisEventListener<ExcelData>() {
                /**
                 * 这个每一条数据解析都会来调用
                 */
                @Override
                public void invoke(ExcelData o, AnalysisContext analysisContext) {
                    log.info(o.getName());
                    excelData.add(o);
                    UserRequestParam user = new UserRequestParam();
                    user.setUsername(o.getPhone());
                    user.setRole(1);
                    user.setPassword(o.getPhone());
                    user.setPhone(o.getPhone());
                    user.setName(o.getName());
                    RequestDataParam<UserRequestParam> param = new RequestDataParam<>();
                    param.setData(user);
                    try {
                        ResponseParam usersave = usersave(param);
                        User userdata = (User) usersave.getData();

                        //用户班级
                        UserClazz userClazz = new UserClazz();
                        userClazz.setUserId(userdata.getId());
                        userClazz.setClazzId(13L);
                        userClazzMapper.insert(userClazz);

                        //用户班级老师
                        Apply apply = new Apply();
                        apply.setStudentId(userdata.getId());
                        apply.setTeacherId(Long.valueOf(id));
                        apply.setClazzId(13L);
                        apply.setApplyTime(new Date());
                        apply.setType(1);
                        apply.setFinishTime(new Date());
                        apply.setDeleteFlag(0);
                        applyMapper.insert(apply);
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    } catch (UploadException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                /**
                 * 所有数据解析完成了 都会来调用
                 *
                 */
                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                }
            }).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseParam();
    }

    public ResponseParam usersave(RequestDataParam<UserRequestParam> req) throws ServiceException, UploadException, IOException {
        UserRequestParam data = req.getData();
        if(StrUtil.isBlank(data.getPassword())){
            throw new ServiceException("密码不能为空！");
        }
        if(userService.existUserByUsername(data.getUsername())){
            throw new ServiceException("当前用户已经存在！");
        }

        User user = User.builder().deleteFlag( 0).lockFlag( 0).createTime(DateUtil.date()).updateTime(DateUtil.date()).build();
        BeanUtil.copyProperties(data, user);
        String salt = SaltUtil.getSalt(6);
        Md5Hash md5Hash = new Md5Hash(data.getPassword(), salt, 1024);
        user.setPassword(md5Hash.toHex());
        user.setSalt(salt);
        user.setName(ObjectUtil.defaultIfBlank(user.getName(), StrUtil.concat(true,"用户", RandomUtil.randomString(8))));

        String imgName = "";
        RoleEnum roleEnum = RoleEnum.getByCode(data.getRole());
        if(ObjectUtil.isNull(roleEnum)){
            log.error("找不到该用户注册的角色");
            throw new ServiceException("系统错误");
        }else {
            if(ObjectUtil.equal(roleEnum,RoleEnum.SUPERADMIN)){
                imgName = ImgDefaultName.SUPERADMIN_ICON;
            }else if(ObjectUtil.equal(roleEnum,RoleEnum.ADMIN)){
                imgName = ImgDefaultName.ADMIN_ICON;
            }else if(ObjectUtil.equal(roleEnum,RoleEnum.TEACHER)){
                imgName = ImgDefaultName.TEACHER_ICON;
            }else{
                imgName = ImgDefaultName.STUDENT_ICON;
            }
        }
        user.setImg(imgName);

        ResponseParam responseParam = new ResponseParam();
        boolean status = userService.save(user);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误！");
        }
        responseParam.setData(user);
        return responseParam;
    }
}
