package com.metaverse.user.service;

import cn.hutool.core.util.StrUtil;
import com.metaverse.user.db.entity.MetaverseGroupQuestionDO;
import com.metaverse.user.db.service.IMetaverseGroupQuestionService;
import com.metaverse.user.req.AnswerGroupQuestionReq;
import com.metaverse.user.req.GroupReq;
import com.metaverse.user.resp.UserGroupQuestionResp;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupQuestionService {
    @Setter
    private GroupJoinRequestService groupJoinRequestService;
    private final IMetaverseGroupQuestionService groupQuestionService;

    @Transactional(rollbackFor = Exception.class)
    public Boolean answerGroupQuestion(AnswerGroupQuestionReq req, Long currentUserId) {
        Long groupId = req.getGroupId();
        MetaverseGroupQuestionDO groupQuestionDO = groupQuestionService.lambdaQuery()
                .eq(MetaverseGroupQuestionDO::getGroupId, groupId)
                .one();
        if (groupQuestionDO.getEnabled() && StrUtil.equals(req.getQuestionAnswer(), groupQuestionDO.getAnswer())) {
            return groupJoinRequestService.agreeGroupRequest(Objects.isNull(groupQuestionDO.getUpdateBy()) ? groupQuestionDO.getCreateBy() : groupQuestionDO.getUpdateBy(), new GroupReq(groupId, currentUserId));
        }
        return false;
    }

    UserGroupQuestionResp findGroupQuestion(Long groupId) {
        return convertToQuestionResp(groupQuestionService.lambdaQuery()
                .eq(MetaverseGroupQuestionDO::getGroupId, groupId)
                .eq(MetaverseGroupQuestionDO::getEnabled, Boolean.TRUE)
                .one());
    }

    private UserGroupQuestionResp convertToQuestionResp(MetaverseGroupQuestionDO questionDO) {
        if (questionDO == null) {
            return null;
        }
        return new UserGroupQuestionResp()
                .setGroupId(questionDO.getGroupId())
                .setQuestion(questionDO.getQuestion());
    }

}
