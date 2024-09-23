package com.metaverse.user.service;

import com.metaverse.user.req.AddFriendReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@RequiredArgsConstructor
@Slf4j
@Service
public class FriendRequestService {
    public String addFriend(@Valid AddFriendReq req) {
        return null;
    }
}
