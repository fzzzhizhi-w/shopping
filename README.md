# 潮流商城 🛒

基于 **Spring Boot 3.3.x + Vue 3 + DeepSeek AI** 的现代化全栈购物商城项目。

## 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.3.4 | 主框架 |
| Java | 17 | 开发语言 |
| MySQL | 8.0 | 数据库 |
| MyBatis-Plus | 3.5.7 | ORM框架（兼容Spring Boot 3.x） |
| JWT (jjwt) | 0.11.5 | Token认证 |
| DeepSeek API | — | AI对话与推荐 |

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.2.x | 前端框架 |
| Vite | 2.9.x | 构建工具（兼容Node 14） |
| Element Plus | 2.2.x | UI组件库 |
| Pinia | 2.x | 状态管理 |
| Vue Router | 4.x | 路由管理 |
| Axios | 0.27.x | HTTP客户端 |

## 项目结构

```
shopping/
├── sql/                        # 数据库脚本
│   ├── schema.sql              # DDL建表语句
│   └── data.sql                # 示例数据
├── shopping-backend/           # Spring Boot后端
│   ├── pom.xml
│   └── src/main/java/com/shopping/
│       ├── ShoppingApplication.java
│       ├── config/             # 配置类（MybatisPlus, JWT, DeepSeek, CORS, Security）
│       ├── controller/         # 控制器（Auth, Product, Category, Cart, Order, Ai, Recommend）
│       ├── service/impl/       # 服务实现
│       ├── mapper/             # MyBatis-Plus Mapper
│       ├── entity/             # 数据库实体
│       ├── dto/                # 请求DTO
│       ├── common/             # 公共响应Result、PageResult
│       ├── interceptor/        # 认证拦截器、用户上下文
│       ├── utils/              # JWT工具、DeepSeek HTTP客户端
│       └── exception/          # 全局异常处理
└── shopping-frontend/          # Vue 3前端
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── api/                # API接口（auth, product, cart, order, ai）
        ├── stores/             # Pinia状态（user, cart）
        ├── router/             # Vue Router路由配置
        ├── components/         # 公共组件（AppHeader, ProductCard）
        └── views/              # 页面（Home, ProductList, ProductDetail, Cart, Orders, AiChat...）
```

## 功能特性

### 商城功能（用户端）
- 🏠 **首页**：轮播图（API驱动，含广告位展示）+ 分类导航 + 热销商品 + 新品推荐
- 🔍 **商品搜索**：关键词搜索、分类筛选、价格排序
- 📦 **商品详情**：图片展示、规格选择、加入购物车、**收藏商品**、**商品评价列表**
- 🛒 **购物车**：增删改、批量结算
- 📋 **订单管理**：创建订单、支付模拟、取消订单、状态跟踪、**订单评价**
- ❤️ **我的收藏**：商品收藏/取消收藏、收藏列表分页管理
- 📜 **浏览历史**：自动记录商品浏览记录、历史列表管理、清空历史
- 📍 **收货地址**：新增/编辑/删除地址、设置默认地址
- 👤 **用户中心**：个人信息、修改密码

### AI功能（DeepSeek）
- 💬 **普通对话**：调用DeepSeek API，问答落库保存
- ⚡ **流式对话**：SSE打字机效果（后端推送delta，前端实时展示）
- 📜 **历史记录**：按会话管理对话历史
- 🎯 **AI推荐**：根据分类和预算智能推荐商品

### 后台管理功能（admin角色）
- 📦 **商品管理**：CRUD、上下架
- 🗂️ **分类管理**：CRUD
- 📋 **订单管理**：查看所有订单、发货、确认收货
- 🖼️ **轮播图管理**：新增/编辑/删除轮播图，排序与状态控制
- 📢 **广告位管理**：多位置广告配置（首页顶部/侧边/商品列表）
- ⭐ **评价管理**：查看所有商品评价

## 快速开始

### 1. 环境要求
- Java 17+
- Maven 3.6+
- Node.js 14+ (推荐16+)
- MySQL 8.0

### 2. 数据库初始化

```bash
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/data.sql
```

> 示例账号：`admin` / `testuser`，密码均为 `admin123`

### 3. 配置后端

编辑 `shopping-backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shopping?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root        # 改为你的数据库用户名
    password: 123456      # 改为你的数据库密码

deepseek:
  api-key: sk-xxxx       # 填入你的DeepSeek API Key
```

或通过环境变量设置 DeepSeek Key：
```bash
export DEEPSEEK_API_KEY=sk-your-key-here
```

### 4. 启动后端

```bash
cd shopping-backend
mvn spring-boot:run
```

后端默认运行在 `http://localhost:8080`

### 5. 启动前端

```bash
cd shopping-frontend
npm install
npm run dev
```

前端默认运行在 `http://localhost:3000`，已配置代理到后端8080。

## API接口文档

### 认证相关（无需Token）
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 用户登录，返回Token |
| POST | `/api/auth/register` | 用户注册 |
| GET | `/api/auth/info` | 获取当前用户信息 |

### 商品相关（无需Token）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/products` | 商品列表（分页、筛选） |
| GET | `/api/products/{id}` | 商品详情 |
| GET | `/api/products/hot` | 热销商品Top10 |
| GET | `/api/products/new` | 最新商品 |
| GET | `/api/products/search?keyword=` | 搜索商品 |
| GET | `/api/categories` | 分类列表 |

### 购物车（需要Token）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/cart` | 查看购物车 |
| POST | `/api/cart/add` | 加入购物车 |
| PUT | `/api/cart/{id}` | 修改数量 |
| DELETE | `/api/cart/{id}` | 删除单项 |
| DELETE | `/api/cart/clear` | 清空购物车 |

### 订单（需要Token）
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/orders` | 创建订单 |
| GET | `/api/orders` | 订单列表 |
| GET | `/api/orders/{id}` | 订单详情 |
| PUT | `/api/orders/{id}/pay` | 模拟支付 |
| PUT | `/api/orders/{id}/cancel` | 取消订单 |

### AI接口（需要Token）
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/ai/chat` | 普通对话（落库） |
| GET | `/api/ai/stream?content=&sessionId=` | 流式SSE对话 |
| GET | `/api/ai/history?sessionId=` | 历史记录 |
| DELETE | `/api/ai/history?sessionId=` | 清除历史 |
| GET | `/api/recommend?category=&budget=` | AI商品推荐 |

### 公开接口（无需Token）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/public/banners` | 活跃轮播图列表 |
| GET | `/api/public/ads?position=` | 广告位列表（可按位置筛选） |

### 收藏（需要Token）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/favorites` | 我的收藏列表 |
| POST | `/api/favorites/{productId}` | 收藏商品 |
| DELETE | `/api/favorites/{productId}` | 取消收藏 |
| GET | `/api/favorites/{productId}/status` | 查询是否已收藏 |

### 浏览历史（需要Token）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/history` | 浏览历史列表 |
| POST | `/api/history/{productId}` | 记录浏览 |
| DELETE | `/api/history/{productId}` | 删除单条记录 |
| DELETE | `/api/history` | 清空浏览历史 |

### 收货地址（需要Token）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/addresses` | 我的地址列表 |
| POST | `/api/addresses` | 新增地址 |
| PUT | `/api/addresses/{id}` | 编辑地址 |
| DELETE | `/api/addresses/{id}` | 删除地址 |
| PUT | `/api/addresses/{id}/default` | 设为默认地址 |

### 订单评价（需要Token / 商品评价列表公开）
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/reviews/orders/{orderId}` | 提交评价 |
| GET | `/api/reviews/orders/{orderId}` | 查看订单评价状态 |
| GET | `/api/reviews/products/{productId}` | 商品评价列表（公开） |

### 后台管理（需要admin Token）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET/POST/PUT/DELETE | `/api/admin/products/**` | 商品CRUD |
| GET/PUT | `/api/admin/orders/**` | 订单管理（发货/收货） |
| GET/POST/PUT/DELETE | `/api/admin/categories/**` | 分类CRUD |
| GET/POST/PUT/DELETE | `/api/admin/banners/**` | 轮播图CRUD |
| GET/POST/PUT/DELETE | `/api/admin/ads/**` | 广告位CRUD |
| GET | `/api/admin/reviews` | 评价列表查询 |

### Token使用方式

登录后获取token，在后续请求头中携带：
```
Token: eyJhbGciOiJIUzI1NiJ9...
```

### 流式对话前端调用示例

```javascript
const response = await fetch(`/api/ai/stream?content=${encodeURIComponent(msg)}&sessionId=${sessionId}`, {
  headers: { 'Token': localStorage.getItem('token') }
})
const reader = response.body.getReader()
const decoder = new TextDecoder()
while (true) {
  const { done, value } = await reader.read()
  if (done) break
  const chunk = decoder.decode(value)
  // 解析SSE格式: "data: {...}\n\n"
  const lines = chunk.split('\n').filter(l => l.startsWith('data: '))
  for (const line of lines) {
    const json = line.slice(6)
    if (json === '[DONE]') break
    const delta = JSON.parse(json)?.choices?.[0]?.delta?.content || ''
    // 追加到消息文本
  }
}
```

## 数据库表结构

| 表名 | 说明 |
|------|------|
| `user` | 用户信息（支持逻辑删除，role字段区分用户/管理员） |
| `category` | 商品分类 |
| `product` | 商品信息 |
| `cart` | 购物车 |
| `order` | 订单主表（支持逻辑删除） |
| `order_item` | 订单明细 |
| `chat_history` | AI对话历史 |
| `product_favorite` | 商品收藏（user_id+product_id唯一索引） |
| `browse_history` | 浏览历史（user_id+product_id唯一索引，记录最新浏览时间） |
| `address` | 用户收货地址（支持多地址+默认标记） |
| `order_review` | 订单评价（order_item_id唯一索引，防重复评价） |
| `banner` | 轮播图（status启用/停用，sort排序） |
| `ad` | 广告位（position区分首页顶部/侧边/商品列表等） |

## 安全说明

- 密码使用 **BCrypt** 加密存储
- Token 通过 **HMAC-SHA256** 签名，默认有效期24小时，Payload包含userId与role
- AI接口、购物车、订单接口均需携带 `Token` 请求头
- 商品浏览、分类、公开Banner/广告接口对外公开无需认证
- 后台管理接口（`/api/admin/**`）由AdminInterceptor强制校验role=admin
- 收藏/浏览历史/地址/评价接口均做用户数据隔离（userId匹配校验）
- CORS 已配置，支持前端跨域请求

## 开发说明

**DeepSeek API Key 获取**：访问 [platform.deepseek.com](https://platform.deepseek.com) 注册并获取API Key。

**修改数据库配置**：`shopping-backend/src/main/resources/application.yml`

**生产构建前端**：
```bash
cd shopping-frontend && npm run build
# 产物在 dist/ 目录，可部署到 Nginx
```
