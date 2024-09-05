package com.metaverse.common.model;

/**
 * 聚合根：领域对象，具有自己的行为，比如人类是一个聚合根，人类可以更改自己的用户名是这个人类具备的行为
 *
 * @param <T> 对应的贫血模型
 */
public interface IAggregateRoot<T> {
    Long pkVal();

    Long modelVersion();
}
