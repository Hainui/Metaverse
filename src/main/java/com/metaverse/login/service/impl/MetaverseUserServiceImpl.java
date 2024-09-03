package com.metaverse.login.service.impl;

import com.metaverse.login.service.IMetaverseUserService;
import entity.MetaverseUserDO;
import mapper.MetaverseUserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 元宇宙用户表 服务实现类
 * </p>
 *
 * @author Hainui
 * @since 2024-09-03
 */
@Service
public class MetaverseUserServiceImpl extends ServiceImpl<MetaverseUserMapper, MetaverseUserDO> implements IMetaverseUserService {

}
