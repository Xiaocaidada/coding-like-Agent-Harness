# 本地文创市集摊位管理系统

## 项目概述
这是一个基于SpringBoot3 + MyBatis-Plus + MySQL开发的本地文创市集摊位管理系统后端，专为小型线下文创市集活动管理设计。

## 技术栈
- SpringBoot3
- MyBatis-Plus
- MySQL8
- Lombok
- Validation参数校验
- JWT登录鉴权

## 核心业务模块
1. **活动发布管理** - 市集活动的增删改查
2. **摊位管理** - 摊位信息维护与分配
3. **摊主报名模块** - 摊主报名审核
4. **市集订单模块** - 摊位租赁订单管理
5. **客流统计模块** - 客流数据录入与统计
6. **用户与权限** - 管理员和摊主角色管理

## 目录结构
```
my_project/
├── src/main/java/com/craftmarket/
│   ├── config/          # 配置类
│   ├── controller/      # 控制器层
│   ├── dto/            # 数据传输对象
│   ├── entity/         # 实体类
│   ├── enums/          # 枚举类
│   ├── mapper/         # 数据访问层
│   ├── service/        # 服务层
│   └── utils/          # 工具类
├── src/main/resources/
│   ├── mapper/         # MyBatis映射文件
│   └── application.yml # 配置文件
├── src/test/java/      # 单元测试
└── sql/               # 数据库脚本
```

## 启动方式
1. 确保MySQL数据库运行，执行sql目录中的建表脚本
2. 配置数据库连接信息（使用环境变量）
3. 运行主程序：CraftMarketApplication.java

## 接口说明
项目采用RESTful风格接口，提供完整的CRUD功能：
- 活动管理接口
- 摊位管理接口
- 摊主报名接口
- 订单管理接口
- 客流统计接口
- 用户认证接口