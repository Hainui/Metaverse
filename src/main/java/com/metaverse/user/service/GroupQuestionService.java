package com.metaverse.user.service;

import cn.hutool.core.util.StrUtil;
import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.user.db.entity.MetaverseGroupQuestionDO;
import com.metaverse.user.db.service.IMetaverseGroupQuestionService;
import com.metaverse.user.req.AnswerGroupQuestionReq;
import com.metaverse.user.req.GroupQuestionReq;
import com.metaverse.user.req.GroupReq;
import com.metaverse.user.resp.UserGroupQuestionResp;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.LocalDateTime;
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

    @Transactional(rollbackFor = Exception.class)
    public Boolean createGroupQuestion(Long currentUserId, @Valid GroupQuestionReq req) {
        return groupQuestionService.save(new MetaverseGroupQuestionDO()
                .setGroupId(req.getGroupId())
                .setQuestion(req.getQuestion())
                .setAnswer(req.getQuestionAnswer())
                .setSavedAt(LocalDateTime.now())
                .setUpdateBy(currentUserId)
                .setVersion(0L));

    }


    @Transactional(rollbackFor = Exception.class)
    public boolean modifyGroupQuestion(Long currentUserId, @Valid GroupQuestionReq req) {
        MetaverseGroupQuestionDO groupQuestionDO = getGroupQuestionForUpdate(req.getGroupId());
        if (!hasQuestionChanged(groupQuestionDO, req)) {
            return false;
        }
        groupQuestionDO.setQuestion(req.getQuestion())
                .setAnswer(req.getQuestionAnswer())
                .setUpdateBy(currentUserId)
                .setVersion(groupQuestionDO.getVersion() + 1);
        return groupQuestionService.updateById(groupQuestionDO);
    }

    private boolean hasQuestionChanged(MetaverseGroupQuestionDO groupQuestionDO, GroupQuestionReq req) {
        return !req.getQuestion().equals(groupQuestionDO.getQuestion()) || !req.getQuestionAnswer().equals(groupQuestionDO.getAnswer());
    }

    @NotNull
    private MetaverseGroupQuestionDO getGroupQuestionForUpdate(Long groupId) {
        MetaverseGroupQuestionDO groupQuestionDO = groupQuestionService.lambdaQuery()
                .eq(MetaverseGroupQuestionDO::getGroupId, groupId)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();
        if (groupQuestionDO == null) {
            throw new IllegalArgumentException("当前群组未设置问题！");
        }
        return groupQuestionDO;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean disableGroupQuestion(Long currentUserId, Long groupId) {
        MetaverseGroupQuestionDO groupQuestionDO = getGroupQuestionForUpdate(groupId);
        boolean noChange = Boolean.FALSE.equals(groupQuestionDO.getEnabled());
        if (noChange) {
            return false;
        }
        return groupQuestionService.lambdaUpdate()
                .eq(MetaverseGroupQuestionDO::getGroupId, groupId)
                .set(MetaverseGroupQuestionDO::getEnabled, Boolean.FALSE)
                .set(MetaverseGroupQuestionDO::getUpdateBy, currentUserId)
                .set(MetaverseGroupQuestionDO::getVersion, groupQuestionDO.getVersion() + 1)
                .update();

    }

    @Transactional(rollbackFor = Exception.class)
    public boolean enableGroupQuestion(Long currentUserId, Long groupId) {
        MetaverseGroupQuestionDO groupQuestionDO = getGroupQuestionForUpdate(groupId);
        boolean noChange = Boolean.TRUE.equals(groupQuestionDO.getEnabled());
        if (noChange) {
            return false;
        }
        return groupQuestionService.lambdaUpdate()
                .eq(MetaverseGroupQuestionDO::getGroupId, groupId)
                .set(MetaverseGroupQuestionDO::getEnabled, Boolean.TRUE)
                .set(MetaverseGroupQuestionDO::getUpdateBy, currentUserId)
                .set(MetaverseGroupQuestionDO::getVersion, groupQuestionDO.getVersion() + 1)
                .update();

    }
}
