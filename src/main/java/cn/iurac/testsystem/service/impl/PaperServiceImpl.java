package cn.iurac.testsystem.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.entity.PaperRepo;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.param.request.paper.PaperPageRequestParam;
import cn.iurac.testsystem.param.request.paper.PaperRequestParam;
import cn.iurac.testsystem.service.PaperRepoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.iurac.testsystem.entity.Paper;
import cn.iurac.testsystem.service.PaperService;
import cn.iurac.testsystem.mapper.PaperMapper;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 */
@Service("PaperService")
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper>
implements PaperService{

    @Resource
    private PaperRepoService paperRepoService;

    @Override
    public Page<Paper> listForPage(Page<Paper> page, PaperPageRequestParam param) {
        QueryWrapper<Paper> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(param.getTitle()),"title",param.getTitle())
                .eq(ObjectUtil.isNotNull(param.getCreateUserId()),"create_user_id",param.getCreateUserId())
                .ge(StrUtil.isNotBlank(param.getStartTime()),"create_time",param.getStartTime())
                .le(StrUtil.isNotBlank(param.getEndTime()),"create_time", param.getEndTime());

        return page(page,queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = SecurityException.class)
    public boolean save(PaperRequestParam data) throws ServiceException {
        Integer score = data.getRadioCount()*data.getRadioScore()
                + data.getSelectCount()*data.getSelectScore()
                + data.getJudgeCount()*data.getJudgeScore();
        if(score<=0){
            throw new ServiceException("总分不能为零或者负数");
        }
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Paper paper = Paper.builder()
                .title(data.getTitle())
                .radioCount(data.getRadioCount())
                .radioScore(data.getRadioScore())
                .selectCount(data.getSelectCount())
                .selectScore(data.getSelectScore())
                .judgeCount(data.getJudgeCount())
                .judgeScore(data.getJudgeScore())
                .score(score)
                .createTime(DateUtil.date())
                .updateTime(DateUtil.date())
                .createUserId(user.getId())
                .deleteFlag(FieldFlagEnum.NORMAL.getCode())
                .build();
        if(!save(paper)){
            throw new ServiceException("添加试卷时发生异常");
        }
        List<Long> repoIds = data.getRepoIds();
        for (Long id : repoIds) {
            PaperRepo paperRepo = PaperRepo.builder().repoId(id).paperId(paper.getId()).build();
            if(!paperRepoService.save(paperRepo)){
                throw new ServiceException("试卷绑定题库时发生异常");
            }
        }
        return true;
    }
}




