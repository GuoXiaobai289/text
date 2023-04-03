package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.PaperRepo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface PaperRepoService extends IService<PaperRepo> {

    List<PaperRepo> list(PaperRepo paperRepo);
}
