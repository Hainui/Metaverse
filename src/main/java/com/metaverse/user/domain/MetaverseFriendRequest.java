package com.metaverse.user.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MetaverseFriendRequest {

    protected static final Long MODEL_VERSION = 1L;
}
