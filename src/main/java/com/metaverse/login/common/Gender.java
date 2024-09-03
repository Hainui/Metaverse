package com.metaverse.login.common;

public enum Gender {
        // 定义枚举常量 FEMALE 和 MALE
        FEMALE(0, "女"),
        MALE(1, "男");

        // 成员变量用于存储性别的整数值和描述
        private int value;
        private String description;

        // 构造方法，传入性别的整数值和描述
        Gender(int value, String description) {
            this.value = value;
            this.description = description;
        }

        // 获取性别的整数值
        public int getValue() {
            return value;
        }

        // 获取性别的描述
        public String getDescription() {
            return description;
        }

        // 静态方法，根据整数值反查对应的枚举值
        public static Gender fromValue(int value) {
            for (Gender gender : values()) {
                if (gender.getValue() == value) {
                    return gender;
                }
            }
            throw new IllegalArgumentException("Invalid gender value: " + value);
        }

        // 可选：重写toString方法，使得枚举常量可以直接被转换成字符串
        @Override
        public String toString() {
            return this.description;
        }
    }