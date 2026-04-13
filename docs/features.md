# 功能说明文档

## 1. 角色系统 (Role System)

### 角色类型

| 角色 | 标识 | 权限 |
|------|------|------|
| 普通用户 | `user` | 购物、收藏、拼团等 |
| 商家 | `merchant` | 管理自己的商品和订单 |
| 管理员 | `admin` | 全平台管理权限 |

### 注册时选择角色
- 注册页面提供"普通用户"和"商家"两种选择（默认普通用户）
- 管理员账号只能在数据库中手动设置

### 登录后的跳转
- `admin` → `/admin/products`（管理员后台）
- `merchant` → `/merchant/products`（商家后台）
- `user` → 首页或来源页

### 后端鉴权
- `AuthInterceptor`：验证 token 有效性，适用于所有需登录的接口
- `AdminInterceptor`：验证 role=admin，保护 `/api/admin/**`
- `MerchantInterceptor`：验证 role=merchant 或 role=admin，保护 `/api/merchant/**`

---

## 2. 地址管理与结算集成

### 地址管理
- 用户可在"收货地址"页面（`/addresses`）管理多个收货地址
- 支持设置默认地址（字段 `is_default`）
- 地址包含：姓名、手机号、省/市/区/详细地址

### 购物车结算集成
- 点击"去结算"时自动加载用户已保存的地址
- 在收货信息对话框顶部显示已保存地址列表，点击可一键填充
- 也可手动填写新地址
- 提交订单时，使用正确字段名发送：
  - `address`（完整地址）
  - `receiverName`（收货人姓名）
  - `receiverPhone`（手机号）
  - `cartIds`（购物车条目ID数组，后端用来获取商品信息）

---

## 3. 商家功能 (Merchant Features)

### 商家商品管理
- 访问路径：`/merchant/products`
- 商家只能看到和管理自己发布的商品（通过 `merchant_id` 过滤）
- 功能：新增/编辑/删除/上下架商品

### 商家订单管理
- 访问路径：`/merchant/orders`
- 显示包含商家商品的订单
- 商家可对"已付款"订单执行发货操作（状态 1→2）

### 商品的 merchantId 字段
- `product` 表新增 `merchant_id` 字段
- 商家通过 `/api/merchant/products` 创建的商品会自动关联当前登录商家ID
- 管理员通过 `/api/admin/products` 创建的商品 `merchant_id` 为 null

---

## 4. 拼团功能 (Group Buying)

### 功能流程
1. 用户在商品详情页点击"发起拼团"
2. 填写拼团价格、成团人数（最少2人）、有效时间
3. 系统生成唯一8位分享码
4. 发起人将分享码发给好友
5. 好友在商品详情页点击"加入拼团"，输入分享码加入
6. 当参与人数达到最小成团人数时，拼团状态变为"已成团"

### 拼团状态
| 状态值 | 含义 |
|--------|------|
| 0 | 进行中（可加入） |
| 1 | 已成团（成功） |
| 2 | 已失败 |
| 3 | 已过期 |

### 数据表
- `group_buy`：拼团主表（记录每个拼团活动）
- `group_buy_member`：拼团成员表（记录参与关系）

### API 接口
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/groupbuy/create` | 发起拼团 | 需登录 |
| POST | `/api/groupbuy/join/{shareCode}` | 加入拼团 | 需登录 |
| GET | `/api/groupbuy/{shareCode}` | 获取拼团详情 | 公开 |
| GET | `/api/groupbuy/product/{productId}` | 获取商品拼团列表 | 公开 |
| GET | `/api/groupbuy/my` | 我的拼团记录 | 需登录 |

### 前端页面
- **商品详情页**：显示当前活跃拼团列表，支持发起/加入拼团
- **我的拼团**（`/group-buys`）：查看用户参与的所有拼团，显示进度、分享码等信息
