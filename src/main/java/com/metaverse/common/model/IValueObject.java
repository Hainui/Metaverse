package com.metaverse.common.model;

import java.io.Serializable;

/**
 * 值对象：必须依附聚合根才能存活 - 比如人类身上的器官
 */
public interface IValueObject extends Serializable {

    Long modelVersion();
}
