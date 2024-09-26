package com.metaverse.user.service;

import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.user.db.entity.MetaverseUserFriendQuestionDO;
import com.metaverse.user.db.service.IMetaverseUserFriendQuestionService;
import com.metaverse.user.req.UserFriendQuestionReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFriendQuestionService {
    private final IMetaverseUserFriendQuestionService friendQuestionService;
    
    public Boolean createQuestion(Long currentUserId, UserFriendQuestionReq req) {
        return friendQuestionService.save(new MetaverseUserFriendQuestionDO()
                .setCreateBy(currentUserId)
                .setCreatedAt(LocalDateTime.now())
                .setQuestion(req.getQuestion())
                .setUserId(currentUserId)
                .setEnabled(Boolean.TRUE)
                .setCorrectAnswer(req.getCorrectAnswer())
                .setVersion(0L));
    }

    public Boolean modifyQuestion(Long currentUserId, UserFriendQuestionReq req) {
        MetaverseUserFriendQuestionDO questionDO = assertNotExistAndWriteLoad(currentUserId);
        boolean noChange = req.getQuestion().equals(questionDO.getQuestion()) && req.getCorrectAnswer().equals(questionDO.getCorrectAnswer());
        if (noChange) {
            return false;
        }
        return friendQuestionService.lambdaUpdate()
                .eq(MetaverseUserFriendQuestionDO::getUserId, currentUserId)
                .set(MetaverseUserFriendQuestionDO::getQuestion, req.getQuestion())
                .set(MetaverseUserFriendQuestionDO::getCorrectAnswer, req.getCorrectAnswer())
                .set(MetaverseUserFriendQuestionDO::getUpdateBy, currentUserId)
                .set(MetaverseUserFriendQuestionDO::getVersion, questionDO.getVersion() + 1)
                .update();
    }

    @NotNull
    private MetaverseUserFriendQuestionDO assertNotExistAndWriteLoad(Long currentUserId) {
        MetaverseUserFriendQuestionDO questionDO = friendQuestionService.lambdaQuery()
                .eq(MetaverseUserFriendQuestionDO::getUserId, currentUserId)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();
        if (questionDO == null) {
            throw new IllegalArgumentException("当前用户未设置问题！");
        }
        return questionDO;
    }

    public boolean disableQuestion(Long currentUserId) {
        MetaverseUserFriendQuestionDO questionDO = assertNotExistAndWriteLoad(currentUserId);
        boolean noChange = Boolean.FALSE.equals(questionDO.getEnabled());
        if (noChange) {
            return false;
        }
        return friendQuestionService.lambdaUpdate()
                .eq(MetaverseUserFriendQuestionDO::getUserId, currentUserId)
                .set(MetaverseUserFriendQuestionDO::getEnabled, Boolean.FALSE)
                .set(MetaverseUserFriendQuestionDO::getUpdateBy, currentUserId)
                .set(MetaverseUserFriendQuestionDO::getVersion, questionDO.getVersion() + 1)
                .update();
    }

    public boolean enableQuestion(Long currentUserId) {
        MetaverseUserFriendQuestionDO questionDO = assertNotExistAndWriteLoad(currentUserId);
        boolean noChange = Boolean.TRUE.equals(questionDO.getEnabled());
        if (noChange) {
            return false;
        }
        return friendQuestionService.lambdaUpdate()
                .eq(MetaverseUserFriendQuestionDO::getUserId, currentUserId)
                .set(MetaverseUserFriendQuestionDO::getEnabled, Boolean.TRUE)
                .set(MetaverseUserFriendQuestionDO::getUpdateBy, currentUserId)
                .set(MetaverseUserFriendQuestionDO::getVersion, questionDO.getVersion() + 1)
                .update();
    }
}
