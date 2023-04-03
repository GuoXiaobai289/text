package cn.iurac.testsystem.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.iurac.testsystem.entity.PaperRepo;
import cn.iurac.testsystem.service.PaperRepoService;
import cn.iurac.testsystem.mapper.PaperRepoMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class PaperRepoServiceImpl extends ServiceImpl<PaperRepoMapper, PaperRepo>
implements PaperRepoService {

    @Override
    public List<PaperRepo> list(PaperRepo paperRepo) {
        QueryWrapper<PaperRepo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(paperRepo.getRepoId()),"repo_id",paperRepo.getRepoId())
                .eq(ObjectUtil.isNotNull(paperRepo.getPaperId()),"paper_id",paperRepo.getPaperId());
        return list(queryWrapper);
    }
}




