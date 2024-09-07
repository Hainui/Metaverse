package com.metaverse.common.model;

import java.io.Serializable;

/**
 * 实体：可以离开聚合根存活 - 比如人的背包装备
 */
public interface IEntity extends Serializable {
    Long pkVal();

    Long modelVersion();
}
